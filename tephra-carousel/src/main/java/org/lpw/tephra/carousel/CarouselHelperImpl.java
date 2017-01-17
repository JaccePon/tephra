package org.lpw.tephra.carousel;

import net.sf.json.JSONObject;
import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextClosedListener;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.cache.Cache;
import org.lpw.tephra.ctrl.context.Header;
import org.lpw.tephra.ctrl.context.Session;
import org.lpw.tephra.ctrl.execute.Execute;
import org.lpw.tephra.ctrl.execute.ExecuteListener;
import org.lpw.tephra.ctrl.status.Status;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Http;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.TimeUnit;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author lpw
 */
@Component("tephra.carousel.helper")
public class CarouselHelperImpl implements CarouselHelper, ExecuteListener, ContextRefreshedListener, ContextClosedListener {
    private static final String CACHE_SERVICE = "tephra.carousel.helper.service:";

    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Http http;
    @Inject
    private Logger logger;
    @Inject
    private Cache cache;
    @Inject
    private Status status;
    @Inject
    private Header header;
    @Inject
    private Session session;
    @Inject
    private Optional<Set<CarouselRegister>> registers;
    @Value("${tephra.carousel.url:}")
    private String carouselUrl;
    @Value("${tephra.ctrl.service-root:}")
    private String serviceUrl;
    @Value("${tephra.carousel.service.all:false}")
    private boolean serviceAll;
    private boolean emptyCarouselUrl;
    private boolean emptyServiceUrl;
    private Map<String, String> services = new ConcurrentHashMap<>();
    private ExecutorService executorService;

    @Override
    public boolean config(String name, String description, ActionBuilder actionBuilder) {
        return config(name, description, actionBuilder, 0, 0, 0, false);
    }

    @Override
    public boolean config(String name, String description, ActionBuilder actionBuilder, int delay, int interval, int times, boolean wait) {
        if (emptyCarouselUrl || validator.isEmpty(name) || actionBuilder == null || validator.isEmpty(carouselUrl))
            return false;

        JSONObject object = new JSONObject();
        object.put("name", name);
        object.put("description", description);
        object.put("actions", actionBuilder.get());
        if (delay > 0)
            object.put("delay", delay);
        if (interval > 0)
            object.put("interval", interval);
        if (times > 0)
            object.put("times", times);
        if (wait)
            object.put("wait", 1);

        return code0(http.post(carouselUrl + "/config/update", null, object.toString()));
    }

    @Override
    public String process(String name, int delay, Map<String, String> header, String data) {
        if (emptyCarouselUrl)
            return null;

        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        if (delay > 0)
            map.put("delay", "" + delay);
        map.put("data", data);

        return http.post(carouselUrl + "/process/execute", header, map);
    }

    @Override
    public boolean register(String key, String service) {
        services.put(key, service);
        if (emptyCarouselUrl || emptyServiceUrl)
            return false;

        Map<String, String> map = new HashMap<>();
        map.put("key", key);
        map.put("service", serviceUrl + service);
        map.put("validate", serviceUrl + status.getUri());
        map.put("validate", "^\\{\"code\":0,.*");

        return code0(http.post(carouselUrl + "/discovery/register", null, map));
    }

    @Override
    public String service(String key, Map<String, String> header, Map<String, String> parameter) {
        return service(key, header, parameter, 0);
    }

    @Override
    public String service(String key, Map<String, String> header, Map<String, String> parameter, int cacheTime) {
        String cacheKey = null;
        boolean cacheable = cacheTime > 0;
        if (cacheable) {
            cacheKey = CACHE_SERVICE + key + converter.toString(header) + converter.toString(parameter)
                    + (System.currentTimeMillis() / cacheTime / TimeUnit.Minute.getTime());
            String string = cache.get(cacheKey);
            if (string != null)
                return string;
        }

        if (services.containsKey(key)) {
            try {
                Future<String> future = executorService.submit(BeanFactory.getBean(LocalService.class)
                        .build(services.get(key), this.header.getIp(), session.getId(), header, parameter));

                return cacheService(cacheable, cacheKey, future.get());
            } catch (Exception e) {
                logger.warn(e, "执行本地服务[{}:{}]时发生异常！", key, services.get(key));

                return null;
            }
        }

        if (emptyCarouselUrl)
            return null;

        if (header == null)
            header = new HashMap<>();
        header.put("carousel-ds-key", key);

        return cacheService(cacheable, cacheKey, http.post(carouselUrl + "/discovery/execute", header, parameter));
    }

    private String cacheService(boolean cacheable, String cacheKey, String result) {
        if (cacheable)
            cache.put(cacheKey, result, false);

        return result;
    }

    private boolean code0(String string) {
        if (validator.isEmpty(string))
            return false;

        try {
            return JSONObject.fromObject(string).getInt("code") == 0;
        } catch (Throwable e) {
            logger.warn(e, "解析JSON数据时发生异常！", string);

            return false;
        }
    }

    @Override
    public void definition(Execute classExecute, Execute methodExecute) {
        if (!serviceAll)
            return;

        for (String name : converter.toArray(methodExecute.name(), ","))
            services.put(classExecute.key() + "." + name, classExecute.name() + name);
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        emptyCarouselUrl = validator.isEmpty(carouselUrl);
        emptyServiceUrl = validator.isEmpty(serviceUrl);
        registers.ifPresent(set -> set.forEach(register -> services.putAll(register.getKeyService())));
        services.forEach(this::register);
        if (executorService == null)
            executorService = Executors.newCachedThreadPool();

        if (!services.isEmpty() && logger.isInfoEnable())
            logger.info("本地发现服务[{}:{}]发布完成。", services.size(), converter.toString(services));
    }

    @Override
    public int getContextClosedSort() {
        return 9;
    }

    @Override
    public void onContextClosed() {
        if (executorService != null)
            executorService.shutdown();
    }
}
