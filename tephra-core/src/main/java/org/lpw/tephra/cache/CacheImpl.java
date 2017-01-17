package org.lpw.tephra.cache;

import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.cache")
public class CacheImpl implements Cache, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private Set<Handler> handlers;
    @Value("${tephra.cache.name:}")
    private String name;
    private Handler handler;

    @Override
    public void put(String key, Object value, boolean resident) {
        if (validator.isEmpty(key) || value == null)
            return;

        handler.put(key, value, resident);
    }

    @Override
    public <T> T get(String key) {
        if (validator.isEmpty(key))
            return null;

        return handler.get(key);
    }

    @Override
    public <T> T remove(String key) {
        if (validator.isEmpty(key))
            return null;

        return handler.remove(key);
    }

    @Override
    public int getContextRefreshedSort() {
        return 5;
    }

    @Override
    public void onContextRefreshed() {
        if (logger.isDebugEnable())
            logger.debug("使用[{}]缓存处理器。", name);

        for (Handler handler : handlers) {
            if (handler.getName().equals(name)) {
                this.handler = handler;

                break;
            }
        }

        if (handler == null)
            logger.warn(null, "无法获得缓存处理器[{}]。", name);
    }
}
