package org.lpw.tephra.test;

import java.util.Map;

/**
 * @author lpw
 */
public interface MockCarouselService {
    /**
     * 执行服务。
     *
     * @param key       服务key。
     * @param header    头信息。
     * @param parameter 请求参数。
     * @param cacheTime 缓存时长，0表示不缓存，单位：分钟。
     * @return 执行结果。
     */
    String service(String key, Map<String, String> header, Map<String, String> parameter, int cacheTime);
}
