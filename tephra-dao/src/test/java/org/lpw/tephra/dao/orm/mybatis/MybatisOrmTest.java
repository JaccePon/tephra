package org.lpw.tephra.dao.orm.mybatis;

import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.dao.Mode;
import org.lpw.tephra.test.DaoTestSupport;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.TimeUnit;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author lpw
 */
public class MybatisOrmTest extends DaoTestSupport {
    @Inject
    private Generator generator;
    @Inject
    private Converter converter;
    @Inject
    private MybatisOrm mybatisOrm;

    @Test
    public void crudByMapper() {
        long time = System.currentTimeMillis();
        TestMapper mapper = mybatisOrm.getMapper(null, Mode.Write, TestMapper.class);
        Assert.assertNotNull(mapper);
        Assert.assertNull(mapper.findById("id"));

        TestModel model1 = new TestModel();
        model1.setId(generator.uuid());
        model1.setSort(1);
        model1.setName("MyBatis");
        model1.setDatecol(new Date(time - TimeUnit.Day.getTime()));
        model1.setTimecol(new Timestamp(time - TimeUnit.Hour.getTime()));
        Assert.assertEquals(1, mapper.insert(model1));
        TestModel model2 = mapper.findById(model1.getId());
        Assert.assertNotNull(model2);
        Assert.assertNotEquals(model1.hashCode(), model2.hashCode());
        Assert.assertEquals(1, model2.getSort());
        Assert.assertEquals("MyBatis", model2.getName());
        Assert.assertEquals(converter.toString(new Date(time - TimeUnit.Day.getTime())), converter.toString(model2.getDatecol()));
        Assert.assertEquals(converter.toString(new Timestamp(time - TimeUnit.Hour.getTime())), converter.toString(model2.getTimecol()));

        TestModel model3 = new TestModel();
        model3.setId(model1.getId());
        model3.setName("new name");
        model3.setDatecol(new Date(time - 3 * TimeUnit.Day.getTime()));
        model3.setTimecol(new Timestamp(time - 3 * TimeUnit.Hour.getTime()));
        Assert.assertEquals(1, mapper.update(model3));
        TestModel model4 = mapper.findById(model1.getId());
        Assert.assertNotNull(model4);
        Assert.assertEquals(1, model4.getSort());
        Assert.assertEquals("new name", model4.getName());
        Assert.assertEquals(converter.toString(new Date(time - 3 * TimeUnit.Day.getTime())), converter.toString(model4.getDatecol()));
        Assert.assertEquals(converter.toString(new Timestamp(time - 3 * TimeUnit.Hour.getTime())), converter.toString(model4.getTimecol()));

        Assert.assertEquals(1, mapper.delete(model1.getId()));
        Assert.assertNull(mapper.findById(model1.getId()));

        List<TestModel> list = mapper.selectAll();
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());

        for (int i = 0; i < 10; i++) {
            TestModel model = new TestModel();
            model.setId("id" + i);
            model.setSort(i);
            model.setName("name" + i);
            model.setDatecol(new Date(time - i * TimeUnit.Day.getTime()));
            model.setTimecol(new Timestamp(time - i * TimeUnit.Hour.getTime()));
            Assert.assertEquals(1, mapper.insert(model));
        }
        checkList(mapper.selectAll(), time);

        close();
    }

    @Test
    public void crudByStatement() {
        crudUseStatement("");
    }

    @Test
    public void crudByXml() {
        crudUseStatement("FromXml");
    }

    private void crudUseStatement(String suffix) {
        long time = System.currentTimeMillis();
        Assert.assertNull(mybatisOrm.selectOne(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.findById" + suffix).parameter("id")));

        TestModel model1 = new TestModel();
        model1.setId(generator.uuid());
        model1.setSort(1);
        model1.setName("MyBatis");
        model1.setDatecol(new Date(time - TimeUnit.Day.getTime()));
        model1.setTimecol(new Timestamp(time - TimeUnit.Hour.getTime()));
        Assert.assertEquals(1, mybatisOrm.insert(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.insert" + suffix).parameter(model1)));
        TestModel model2 = mybatisOrm.selectOne(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.findById" + suffix).parameter(model1.getId()));
        Assert.assertNotNull(model2);
        Assert.assertNotEquals(model1.hashCode(), model2.hashCode());
        Assert.assertEquals(1, model2.getSort());
        Assert.assertEquals("MyBatis", model2.getName());
        Assert.assertEquals(converter.toString(new Date(time - TimeUnit.Day.getTime())), converter.toString(model2.getDatecol()));
        Assert.assertEquals(converter.toString(new Timestamp(time - TimeUnit.Hour.getTime())), converter.toString(model2.getTimecol()));

        TestModel model3 = new TestModel();
        model3.setId(model1.getId());
        model3.setName("new name");
        model3.setDatecol(new Date(time - 3 * TimeUnit.Day.getTime()));
        model3.setTimecol(new Timestamp(time - 3 * TimeUnit.Hour.getTime()));
        Assert.assertEquals(1, mybatisOrm.update(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.update" + suffix).parameter(model3)));
        TestModel model4 = mybatisOrm.selectOne(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.findById" + suffix).parameter(model1.getId()));
        Assert.assertNotNull(model4);
        Assert.assertEquals(1, model4.getSort());
        Assert.assertEquals("new name", model4.getName());
        Assert.assertEquals(converter.toString(new Date(time - 3 * TimeUnit.Day.getTime())), converter.toString(model4.getDatecol()));
        Assert.assertEquals(converter.toString(new Timestamp(time - 3 * TimeUnit.Hour.getTime())), converter.toString(model4.getTimecol()));

        Assert.assertEquals(1, mybatisOrm.delete(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.delete" + suffix).parameter(model1.getId())));
        Assert.assertNull(mybatisOrm.selectOne(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.findById" + suffix).parameter(model1.getId())));

        List<TestModel> list = mybatisOrm.selectList(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.selectAll" + suffix));
        Assert.assertNotNull(list);
        Assert.assertTrue(list.isEmpty());

        for (int i = 0; i < 10; i++) {
            TestModel model = new TestModel();
            model.setId("id" + i);
            model.setSort(i);
            model.setName("name" + i);
            model.setDatecol(new Date(time - i * TimeUnit.Day.getTime()));
            model.setTimecol(new Timestamp(time - i * TimeUnit.Hour.getTime()));
            Assert.assertEquals(1, mybatisOrm.insert(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.insert" + suffix).parameter(model)));
        }
        checkList(mybatisOrm.selectList(new MybatisBuilder().statement("org.lpw.tephra.dao.orm.mybatis.TestMapper.selectAll" + suffix)), time);

        close();
    }

    private void checkList(List<TestModel> list, long time) {
        Assert.assertEquals(10, list.size());
        for (int i = 0; i < 10; i++) {
            TestModel model = list.get(i);
            Assert.assertEquals("id" + i, model.getId());
            Assert.assertEquals(i, model.getSort());
            Assert.assertEquals("name" + i, model.getName());
            Assert.assertEquals(converter.toString(new Date(time - i * TimeUnit.Day.getTime())), converter.toString(model.getDatecol()));
            Assert.assertEquals(converter.toString(new Timestamp(time - i * TimeUnit.Hour.getTime())), converter.toString(model.getTimecol()));
        }
    }
}
