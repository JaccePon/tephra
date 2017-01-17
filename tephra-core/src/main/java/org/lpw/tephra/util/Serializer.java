package org.lpw.tephra.util;

/**
 * 序列化。
 *
 * @author lpw
 */
public interface Serializer {
    /**
     * 将对象序列化为byte数据。
     *
     * @param object 要进行序列化的对象。
     * @return byte数据；如果序列化失败则返回null。
     */
    byte[] serialize(Object object);

    /**
     * 将byte数据反序列化为对象。
     *
     * @param bytes byte数据。
     * @return 对象；如果反序列化失败则返回null。
     */
    <T> T unserialize(byte[] bytes);
}
