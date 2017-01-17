package org.lpw.tephra.bean;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.CoreTestSupport;
import org.lpw.tephra.util.Converter;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author lpw
 */
public class BeanFactoryTest extends CoreTestSupport {
    @Inject
    private Converter converter;
    @Inject
    private SingletonBean singletonBean;

    @Test
    public void getBeanByClass() {
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < 10; i++)
            set.add(BeanFactory.getBean(SingletonBean.class).hashCode());
        Assert.assertEquals(1, set.size());

        set = new HashSet<>();
        for (int i = 0; i < 10; i++)
            set.add(BeanFactory.getBean(PrototypeBean.class).hashCode());
        Assert.assertEquals(10, set.size());

        Assert.assertNull(BeanFactory.getBean(NullBean.class));
    }

    @Test
    public void getBeanByName() {
        Assert.assertNull(BeanFactory.getBean(""));
        Assert.assertNull(BeanFactory.getBean("abc"));
        Assert.assertEquals(singletonBean.hashCode(), BeanFactory.getBean("tephra.bean.singleton").hashCode());
    }

    @Test
    public void getBeanByNameClass() {
        Assert.assertNull(BeanFactory.getBean("", Converter.class));
        Assert.assertNull(BeanFactory.getBean("abc", Converter.class));
        Assert.assertEquals(singletonBean.hashCode(), BeanFactory.getBean("tephra.bean.singleton", SingletonBean.class).hashCode());
    }

    @Test
    public void getBeans() {
        Collection<ContextRefreshedListener> listeners = BeanFactory.getBeans(ContextRefreshedListener.class);
        int count = 0;
        for (ContextRefreshedListener listener : listeners)
            if (listener instanceof ContextRefreshListener1 || listener instanceof ContextRefreshListener2)
                count++;
        Assert.assertEquals(2, count);

        Assert.assertTrue(BeanFactory.getBeans(NullBean.class).isEmpty());
    }

    @Test
    public void getBeanClass() {
        Assert.assertNull(BeanFactory.getBeanClass(""));
        Assert.assertNull(BeanFactory.getBeanClass("abc"));
        Assert.assertEquals(SingletonBean.class, BeanFactory.getBeanClass("tephra.bean.singleton"));
    }

    @Test
    public void getBeanNames() {
        Set<String> set = converter.toSet(BeanFactory.getBeanNames());
        Assert.assertTrue(set.contains("tephra.util.converter"));
        Assert.assertTrue(set.contains("tephra.bean.singleton"));
        Assert.assertTrue(set.contains("tephra.bean.prototype"));
        Assert.assertTrue(set.contains("tephra.bean.context-refresh-listener.1"));
        Assert.assertTrue(set.contains("tephra.bean.context-refresh-listener.2"));
    }
}
