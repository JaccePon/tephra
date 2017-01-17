package org.lpw.tephra.bean;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author lpw
 */
public class ContainerTest extends CoreTestSupport {
    private static List<Integer> contextRefreshedList = new ArrayList<>();
    private static List<Integer> contextClosedList = new ArrayList<>();

    @Inject
    private Container container;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;

    @Test
    public void getBeanByClass() {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 10; i++)
            set.add(container.getBean(SingletonBean.class).hashCode());
        Assert.assertEquals(1, set.size());

        set = new HashSet<>();
        for (int i = 0; i < 10; i++)
            set.add(container.getBean(PrototypeBean.class).hashCode());
        Assert.assertEquals(10, set.size());

        Assert.assertNull(container.getBean(NullBean.class));
    }

    @Test
    public void getBeanByName() {
        Assert.assertNull(container.getBean(""));
        Assert.assertNull(container.getBean("abc"));
        Assert.assertEquals(converter.hashCode(), container.getBean("tephra.util.converter").hashCode());
    }

    @Test
    public void getBeanByNameClass() {
        Assert.assertNull(container.getBean("", Converter.class));
        Assert.assertNull(container.getBean("abc", Converter.class));
        Assert.assertEquals(converter.hashCode(), container.getBean("tephra.util.converter", Converter.class).hashCode());
    }

    @Test
    public void getBeans() {
        Collection<ContextRefreshedListener> listeners = container.getBeans(ContextRefreshedListener.class);
        int count = 0;
        for (ContextRefreshedListener listener : listeners)
            if (listener instanceof ContextRefreshListener1 || listener instanceof ContextRefreshListener2)
                count++;
        Assert.assertEquals(2, count);

        Assert.assertTrue(container.getBeans(NullBean.class).isEmpty());
    }

    @Test
    public void getBeanClass() {
        Assert.assertNull(container.getBeanClass(""));
        Assert.assertNull(container.getBeanClass("abc"));
        Assert.assertEquals(SingletonBean.class, container.getBeanClass("tephra.bean.singleton"));
    }

    @Test
    public void getBeanNames() {
        Set<String> set = converter.toSet(container.getBeanNames());
        Assert.assertTrue(set.contains("tephra.util.converter"));
        Assert.assertTrue(set.contains("tephra.bean.singleton"));
        Assert.assertTrue(set.contains("tephra.bean.prototype"));
        Assert.assertTrue(set.contains("tephra.bean.context-refresh-listener.1"));
        Assert.assertTrue(set.contains("tephra.bean.context-refresh-listener.2"));
    }

    @Test
    public void mapBeanName() {
        Assert.assertEquals(Bean1.class, container.getBeanClass("tephra.bean.1"));
        container.mapBeanName("tephra.bean.1", "tephra.bean.2");
        Assert.assertEquals(Bean2.class, container.getBeanClass("tephra.bean.1"));
        container.mapBeanName("tephra.bean.2", "tephra.bean.3");
        Assert.assertEquals(Bean2.class, container.getBeanClass("tephra.bean.1"));
        Assert.assertEquals(Bean3.class, container.getBeanClass("tephra.bean.2"));
        Assert.assertEquals(Bean3.class, container.getBeanClass("tephra.bean.3"));
        container.mapBeanName("tephra.bean.1", "tephra.bean.3");
        Assert.assertEquals(Bean3.class, container.getBeanClass("tephra.bean.1"));
        Assert.assertEquals(Bean3.class, container.getBeanClass("tephra.bean.2"));
        Assert.assertEquals(Bean3.class, container.getBeanClass("tephra.bean.3"));
        Assert.assertTrue(container.getBeans(Bean2.class).isEmpty());
    }

    @Test
    public void contextRefreshed() throws Exception {
        Assert.assertEquals(2, contextRefreshedList.size());
        for (int i = 0; i < contextRefreshedList.size(); i++)
            Assert.assertEquals(i + 1, converter.toInt(contextRefreshedList.get(i)));

        for (int i = 0; i < 2; i++) {
            ((ContainerImpl) container).onApplicationEvent(new ContextRefreshedEvent(getApplicationContext()));
            Assert.assertEquals(4 + i * 2, contextRefreshedList.size());
            for (int j = 0; j < contextRefreshedList.size(); j++)
                Assert.assertEquals(j % 2 + 1, converter.toInt(contextRefreshedList.get(j)));
        }

        Field field = ContainerImpl.class.getDeclaredField("refreshedListeners");
        field.setAccessible(true);
        Object object = field.get(container);
        field.set(container, Optional.empty());
        for (int i = 0; i < 2; i++) {
            ((ContainerImpl) container).onApplicationEvent(new ContextRefreshedEvent(getApplicationContext()));
            Assert.assertEquals(6, contextRefreshedList.size());
            for (int j = 0; j < contextRefreshedList.size(); j++)
                Assert.assertEquals(j % 2 + 1, converter.toInt(contextRefreshedList.get(j)));
        }

        ((ContainerImpl) container).onApplicationEvent(new ApplicationContextEvent(getApplicationContext()) {
        });
        Assert.assertEquals(6, contextRefreshedList.size());
        for (int i = 0; i < contextRefreshedList.size(); i++)
            Assert.assertEquals(i % 2 + 1, converter.toInt(contextRefreshedList.get(i)));

        field.set(container, object);
    }

    @Test
    public void contextClosed() throws Exception {
        for (int i = 0; i < 2; i++) {
            ((ContainerImpl) container).onApplicationEvent(new ContextClosedEvent(getApplicationContext()));
            Assert.assertEquals(2 + i * 2, contextClosedList.size());
            for (int j = 0; j < contextRefreshedList.size(); j++)
                Assert.assertEquals(j % 2 + 1, converter.toInt(contextClosedList.get(j)));
        }

        Field field = ContainerImpl.class.getDeclaredField("closedListeners");
        field.setAccessible(true);
        Object object = field.get(container);
        field.set(container, Optional.empty());
        for (int i = 0; i < 2; i++) {
            ((ContainerImpl) container).onApplicationEvent(new ContextClosedEvent(getApplicationContext()));
            Assert.assertEquals(4, contextClosedList.size());
            for (int j = 0; j < contextClosedList.size(); j++)
                Assert.assertEquals(j % 2 + 1, converter.toInt(contextClosedList.get(j)));
        }

        ((ContainerImpl) container).onApplicationEvent(new ApplicationContextEvent(getApplicationContext()) {
        });
        Assert.assertEquals(4, contextClosedList.size());
        for (int i = 0; i < contextClosedList.size(); i++)
            Assert.assertEquals(i % 2 + 1, converter.toInt(contextClosedList.get(i)));

        field.set(container, object);
    }

    private ApplicationContext getApplicationContext() throws Exception {
        Field field = ContainerImpl.class.getDeclaredField("applicationContext");
        field.setAccessible(true);
        return (ApplicationContext) field.get(container);
    }

    public static void runContextRefreshListener(int sort) {
        contextRefreshedList.add(sort);
    }

    public static void runContextCloseListener(int sort) {
        contextClosedList.add(sort);
    }
}
