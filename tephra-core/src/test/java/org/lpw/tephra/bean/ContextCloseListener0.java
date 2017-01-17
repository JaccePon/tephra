package org.lpw.tephra.bean;

import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.bean.context-close-listener.0")
public class ContextCloseListener0 implements ContextClosedListener {
    @Override
    public int getContextClosedSort() {
        return 1;
    }

    @Override
    public void onContextClosed() {
        throw new NullPointerException();
    }
}
