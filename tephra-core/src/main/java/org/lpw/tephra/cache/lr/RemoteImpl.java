package org.lpw.tephra.cache.lr;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.bean.ContextClosedListener;
import org.lpw.tephra.nio.NioHelper;
import org.lpw.tephra.scheduler.MinuteJob;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Serializer;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author lpw
 */
@Component("tephra.cache.lr.remote")
public class RemoteImpl implements Remote, MinuteJob, ContextClosedListener {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Generator generator;
    @Inject
    private Serializer serializer;
    @Inject
    private NioHelper nioHelper;
    @Value("${tephra.cache.remote.ips:}")
    private String ips;
    @Value("${tephra.cache.remote.max-thread:5}")
    private int maxThread;
    private String id;
    private Set<Channel> channels;
    private ExecutorService executorService;

    @Override
    public String getId() {
        if (id == null)
            id = generator.uuid();

        return id;
    }

    @Override
    public void put(Element element) {
        write(element);
    }

    @Override
    public void remove(String key) {
        write(key);
    }

    private void write(Object object) {
        if (validator.isEmpty(channels))
            return;

        channels.stream().filter((channel) -> channel.getState() == Channel.State.Connected)
                .forEach((channel) -> executorService.execute(() -> nioHelper.send(channel.getSessionId(), serializer.serialize(object))));
    }

    @Override
    public void executeMinuteJob() {
        if (validator.isEmpty(ips))
            return;

        if (channels == null) {
            int indexOf = ips.lastIndexOf('.') + 1;
            String prefix = ips.substring(0, indexOf);
            String suffix = ips.substring(indexOf);
            int[] range = new int[2];
            indexOf = suffix.indexOf('-');
            if (indexOf == -1) {
                range[0] = converter.toInt(suffix);
                range[1] = range[0];
            } else {
                range[0] = converter.toInt(suffix.substring(0, indexOf));
                range[1] = converter.toInt(suffix.substring(indexOf + 1));
            }

            channels = new HashSet<>();
            for (int i = range[0]; i <= range[1]; i++) {
                Channel channel = BeanFactory.getBean(Channel.class);
                channel.setIp(prefix + i);
                channels.add(channel);
            }
        }

        if (channels.isEmpty())
            return;

        if (executorService == null)
            executorService = Executors.newFixedThreadPool(maxThread);

        channels.stream().filter((channel) -> channel.getState() == Channel.State.Disconnect)
                .forEach((channel) -> executorService.execute(channel::connect));
    }

    @Override
    public int getContextClosedSort() {
        return 5;
    }

    @Override
    public void onContextClosed() {
        if (executorService != null)
            executorService.shutdown();
    }
}
