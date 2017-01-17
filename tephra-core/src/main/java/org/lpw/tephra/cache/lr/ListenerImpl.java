package org.lpw.tephra.cache.lr;

import org.lpw.tephra.nio.NioHelper;
import org.lpw.tephra.nio.ServerListener;
import org.lpw.tephra.util.Serializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Component("tephra.cache.lr.listener")
public class ListenerImpl implements ServerListener {
    @Inject
    private Serializer serializer;
    @Inject
    private NioHelper nioHelper;
    @Inject
    private Local local;
    @Inject
    private Remote remote;
    @Value("${tephra.cache.listen-port:0}")
    private int port;
    @Value("${tephra.cache.listener.max-thread:5}")
    private int maxThread;

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getMaxThread() {
        return maxThread;
    }

    @Override
    public void accept(String sessionId) {
        nioHelper.send(sessionId, remote.getId().getBytes());
    }

    @Override
    public void receive(String sessionId, byte[] message) {
        if (message == null)
            return;

        Object object = serializer.unserialize(message);
        if (object == null)
            return;

        if (object instanceof String)
            local.remove((String) object);
        else if (object instanceof Element)
            local.put((Element) object);
    }

    @Override
    public void disconnect(String sessionId) {
    }
}
