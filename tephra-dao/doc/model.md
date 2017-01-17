# Model定义
Model用于保存ORM映射数据，用于业务计算过程。Model采用UUID作为系统ID值，以降低多节点集群时的并发风险。

1、Model
```java
package org.lpw.tephra.dao.model;

/**
 * 持久化模型定义。
 *
 * @author lpw
 */
public interface Model {
    /**
     * 获得Model ID值。
     *
     * @return Model ID值。
     */
    String getId();

    /**
     * 设置Model ID值。
     *
     * @param id Model ID值。
     */
    void setId(String id);
}
```
2、ModelSupport提供了默认的Model实现，以简化Model类的定义。
```java
package org.lpw.tephra.dao.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Model支持类，主键ID使用UUID。
 *
 * @author lpw
 */
@MappedSuperclass()
public class ModelSupport implements Model {
    private static final String ID = "c_id";
    private static final String UUID = "uuid2";

    private String id;

    @Jsonable
    @Column(name = ID)
    @Id
    @GeneratedValue(generator = UUID)
    @GenericGenerator(name = UUID, strategy = UUID)
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
```
3、如果希望由数据库来维护Model ID值，则可以使用NativeIdModelSupport。
```java
package org.lpw.tephra.dao.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Model支持类，主键ID由数据库自行维护。
 *
 * @author lpw
 */
@MappedSuperclass()
public class NativeIdModelSupport implements Model {
    private static final String ID = "c_id";
    private static final String NATIVE = "native";

    private String id;

    @Jsonable
    @Column(name = ID)
    @Id
    @GeneratedValue(generator = NATIVE)
    @GenericGenerator(name = NATIVE, strategy = NATIVE)
    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
```
4、如果属性值允许输出到JSON对象中，则需要使用Jsonable注解标示。
```java
package org.lpw.tephra.dao.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义可转化为JSON的属性。
 *
 * @author lpw
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Jsonable {
    /**
     * 数据格式。
     *
     * @return 数据格式。
     */
    String format() default "";
}
```
5、一个Model示例，可以用于LiteOrm和HibernateOrm：
```java
package org.lpw.spark.shortcut;
 
import org.lpw.tephra.dao.model.Jsonable;
import org.lpw.tephra.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.transaction.Transactional;
import java.util.Map;
 
/**
 * @author lpw
 */
@Component("spark.shortcut.model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = "spark.shortcut")
@Table(name = "t_spark_shortcut")
public class ShortcutModel extends ModelSupport{
    private String name;
    private String uri;
    private String params;
    private Map<String,Object> paramMap;
 
    @Jsonable
    @Column(name = "c_name")
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    @Jsonable
    @Column(name = "c_uri")
    public String getUri() {
        return uri;
    }
 
    public void setUri(String uri) {
        this.uri = uri;
    }
 
    @Jsonable
    @Column(name = "c_params")
    public String getParams() {
        return params;
    }
 
    public void setParams(String params) {
        this.params = params;
    }
 
    @Transient
    public Map<String, Object> getParamMap() {
        return paramMap;
    }
 
    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }
}
```