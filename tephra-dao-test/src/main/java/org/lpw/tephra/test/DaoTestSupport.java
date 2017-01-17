package org.lpw.tephra.test;

import org.junit.Before;
import org.lpw.tephra.atomic.Closable;
import org.lpw.tephra.util.Validator;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author lpw
 */
public class DaoTestSupport extends CoreTestSupport {
    @Inject
    private Validator validator;
    @Resource(name = "tephra.dao.jdbc.sql")
    private Object sql;
    @Inject
    private Set<Closable> closables;
    private Method update;

    @Before
    public void before() throws Exception {
        String path = getClass().getResource("/").getPath();
        path = path.substring(0, path.lastIndexOf("/target/")) + "/src/";
        sql(path + "main/sql/create.sql");
        sql(path + "test/sql/mock.sql");
        close();
    }

    private void sql(String path) throws Exception {
        File file = new File(path);
        if (!file.exists() && !file.isFile())
            return;

        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) {
            if (validator.isEmpty(line))
                continue;

            sb.append(line);
            if (line.trim().endsWith(";")) {
                update(sb.toString());
                sb.delete(0, sb.length());
            }
        }
        reader.close();
        close();
    }

    private void update(String sql) throws Exception {
        if (update == null)
            update = this.sql.getClass().getMethod("update", String.class, Object[].class);
        update.invoke(this.sql, sql, new Object[0]);
    }

    protected void close() {
        closables.forEach(Closable::close);
    }
}
