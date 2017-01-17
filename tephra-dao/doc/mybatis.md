# 使用MybatisOrm持久化数据
Dao模块提供了MybatisOrm用于封装MyBatis操作。MybatisOrm接口定义如下：
```java
package org.lpw.tephra.dao.orm.mybatis;

import org.lpw.tephra.dao.Mode;

import java.util.List;
import java.util.Map;

/**
 * MybatisOrm。主要提供基于MyBatis的ORM支持。
 *
 * @author lpw
 */
public interface MybatisOrm {
    /**
     * 获取Mapper实例。
     *
     * @param dataSource  数据源，如果为null则使用默认数据源。
     * @param mode        连接类型，如果为null则使用可读写连接。
     * @param mapperClass Mapper类。
     * @param <T>         Mapper类定义。
     * @return Mapper实例。
     */
    <T> T getMapper(String dataSource, Mode mode, Class<T> mapperClass);

    /**
     * 检索一个数据。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     * @param <T>     返回结果类型。
     * @return 返回结果。
     */
    <T> T selectOne(MybatisBuilder builder);

    /**
     * 检索数据集。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     * @param <T>     返回数据类定义。
     * @return 数据集。
     */
    <T> List<T> selectList(MybatisBuilder builder);

    /**
     * 检索Map数据集。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     * @param <K>     返回数据key类定义。
     * @param <V>     返回数据value类定义。
     * @return Map数据集。
     */
    <K, V> Map<K, V> selectMap(MybatisBuilder builder);

    /**
     * 执行检索，并将检索结果交由ResultHandler处理。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     */
    void select(MybatisBuilder builder);

    /**
     * 插入数据。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     * @return 受影响记录数。
     */
    int insert(MybatisBuilder builder);

    /**
     * 更新数据。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     * @return 受影响记录数。
     */
    int update(MybatisBuilder builder);

    /**
     * 删除数据。
     * 如果未设置数据源则使用默认数据源。
     *
     * @param builder MyBatis参数构造器。
     * @return 受影响记录数。
     */
    int delete(MybatisBuilder builder);

    /**
     * 手动回滚本次事务所有更新操作。
     */
    void rollback();

    /**
     * 提交持久化，并关闭当前线程的所有连接。
     */
    void close();
}
```
1、在dao.tephra.config.properties中配置搜索包：
```properties
## MyBatis Orm设置。
## 设置Model搜索包名集。
## 使用JSON数据格式，每个设置对象包含key与values属性，其中
## * key 为数据库引用名称；空则为默认库；必须有且仅有一个key设置为空。
## * values 为搜索包名集。
#tephra.dao.orm.mybatis.mappers = [{key:"",values:["org.lpw.tephra"]}]
```
2、定义Mapper接口：
```java
package org.lpw.tephra.dao.orm.mybatis;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.lpw.tephra.dao.orm.TestModel;

import java.util.List;

/**
 * @author lpw
 */
public interface TestMapper {
    @Select("select * from t_tephra_mybatis where id=#{id}")
    TestModel findById(String id);

    @Insert("insert into t_tephra_mybatis values(#{id},#{sort},#{name})")
    int insert(TestModel model);

    @Update("update t_tephra_mybatis set name=#{name} where id=#{id}")
    int update(TestModel model);

    @Delete("delete from t_tephra_mybatis where id=#{id}")
    int delete(String id);

    @Select("select * from t_tephra_mybatis order by sort")
    List<TestModel> selectAll();
}
```
3、如果必要，也可以在相同包路径下添加XML格式的Mapper，建议在需要使用复杂SQL配置时使用：
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.lpw.tephra.dao.orm.mybatis.TestMapper">
    <select id="findByIdFromXml" resultType="org.lpw.tephra.dao.orm.TestModel">
        select * from t_tephra_mybatis where id=#{id}
    </select>
    <insert id="insertFromXml" parameterType="org.lpw.tephra.dao.orm.TestModel">
        insert into t_tephra_mybatis values(#{id},#{sort},#{name})
    </insert>
    <update id="updateFromXml" parameterType="org.lpw.tephra.dao.orm.TestModel">
        update t_tephra_mybatis set name=#{name} where id=#{id}
    </update>
    <delete id="deleteFromXml">
        delete from t_tephra_mybatis where id=#{id}
    </delete>
    <select id="selectAllFromXml" resultType="org.lpw.tephra.dao.orm.TestModel">
        select * from t_tephra_mybatis order by sort
    </select>
</mapper>
```
4、简单的增删改查示例：
```java
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
```