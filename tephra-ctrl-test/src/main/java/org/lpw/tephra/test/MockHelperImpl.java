package org.lpw.tephra.test;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.lpw.tephra.ctrl.Dispatcher;
import org.lpw.tephra.ctrl.context.HeaderAware;
import org.lpw.tephra.ctrl.context.RequestAware;
import org.lpw.tephra.ctrl.context.ResponseAware;
import org.lpw.tephra.ctrl.context.SessionAware;
import org.lpw.tephra.util.Generator;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Aspect
@Controller("tephra.test.mock.helper")
public class MockHelperImpl implements MockHelper {
    @Inject
    private Generator generator;
    @Inject
    private HeaderAware headerAware;
    @Inject
    private SessionAware sessionAware;
    @Inject
    private RequestAware requestAware;
    @Inject
    private ResponseAware responseAware;
    @Inject
    private Dispatcher dispatcher;
    private ThreadLocal<MockHeader> header = new ThreadLocal<>();
    private ThreadLocal<MockSession> session = new ThreadLocal<>();
    private ThreadLocal<MockRequest> request = new ThreadLocal<>();
    private ThreadLocal<MockResponse> response = new ThreadLocal<>();
    private ThreadLocal<MockFreemarker> freemarker = new ThreadLocal<>();

    @Override
    public MockHeader getHeader() {
        if (header.get() == null)
            header.set(new MockHeaderImpl());

        return header.get();
    }

    @Override
    public MockSession getSession() {
        MockSession mockSession = session.get();
        if (mockSession == null) {
            mockSession = new MockSessionImpl();
            mockSession.setId(generator.random(32));
            session.set(mockSession);
        }

        return mockSession;
    }

    @Override
    public MockRequest getRequest() {
        if (request.get() == null)
            request.set(new MockRequestImpl());

        return request.get();
    }

    @Override
    public MockResponse getResponse() {
        if (response.get() == null)
            response.set(new MockResponseImpl());

        return response.get();
    }

    @Override
    public MockFreemarker getFreemarker() {
        return freemarker.get();
    }

    @Around("target(org.lpw.tephra.freemarker.Freemarker)")
    public Object process(ProceedingJoinPoint point) throws Throwable {
        if (getFreemarker() != null) {
            getFreemarker().process((String) point.getArgs()[0], point.getArgs()[1], null);

            return null;
        }

        return point.proceed();
    }

    @Override
    public void reset() {
        header.remove();
        session.remove();
        request.remove();
        response.remove();
        freemarker.remove();
    }

    @Override
    public void mock(String uri) {
        mock(uri, false);
    }

    @Override
    public void mock(String uri, boolean freemarker) {
        headerAware.set(getHeader());
        sessionAware.set(getSession());
        getRequest().setUri(uri);
        requestAware.set(getRequest());
        responseAware.set(getResponse());
        if (freemarker)
            this.freemarker.set(new MockFreemarkerImpl());
        dispatcher.execute();
    }
}
