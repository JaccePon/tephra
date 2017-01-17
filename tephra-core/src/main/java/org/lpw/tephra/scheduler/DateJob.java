package org.lpw.tephra.scheduler;

/**
 * 每日执行定时器任务，每日执行一次任务。
 *
 * @author lpw
 */
public interface DateJob {
    /**
     * 执行每日任务。
     */
    void executeDateJob();
}
