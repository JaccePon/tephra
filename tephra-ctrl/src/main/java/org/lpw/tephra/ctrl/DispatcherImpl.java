package org.lpw.tephra.ctrl;

import org.lpw.tephra.atomic.Closable;
import org.lpw.tephra.atomic.Failable;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.ctrl.console.Console;
import org.lpw.tephra.ctrl.context.Header;
import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.context.Response;
import org.lpw.tephra.ctrl.execute.ExecuteInvocation;
import org.lpw.tephra.ctrl.execute.ExecutorHelper;
import org.lpw.tephra.ctrl.status.Status;
import org.lpw.tephra.ctrl.validate.Validators;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.dispatcher")
public class DispatcherImpl implements Dispatcher, Forward, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Inject
    private Set<Failable> failables;
    @Inject
    private Set<Closable> closables;
    @Inject
    private Header header;
    @Inject
    private Request request;
    @Inject
    private Response response;
    @Inject
    private Counter counter;
    @Inject
    private Status status;
    @Inject
    private Console console;
    @Inject
    private Validators validators;
    @Inject
    private Optional<List<Interceptor>> interceptorsOptional;
    @Inject
    private ExecutorHelper executorHelper;
    @Inject
    private Optional<Permit> permit;
    private ThreadLocal<Long> time = new ThreadLocal<>();
    private ThreadLocal<Map<String, Object>> parameters = new ThreadLocal<>();
    private List<Interceptor> interceptors;

    @Override
    public void execute() {
        time.set(System.currentTimeMillis());
        String uri = request.getUri();
        if (logger.isDebugEnable())
            logger.debug("开始处理请求[{}:{}:{}]。", uri, converter.toString(request.getMap()), converter.toString(header.getMap()));

        boolean statusService = status.isStatus(uri);
        String ip = header.getIp();
        if (!counter.increase(ip) && !statusService) {
            counter.decrease(ip);
            response.write(Failure.Busy);

            return;
        }

        boolean consoleService = console.isConsole(uri);
        if (!statusService && !consoleService)
            executorHelper.set(uri);
        if (!statusService && !consoleService && executorHelper.get() == null) {
            counter.decrease(ip);
            response.write(Failure.Exception);

            logger.warn(null, "无法获得请求[{}]的处理服务！", uri);

            return;
        }

        execute(statusService, consoleService);
        closables.forEach(Closable::close);
        counter.decrease(ip);

        if (logger.isDebugEnable())
            logger.debug("处理请求[{}]完成，耗时[{}]毫秒。", uri, System.currentTimeMillis() - time.get());
    }

    private void execute(boolean statusService, boolean consoleService) {
        Object object;
        if (statusService)
            object = status.execute(counter.get());
        else if (consoleService)
            object = console.execute();
        else
            object = exe();
        response.write(object);
    }

    @Override
    public Object redirect(String uri) {
        if (logger.isDebugEnable())
            logger.debug("跳转到：{}。", uri);

        executorHelper.set(uri);

        return exe();
    }

    private Object exe() {
        if (permit.isPresent() && !permit.get().allow())
            return Failure.NotPermit;

        try {
            return new ExecuteInvocation(interceptors, validators, executorHelper.get()).invoke();
        } catch (Throwable e) {
            failables.forEach(failable -> failable.fail(e));
            logger.warn(e, "执行请求[{}:{}]时发生异常！", request.getUri(), executorHelper.get().getMethod());

            return Failure.Exception;
        }
    }

    @Override
    public Object redirect(String uri, Map<String, Object> parameters) {
        this.parameters.set(parameters);

        return redirect(uri);
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public <T> T getParameter(String name) {
        Map<String, Object> parameters = getParameters();
        if (validator.isEmpty(parameters))
            return null;

        return (T) parameters.get(name);
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters.get();
    }

    @Override
    public void redirectTo(String url) {
        response.redirectTo(url);
    }

    @Override
    public int getContextRefreshedSort() {
        return 8;
    }

    @Override
    public void onContextRefreshed() {
        interceptors = interceptorsOptional.isPresent() ? interceptorsOptional.get() : new ArrayList<>();
        interceptors.sort(Comparator.comparingInt(Interceptor::getSort));
    }
}
