package org.lpw.tephra.dao.orm.mybatis;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.dao.Mode;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Json;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Repository("tephra.dao.orm.mybatis.session-factory")
public class SessionFactoryImpl implements SessionFactory, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Converter converter;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private org.lpw.tephra.dao.jdbc.DataSource dataSource;
    @Value("${tephra.dao.orm.mybatis.mappers:}")
    private String mappers;
    private SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
    private Map<String, SqlSessionFactory> writeables = new ConcurrentHashMap<>();
    private Map<String, List<SqlSessionFactory>> readonlys = new ConcurrentHashMap<>();

    @Override
    public SqlSessionFactory getWriteable(String dataSource) {
        return writeables.get(dataSource);
    }

    @Override
    public SqlSessionFactory getReadonly(String dataSource) {
        if (!this.dataSource.hasReadonly(dataSource))
            return getWriteable(dataSource);

        List<SqlSessionFactory> list = readonlys.get(dataSource);

        return list.get(generator.random(0, list.size() - 1));
    }

    @Override
    public void create(JSONObject config) {
        String key = config.getString("key");
        String[] mappers = json.getAsStringArray(config, "values");
        writeables.put(key, create(key, Mode.Write, dataSource.getWriteable(key), mappers));
        if (dataSource.hasReadonly(key)) {
            List<SqlSessionFactory> list = new ArrayList<>();
            dataSource.listReadonly(key).forEach(dataSource -> list.add(create(key, Mode.Read, dataSource, mappers)));
            readonlys.put(key, list);
        }

        if (logger.isInfoEnable())
            logger.info("MyBatis环境[{}@{}]初始化完成。", key, converter.toString(mappers));
    }

    private SqlSessionFactory create(String key, Mode mode, DataSource dataSource, String[] mappers) {
        Environment environment = new Environment(key + "-" + mode.ordinal(), new JdbcTransactionFactory(), dataSource);
        Configuration configuration = new Configuration(environment);
        for (String mapper : mappers)
            configuration.addMappers(mapper);

        return builder.build(configuration);
    }

    @Override
    public int getContextRefreshedSort() {
        return 5;
    }

    @Override
    public void onContextRefreshed() {
        if (validator.isEmpty(mappers))
            return;

        JSONArray array = JSONArray.fromObject(mappers);
        for (int i = 0; i < array.size(); i++)
            create(array.getJSONObject(i));
    }
}
