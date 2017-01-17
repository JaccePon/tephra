package org.lpw.tephra.scheduler;

import io.netty.util.internal.ConcurrentSet;
import org.lpw.tephra.atomic.Closable;
import org.lpw.tephra.atomic.Failable;
import org.lpw.tephra.bean.ContextClosedListener;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Thread;
import org.lpw.tephra.util.TimeUnit;
import org.lpw.tephra.util.Validator;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定时器支持类。
 *
 * @author lpw
 */
public abstract class SchedulerSupport<T> implements ContextRefreshedListener, ContextClosedListener {
    @Inject
    protected Validator validator;
    @Inject
    protected Thread thread;
    @Inject
    protected Logger logger;
    @Inject
    protected Optional<Set<Failable>> failables;
    @Inject
    protected Optional<Set<Closable>> closables;
    protected Set<Integer> runningJobs;
    protected ExecutorService executorService;

    /**
     * 添加到执行线程池中。
     *
     * @param job 要执行的任务实例。
     */
    protected void pool(T job) {
        while (runningJobs == null)
            thread.sleep(100, TimeUnit.MilliSecond);

        if (isRunning(job))
            return;

        executorService.submit(() -> {
            begin(job);

            try {
                execute(job);
            } catch (Exception e) {
                exception(e);
            }

            finish(job);
        });
    }

    /**
     * 判断指定Job是否正在运行。
     *
     * @param job 要验证的Job对象。
     * @return 如果正在运行则返回true；否则返回false。
     */
    protected boolean isRunning(T job) {
        return job != null && runningJobs.contains(job.hashCode());
    }

    /**
     * 将Job设置为正在运行。
     *
     * @param job 要设置的Job对象。
     */
    protected void begin(T job) {
        if (job != null)
            runningJobs.add(job.hashCode());
    }

    /**
     * 执行任务。
     *
     * @param job 要执行的任务实例。
     */
    protected abstract void execute(T job);

    /**
     * 处理执行异常。
     *
     * @param throwable 异常信息。
     */
    protected void exception(Throwable throwable) {
        failables.ifPresent(set -> set.forEach(failable -> failable.fail(throwable)));

        logger.warn(throwable, "执行定时器任务时发生异常！");
    }

    /**
     * 设置Job对象运行结束。
     *
     * @param job 要设置的Job对象。
     */
    protected void finish(T job) {
        if (job != null)
            runningJobs.remove(job.hashCode());

        closables.ifPresent(set -> set.forEach(Closable::close));
    }

    @Override
    public int getContextRefreshedSort() {
        return 4;
    }

    @Override
    public void onContextRefreshed() {
        runningJobs = new ConcurrentSet<>();
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public int getContextClosedSort() {
        return 4;
    }

    @Override
    public void onContextClosed() {
        executorService.shutdownNow();
    }
}
