package org.lpw.tephra.dao.orm.hibernate;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.dao.orm.PageList;
import org.lpw.tephra.dao.orm.TestModel;
import org.lpw.tephra.test.DaoTestSupport;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.TimeUnit;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lpw
 */
public class HibernateOrmTest extends DaoTestSupport {
    @Inject
    private Converter converter;
    @Inject
    private HibernateOrm hibernateOrm;

    @Test
    public void crud() {
        long time = System.currentTimeMillis();
        PageList<TestModel> pl = hibernateOrm.query(new HibernateQuery(TestModel.class), null);
        Assert.assertNotNull(pl);
        Assert.assertEquals(0, pl.getCount());
        Assert.assertNotNull(pl.getList());
        Assert.assertTrue(pl.getList().isEmpty());

        TestModel model1 = new TestModel();
        model1.setSort(1);
        model1.setName("HibernateOrm");
        model1.setDate(new Date(time - TimeUnit.Day.getTime()));
        model1.setTime(new Timestamp(time - TimeUnit.Hour.getTime()));
        hibernateOrm.save(model1);
        hibernateOrm.close();
        Assert.assertNotNull(model1.getId());
        Assert.assertEquals(36, model1.getId().length());

        TestModel model2 = hibernateOrm.findById(TestModel.class, model1.getId());
        Assert.assertNotEquals(model1.hashCode(), model2.hashCode());
        Assert.assertEquals(model1.getId(), model2.getId());
        Assert.assertEquals(1, model2.getSort());
        Assert.assertEquals("HibernateOrm", model2.getName());
        Assert.assertEquals(converter.toString(new Date(time - TimeUnit.Day.getTime())), converter.toString(model2.getDate()));
        Assert.assertEquals(converter.toString(new Timestamp(time - TimeUnit.Hour.getTime())), converter.toString(model2.getTime()));
        hibernateOrm.close();

        TestModel model3 = new TestModel();
        model3.setId(model1.getId());
        model3.setName("new name");
        model3.setDate(new Date(time - 3 * TimeUnit.Day.getTime()));
        model3.setTime(new Timestamp(time - 3 * TimeUnit.Hour.getTime()));
        hibernateOrm.save(model3);
        TestModel model4 = hibernateOrm.findById(TestModel.class, model1.getId());
        Assert.assertEquals(model1.getId(), model4.getId());
        Assert.assertEquals(0, model4.getSort());
        Assert.assertEquals("new name", model4.getName());
        Assert.assertEquals(converter.toString(new Date(time - 3 * TimeUnit.Day.getTime())), converter.toString(model4.getDate()));
        Assert.assertEquals(converter.toString(new Timestamp(time - 3 * TimeUnit.Hour.getTime())), converter.toString(model4.getTime()));
        hibernateOrm.close();

        hibernateOrm.delete(model1);
        Assert.assertNull(hibernateOrm.findById(TestModel.class, model1.getId()));
        hibernateOrm.close();

        pl = hibernateOrm.query(new HibernateQuery(TestModel.class), null);
        Assert.assertNotNull(pl);
        Assert.assertEquals(0, pl.getCount());
        Assert.assertNotNull(pl.getList());
        Assert.assertTrue(pl.getList().isEmpty());

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            TestModel model = new TestModel();
            model.setSort(i);
            model.setName("name" + i);
            model.setDate(new Date(time - i * TimeUnit.Day.getTime()));
            model.setTime(new Timestamp(time - i * TimeUnit.Hour.getTime()));
            hibernateOrm.save(model);
            list.add(model.hashCode());
        }
        pl = hibernateOrm.query(new HibernateQuery(TestModel.class).where("c_sort<?").order("c_sort"), new Object[]{5});
        Assert.assertNotNull(pl);
        Assert.assertEquals(0, pl.getCount());
        Assert.assertEquals(5, pl.getList().size());
        for (int i = 0; i < 5; i++) {
            TestModel model = pl.getList().get(i);
            Assert.assertEquals(36, model.getId().length());
            Assert.assertEquals(i, model.getSort());
            Assert.assertEquals("name" + i, model.getName());
            Assert.assertEquals(converter.toString(new Date(time - i * TimeUnit.Day.getTime())), converter.toString(model.getDate()));
            Assert.assertEquals(converter.toString(new Timestamp(time - i * TimeUnit.Hour.getTime())), converter.toString(model.getTime()));
            Assert.assertEquals(converter.toInt(list.get(i)), model.hashCode());
        }

        hibernateOrm.close();
    }
}
