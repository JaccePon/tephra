package org.lpw.tephra.dao.jdbc;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.dbcp.BasicDataSource;
import org.lpw.tephra.bean.ContextRefreshedListener;
import org.lpw.tephra.dao.dialect.Dialect;
import org.lpw.tephra.dao.dialect.DialectFactory;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Generator;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Repository("tephra.dao.jdbc.data-source")
public class DataSourceImpl implements org.lpw.tephra.dao.jdbc.DataSource, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Generator generator;
    @Inject
    private Logger logger;
    @Inject
    private DialectFactory dialectFactory;
    @Value("${tephra.dao.database.initial-size:0}")
    private int initialSize;
    @Value("${tephra.dao.database.max-active:5}")
    private int maxActive;
    @Value("${tephra.dao.database.max-wait:5000}")
    private int maxWait;
    @Value("${tephra.dao.database.test-interval:600000}")
    private int testInterval;
    @Value("${tephra.dao.database.remove-abandoned-timeout:300}")
    private int removeAbandonedTimeout;
    @Value("${tephra.dao.database.config:}")
    private String config;
    private Map<String, Dialect> dialects = new HashMap<>();
    private Map<String, DataSource> writeables = new ConcurrentHashMap<>();
    private Map<String, List<DataSource>> readonlys = new ConcurrentHashMap<>();
    private Map<String, Boolean> readonly = new ConcurrentHashMap<>();

    @Override
    public DataSource getWriteable(String name) {
        return writeables.get(name);
    }

    @Override
    public DataSource getReadonly(String name) {
        if (!hasReadonly(name))
            return getWriteable(name);

        List<DataSource> list = readonlys.get(name);

        return list.get(generator.random(0, list.size() - 1));
    }

    @Override
    public List<DataSource> listReadonly(String name) {
        return readonlys.get(name);
    }

    @Override
    public boolean hasReadonly(String name) {
        return readonly.getOrDefault(name, false);
    }

    @Override
    public int getContextRefreshedSort() {
        return 3;
    }

    @Override
    public void onContextRefreshed() {
        if (validator.isEmpty(config))
            return;

        JSONArray array = JSONArray.fromObject(config);
        for (int i = 0; i < array.size(); i++)
            create(array.getJSONObject(i));
    }

    @Override
    public synchronized void create(JSONObject config) {
        String key = config.getString("key");
        Dialect dialect = dialectFactory.get(config.getString("type"));
        dialects.put(key, dialect);
        createDataSource(key, dialect, config.getString("username"), config.getString("password"), config.getJSONArray("ips"), config.getString("schema"));

        if (logger.isInfoEnable())
            logger.info("成功创建数据库[{}]连接池。", config);
    }

    private void createDataSource(String name, Dialect dialect, String username, String password, JSONArray ips, String schema) {
        if (writeables.get(name) != null)
            return;

        for (int i = 0; i < ips.size(); i++) {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(dialect.getDriver());
            dataSource.setUrl(dialect.getUrl(ips.getString(i), schema));
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            dataSource.setInitialSize(initialSize);
            dataSource.setMaxActive(maxActive);
            dataSource.setMaxIdle(maxActive);
            dataSource.setMaxWait(maxWait);
            dataSource.setTestWhileIdle(true);
            dataSource.setTestOnBorrow(false);
            dataSource.setTestOnReturn(false);
            dataSource.setValidationQuery(dialect.getValidationQuery());
            dataSource.setValidationQueryTimeout(maxWait);
            dataSource.setTimeBetweenEvictionRunsMillis(testInterval);
            dataSource.setNumTestsPerEvictionRun(maxActive);
            dataSource.setRemoveAbandoned(true);
            dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
            dataSource.setLogAbandoned(true);

            if (i == 0)
                writeables.put(name, dataSource);
            else {
                List<DataSource> list = readonlys.get(name);
                if (list == null)
                    list = Collections.synchronizedList(new ArrayList<>());
                list.add(dataSource);
                readonlys.put(name, list);
                readonly.put(name, true);
            }

            if (logger.isInfoEnable())
                logger.info("数据源[{}@{}]设置完成。", name, ips.getString(i));
        }
    }

    @Override
    public Map<String, Dialect> getDialects() {
        return dialects;
    }

    @Override
    public Dialect getDialect(String key) {
        return dialects.get(validator.isEmpty(key) ? "" : key);
    }
}
