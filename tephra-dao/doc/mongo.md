# 使用MongoDB持久化数据
Dao模块提供了对于MongoDB的支持，需要配置dao.tephra.config.properties：
```properties
## 设置数据库IP地址+端口号集。
## 使用JSON数据格式，每个设置对象包含key与values属性，其中
## * key 为数据库引用名称，空则为默认数据库；必须有且仅有一个key设置为空。
## * username 数据库登入用户名。
## * password 数据库登入密码。
## * ips 指定数据库的访问IP地址+":"+端口号，如果设置多个值则自动进行负载均衡。
## * schema 数据库名。
#tephra.dao.mongo.config = [{key:"",username:"root",password:"root",ips:["127.0.0.1:27017"],schema:"d_tephra"}]
```
Mongo接口说明：
```java
package org.lpw.tephra.dao.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.bson.Document;
import org.lpw.tephra.dao.Commitable;
import org.lpw.tephra.dao.model.Model;

/**
 * MongoDB操作接口。
 *
 * @author lpw
 */
public interface Mongo extends Commitable {
    /**
     * 获取Mongo数据库。
     *
     * @return Mongo数据库；如果不存在则返回null。
     */
    MongoDatabase getDatabase();

    /**
     * 获取Mongo数据库。
     *
     * @param key 配置key。
     * @return Mongo数据库；如果不存在则返回null。
     */
    MongoDatabase getDatabase(String key);

    /**
     * 获取Mongo文档集。
     *
     * @param modelClass 类名。
     * @return Mongo文档集；如果不存在则返回null。
     */
    MongoCollection<Document> getCollection(Class<? extends Model> modelClass);

    /**
     * 获取Mongo文档集。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @return Mongo文档集；如果不存在则返回null。
     */
    MongoCollection<Document> getCollection(String key, Class<? extends Model> modelClass);

    /**
     * 获取Mongo文档集。
     *
     * @param name 文档名。
     * @return Mongo文档集；如果不存在则返回null。
     */
    MongoCollection<Document> getCollection(String name);

    /**
     * 获取Mongo文档集。
     *
     * @param key  配置key。
     * @param name 文档名。
     * @return Mongo文档集；如果不存在则返回null。
     */
    MongoCollection<Document> getCollection(String key, String name);

    /**
     * 插入一个数据。
     *
     * @param modelClass 类名。
     * @param object     要保存的数据。
     */
    void insert(Class<? extends Model> modelClass, JSONObject object);

    /**
     * 插入一个数据。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @param object     要保存的数据。
     */
    void insert(String key, Class<? extends Model> modelClass, JSONObject object);

    /**
     * 插入一个数据。
     *
     * @param collection 文档名。
     * @param object     要保存的数据。
     */
    void insert(String collection, JSONObject object);

    /**
     * 插入一个数据。
     *
     * @param key        配置key。
     * @param collection 文档名。
     * @param object     要保存的数据。
     */
    void insert(String key, String collection, JSONObject object);

    /**
     * 插入多个数据。
     *
     * @param modelClass 类名。
     * @param array      要保存的数据。
     */
    void insert(Class<? extends Model> modelClass, JSONArray array);

    /**
     * 插入多个数据。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @param array      要保存的数据。
     */
    void insert(String key, Class<? extends Model> modelClass, JSONArray array);

    /**
     * 插入多个数据。
     *
     * @param collection 文档名。
     * @param array      要保存的数据。
     */
    void insert(String collection, JSONArray array);

    /**
     * 插入多个数据。
     *
     * @param key        配置key。
     * @param collection 文档名。
     * @param array      要保存的数据。
     */
    void insert(String key, String collection, JSONArray array);

    /**
     * 更新数据。
     *
     * @param modelClass 类名。
     * @param set        设置值。
     * @param where      条件。
     */
    void update(Class<? extends Model> modelClass, JSONObject set, JSONObject where);

    /**
     * 更新数据。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @param set        设置值。
     * @param where      条件。
     */
    void update(String key, Class<? extends Model> modelClass, JSONObject set, JSONObject where);

    /**
     * 更新数据。
     *
     * @param collection 文档名。
     * @param set        设置值。
     * @param where      条件。
     */
    void update(String collection, JSONObject set, JSONObject where);

    /**
     * 更新数据。
     *
     * @param key        配置key。
     * @param collection 文档名。
     * @param set        设置值。
     * @param where      条件。
     */
    void update(String key, String collection, JSONObject set, JSONObject where);

    /**
     * 删除数据。
     *
     * @param modelClass 类名。
     * @param where      条件。
     */
    void delete(Class<? extends Model> modelClass, JSONObject where);

    /**
     * 删除数据。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @param where      条件。
     */
    void delete(String key, Class<? extends Model> modelClass, JSONObject where);

    /**
     * 删除数据。
     *
     * @param collection 文档名。
     * @param where      条件。
     */
    void delete(String collection, JSONObject where);

    /**
     * 删除数据。
     *
     * @param key        配置key。
     * @param collection 文档名。
     * @param where      条件。
     */
    void delete(String key, String collection, JSONObject where);

    /**
     * 获取一个数据。
     *
     * @param modelClass 类名。
     * @param where      条件。
     * @return 数据；如果不存在则返回null。
     */
    JSONObject findOne(Class<? extends Model> modelClass, JSONObject where);

    /**
     * 获取一个数据。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @param where      条件。
     * @return 数据；如果不存在则返回null。
     */
    JSONObject findOne(String key, Class<? extends Model> modelClass, JSONObject where);

    /**
     * 获取一个数据。
     *
     * @param collection 文档名。
     * @param where      条件。
     * @return 数据；如果不存在则返回{}。
     */
    JSONObject findOne(String collection, JSONObject where);

    /**
     * 获取一个数据。
     *
     * @param key        配置key。
     * @param collection 文档名。
     * @param where      条件。
     * @return 数据；如果不存在则返回{}。
     */
    JSONObject findOne(String key, String collection, JSONObject where);

    /**
     * 检索数据集。
     *
     * @param modelClass 类名。
     * @param where      条件。
     * @return 数据；如果不存在则返回[]。
     */
    JSONArray find(Class<? extends Model> modelClass, JSONObject where);

    /**
     * 检索数据集。
     *
     * @param key        配置key。
     * @param modelClass 类名。
     * @param where      条件。
     * @return 数据；如果不存在则返回[]。
     */
    JSONArray find(String key, Class<? extends Model> modelClass, JSONObject where);

    /**
     * 检索数据集。
     *
     * @param collection 文档名。
     * @param where      条件。
     * @return 数据；如果不存在则返回[]。
     */
    JSONArray find(String collection, JSONObject where);

    /**
     * 检索数据集。
     *
     * @param key        配置key。
     * @param collection 文档名。
     * @param where      条件。
     * @return 数据；如果不存在则返回[]。
     */
    JSONArray find(String key, String collection, JSONObject where);

    /**
     * 创建连接。
     *
     * @param config 配置。
     */
    void create(JSONObject config);
}
```
简单的增删改查：
```java
package org.lpw.tephra.dao.mongo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.lpw.tephra.test.DaoTestSupport;
import org.lpw.tephra.util.Generator;

import javax.inject.Inject;

/**
 * @author lpw
 */
public class MongoTest extends DaoTestSupport {
    @Inject
    private Generator generator;
    @Inject
    private Mongo mongo;

    @Test
    public void insert() {
        create();

        mongo.delete(null, "t_mongo", null);

        JSONObject object1 = new JSONObject();
        object1.put("id", generator.uuid());
        object1.put("name", "hello");
        Assert.assertTrue(mongo.find(null, "t_mongo", JSONObject.fromObject("{\"id\":\"" + object1.getString("id") + "\"}")).isEmpty());
        mongo.insert(null, "t_mongo", object1);
        JSONObject object2 = mongo.findOne(null, "t_mongo", JSONObject.fromObject("{\"id\":\"" + object1.getString("id") + "\"}"));
        Assert.assertEquals(object1.getString("name"), object2.getString("name"));

        JSONArray array1 = new JSONArray();
        array1.add(object1);
        object2.put("id", generator.uuid());
        object2.put("name", "name 2");
        array1.add(object2);
        mongo.insert(null, "t_mongo", array1);
        JSONArray array2 = mongo.find(null, "t_mongo", null);
        Assert.assertEquals(3, array2.size());
        JSONObject object3 = array2.getJSONObject(0);
        Assert.assertEquals(object1.getString("id"), object3.getString("id"));
        Assert.assertEquals(object1.getString("name"), object3.getString("name"));
        JSONObject object4 = array2.getJSONObject(1);
        Assert.assertEquals(object1.getString("id"), object4.getString("id"));
        Assert.assertEquals(object1.getString("name"), object4.getString("name"));
        JSONObject object5 = array2.getJSONObject(2);
        Assert.assertEquals(object2.getString("id"), object5.getString("id"));
        Assert.assertEquals(object2.getString("name"), object5.getString("name"));
    }

    @Test
    public void update() {
        create();

        mongo.delete(null, "t_mongo", null);

        JSONObject object1 = new JSONObject();
        object1.put("id", generator.uuid());
        object1.put("name", "hello");
        mongo.insert(null, "t_mongo", object1);
        JSONObject object2 = new JSONObject();
        object2.put("id", generator.uuid());
        object2.put("name", "name");
        mongo.insert(null, "t_mongo", object2);
        mongo.update(null, "t_mongo", JSONObject.fromObject("{\"name\":\"new name\"}"), JSONObject.fromObject("{\"id\":\"" + object1.getString("id") + "\"}"));
        JSONObject object3 = mongo.findOne(null, "t_mongo", JSONObject.fromObject("{\"id\":\"" + object1.getString("id") + "\"}"));
        Assert.assertEquals("new name", object3.getString("name"));
        JSONObject object4 = mongo.findOne(null, "t_mongo", JSONObject.fromObject("{\"id\":\"" + object2.getString("id") + "\"}"));
        Assert.assertEquals(object2.getString("name"), object4.getString("name"));

        mongo.update(null, "t_mongo", JSONObject.fromObject("{\"name\":\"hello mongo\"}"), null);
        JSONObject object5 = mongo.findOne(null, "t_mongo", JSONObject.fromObject("{\"id\":\"" + object1.getString("id") + "\"}"));
        Assert.assertEquals("hello mongo", object5.getString("name"));
        JSONObject object6 = mongo.findOne(null, "t_mongo", JSONObject.fromObject("{\"id\":\"" + object2.getString("id") + "\"}"));
        Assert.assertEquals("hello mongo", object6.getString("name"));
    }

    @Test
    public void delete() {
        create();

        mongo.delete(null, "t_mongo", null);

        JSONObject object1 = new JSONObject();
        object1.put("id", generator.uuid());
        object1.put("name", "hello");
        mongo.insert(null, "t_mongo", object1);
        JSONObject object2 = new JSONObject();
        object2.put("id", generator.uuid());
        object2.put("name", "name");
        mongo.insert(null, "t_mongo", object2);
        mongo.delete(null, "t_mongo", JSONObject.fromObject("{\"id\":\"" + object1.getString("id") + "\"}"));
        Assert.assertTrue(mongo.find(null, "t_mongo", JSONObject.fromObject("{\"id\":\"" + object1.getString("id") + "\"}")).isEmpty());
        JSONObject object3 = mongo.findOne(null, "t_mongo", JSONObject.fromObject("{\"id\":\"" + object2.getString("id") + "\"}"));
        Assert.assertEquals(object2.getString("name"), object3.getString("name"));
        mongo.insert(null, "t_mongo", object1);
        mongo.delete(null, "t_mongo", null);
        Assert.assertTrue(mongo.find(null, "t_mongo", null).isEmpty());
    }

    private void create() {
        mongo.create(JSONObject.fromObject("{key:\"\",username:\"root\",password:\"root\",ips:[\"127.0.0.1:27017\"],schema:\"d_tephra_test\"}"));
    }
}
```