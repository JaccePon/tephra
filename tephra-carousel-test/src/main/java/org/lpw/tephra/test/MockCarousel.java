package org.lpw.tephra.test;

/**
 * @author lpw
 */
public interface MockCarousel {
    /**
     * 重置Mock环境。
     */
    void reset();

    /**
     * 注册服务。
     *
     * @param key     服务key。
     * @param service Mock服务。
     */
    void register(String key, MockCarouselService service);

    /**
     * 注册服务结果。
     *
     * @param key    服务key。
     * @param result Mock服务结果。
     */
    void register(String key, String result);
}
