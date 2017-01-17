package org.lpw.tephra.bean;

import org.springframework.stereotype.Component;

/**
 * @author lpw
 */
@Component("tephra.bean.context-refresh-listener.2")
public class ContextRefreshListener2 implements ContextRefreshedListener {
    @Override
    public int getContextRefreshedSort() {
        return 2;
    }

    @Override
    public void onContextRefreshed() {
        ContainerTest.runContextRefreshListener(getContextRefreshedSort());
    }
}
