package org.lpw.tephra.util;

/**
 * 时间单位。
 *
 * @author lpw
 */
public enum TimeUnit {
    /**
     * 毫秒。
     */
    MilliSecond(1),
    /**
     * 秒。
     */
    Second(1000),
    /**
     * 分钟。
     */
    Minute(60 * 1000),
    /**
     * 小时。
     */
    Hour(60 * 60 * 1000),
    /**
     * 天。
     */
    Day(24 * 60 * 60 * 1000);

    int time;

    TimeUnit(int time) {
        this.time = time;
    }

    /**
     * 获取时间值。
     *
     * @return 时间值。
     */
    public int getTime() {
        return time;
    }
}
