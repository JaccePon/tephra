package org.lpw.tephra.nio;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextClosedListener;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.util.Validator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.nio.server-manager")
public class ServerManagerImpl implements ServerManager, ContextRefreshedListener, ContextClosedListener {
    @Inject
    private Validator validator;
    @Inject
    private Optional<Set<ServerListener>> listeners;
    private Set<Server> servers;

    @Override
    public int getContextRefreshedSort() {
        return 6;
    }

    @Override
    public void onContextRefreshed() {
        if (!listeners.isPresent() || !validator.isEmpty(servers))
            return;

        servers = Collections.synchronizedSet(new HashSet<>());
        listeners.get().forEach(listener -> {
            Server server = BeanFactory.getBean(Server.class);
            server.listen(listener);
            servers.add(server);
        });
    }

    @Override
    public int getContextClosedSort() {
        return 6;
    }

    @Override
    public void onContextClosed() {
        stop();
    }

    @Override
    public void stop() {
        if (validator.isEmpty(servers))
            return;

        servers.forEach(Server::close);
    }
}
