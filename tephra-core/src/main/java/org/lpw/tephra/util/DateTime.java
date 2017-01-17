package org.lpw.tephra.util;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 日期时间工具。
 *
 * @author lpw
 */
public interface DateTime {
    /**
     * 获取当前时间。
     *
     * @return 当前时间。
     */
    Timestamp now();

    /**
     * 获取指定日期当天的开始时间，即当天的00:00:00.000。
     *
     * @param date 目标日期。
     * @return 开始时间；如果获取失败则返回null。
     */
    Date getStart(Date date);

    /**
     * 获取指定日期当天的结束日期，即当天的23:59:59.999。
     *
     * @param date 目标日期。
     * @return 结束时间；如果获取失败则返回null。
     */
    Date getEnd(Date date);

    /**
     * 将日期时间字符串转化为时间戳。
     *
     * @param string 字符串。
     * @return 时间戳；如果转换失败则返回null。
     */
    Timestamp toTime(String string);

    /**
     * 将日期时间字符串转化为时间戳。
     *
     * @param string  字符串。
     * @param pattern 日期时间格式。
     * @return 时间戳；如果转换失败则返回null。
     */
    Timestamp toTime(String string, String pattern);

    /**
     * 比较两个日期时间值的大小。
     *
     * @param x 日期时间值参数。
     * @param y 日期时间值参数。
     * @return 如果相等则返回0；如果x>y则返回正数；如果x<y则返回负数。
     */
    int compare(Object x, Object y);
}
