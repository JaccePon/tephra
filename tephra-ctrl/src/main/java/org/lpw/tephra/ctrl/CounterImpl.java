package org.lpw.tephra.ctrl;

import org.lpw.tephra.cache.Cache;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.counter")
public class CounterImpl implements Counter {
    private static final String CACHE_DELAY = "tephra.ctrl.counter.delay:";

    @Inject
    private Cache cache;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Inject
    private TrustfulIp trustfulIp;
    @Value("${tephra.ctrl.counter.max:512}")
    private int max;
    @Value("${tephra.ctrl.counter.ip-max:5}")
    private int ipMax;
    @Value("${tephra.ctrl.counter.ip-delay:5000}")
    private int delay;
    private AtomicInteger counter = new AtomicInteger();
    private Map<String, Integer> ipMap = new HashMap<>();

    @Override
    public boolean increase(String ip) {
        if (counter.incrementAndGet() >= max) {
            logger.warn(null, "超过最大并发处理数[{}]。", max);

            return false;
        }

        if (trustfulIp.contains(ip))
            return true;

        String key = CACHE_DELAY + ip;
        Long time = cache.get(key);
        if (time != null && System.currentTimeMillis() - time < delay)
            return false;

        int n = converter.toInt(ipMap.get(ip)) + 1;
        if (n > ipMax) {
            cache.put(key, System.currentTimeMillis(), false);

            logger.warn(null, "超过IP[{}]最大并发处理数[{}]。", ip, ipMax);

            return false;
        }

        ipMap.put(ip, n);

        return true;
    }

    @Override
    public int get() {
        return counter.get();
    }

    @Override
    public void decrease(String ip) {
        counter.decrementAndGet();

        if (trustfulIp.contains(ip))
            return;

        int n = converter.toInt(ipMap.get(ip));
        if (n < 2)
            ipMap.remove(ip);
        else
            ipMap.put(ip, n - 1);
    }
}
