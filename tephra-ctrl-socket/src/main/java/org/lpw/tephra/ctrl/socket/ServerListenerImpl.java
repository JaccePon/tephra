package org.lpw.tephra.ctrl.socket;

import org.lpw.tephra.nio.ServerListener;
import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.socket.server-listener")
public class ServerListenerImpl implements ServerListener {
    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public int getMaxThread() {
        return 0;
    }

    @Override
    public void accept(String sessionId) {
    }

    @Override
    public void receive(String sessionId, byte[] message) {
    }

    @Override
    public void disconnect(String sessionId) {
    }
}
