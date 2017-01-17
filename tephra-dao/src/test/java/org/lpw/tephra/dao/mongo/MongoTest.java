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
