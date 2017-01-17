package org.lpw.tephra.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Aspect
@Component("tephra.test.mock.carousel")
public class MockCarouselImpl implements MockCarousel {
    private Map<String, MockCarouselService> services;
    private Map<String, String> results;

    @Override
    public void reset() {
        services = new HashMap<>();
        results = new HashMap<>();
    }

    @Override
    public void register(String key, MockCarouselService service) {
        services.put(key, service);
    }

    @Override
    public void register(String key, String result) {
        results.put(key, result);
    }

    @SuppressWarnings({"unchecked"})
    @Around("target(org.lpw.tephra.carousel.CarouselHelper)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        if (point.getSignature().getName().equals("service")) {
            String key = (String) point.getArgs()[0];
            if (services.containsKey(key))
                return services.get(key).service(key, (Map<String, String>) point.getArgs()[1], (Map<String, String>) point.getArgs()[2], (Integer) point.getArgs()[3]);

            if (results.containsKey(key))
                return results.get(key);
        }

        return point.proceed();
    }
}
