package org.lpw.tephra.scheduler;

/**
 * 每小时执行定时器任务，每小时执行一次任务。
 *
 * @author lpw
 */
public interface HourJob {
    /**
     * 执行每小时任务。
     */
    void executeHourJob();
}
