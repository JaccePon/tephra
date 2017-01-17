package org.lpw.tephra.nio;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextClosedListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lpw
 */
@Component("tephra.nio.client-manager")
public class ClientManagerImpl implements ClientManager, ContextClosedListener {
    protected Set<Client> clients = Collections.synchronizedSet(new HashSet<>());

    @Override
    public Client get() {
        Client client = BeanFactory.getBean(Client.class);
        clients.add(client);

        return client;
    }

    @Override
    public void close() {
        if (clients.isEmpty())
            return;

        clients.forEach(Client::close);
    }

    @Override
    public int getContextClosedSort() {
        return 5;
    }

    @Override
    public void onContextClosed() {
        close();
    }
}
