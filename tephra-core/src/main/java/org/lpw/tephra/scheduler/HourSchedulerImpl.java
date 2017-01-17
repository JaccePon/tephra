package org.lpw.tephra.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.scheduler.hour")
public class HourSchedulerImpl extends SchedulerSupport<HourJob> implements HourScheduler {
    @Inject
    protected Optional<Set<HourJob>> jobs;

    @Scheduled(cron = "${tephra.scheduler.hour.cron:30 0 * * * ?}")
    @Override
    public synchronized void execute() {
        if (!jobs.isPresent())
            return;

        if (logger.isDebugEnable())
            logger.debug("开始执行每小时定时器调度。。。");

        jobs.get().forEach(this::pool);

        if (logger.isDebugEnable())
            logger.debug("成功执行{}个每小时定时器任务！", jobs.get().size());
    }

    @Override
    protected void execute(HourJob job) {
        job.executeHourJob();
    }
}
