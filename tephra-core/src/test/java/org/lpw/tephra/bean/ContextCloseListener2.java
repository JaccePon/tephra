package org.lpw.tephra.bean;

import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.bean.context-close-listener.2")
public class ContextCloseListener2 implements ContextClosedListener {
    @Override
    public int getContextClosedSort() {
        return 2;
    }

    @Override
    public void onContextClosed() {
        ContainerTest.runContextCloseListener(getContextClosedSort());
    }
}
