package org.lpw.tephra.util;

import org.lpw.tephra.bean.ContextRefreshedListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.File;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lpw
 */
@Component("tephra.util.context")
public class ContextImpl implements Context, ContextRefreshedListener {
    @Inject
    private Thread thread;
    @Inject
    private Logger logger;
    private String root;
    private Map<String, String> map = new ConcurrentHashMap<>();
    private ThreadLocal<Locale> locale = new ThreadLocal<>();

    @Override
    public String getAbsolutePath(String path) {
        String absolutePath = map.get(path);
        if (absolutePath == null) {
            if (path.startsWith("abs:"))
                absolutePath = path.substring(4);
            else if (path.startsWith("classpath:"))
                absolutePath = getClass().getClassLoader().getResource(path.substring(10)).getPath();
            else
                absolutePath = new File(root + "/" + path).getAbsolutePath();
            map.put(path, absolutePath);
        }

        return absolutePath;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    @Override
    public Locale getLocale() {
        Locale locale = this.locale.get();
        if (locale == null)
            locale = Locale.getDefault();

        return locale;
    }

    @Override
    public int getContextRefreshedSort() {
        return 1;
    }

    @Override
    public void onContextRefreshed() {
        String path = getClass().getResource("/").getPath();
        for (String name : new String[]{"/classes/", "/WEB-INF/"}) {
            int indexOf = path.lastIndexOf(name);
            if (indexOf > -1)
                path = path.substring(0, indexOf + 1);
        }

        root = path.replace(File.separatorChar, '/');
        map.clear();

        if (logger.isInfoEnable())
            logger.info("设置运行期根路径：{}", root);
    }
}
