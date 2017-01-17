package org.lpw.tephra.bean;

import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.bean.context-close-listener.1")
public class ContextCloseListener1 implements ContextClosedListener {
    @Override
    public int getContextClosedSort() {
        return 1;
    }

    @Override
    public void onContextClosed() {
        ContainerTest.runContextCloseListener(getContextClosedSort());
    }
}
