package org.lpw.tephra.ctrl.context;

import org.lpw.tephra.crypto.Sign;
import org.lpw.tephra.ctrl.Coder;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.model.ModelTable;
import org.lpw.tephra.dao.model.ModelTables;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Logger;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.context.request")
public class RequestImpl implements Request, RequestAware {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Logger logger;
    @Inject
    private Sign sign;
    @Inject
    private ModelTables modelTables;
    @Inject
    private Optional<Coder> coder;
    @Value("${tephra.ctrl.content.request.sign:tephra-ctrl-sign}")
    private String signName;
    private ThreadLocal<RequestAdapter> adapter = new ThreadLocal<>();

    @Override
    public String get(String name) {
        return adapter.get().get(name);
    }

    @Override
    public int getAsInt(String name) {
        return converter.toInt(get(name));
    }

    @Override
    public long getAsLong(String name) {
        return converter.toLong(get(name));
    }

    @Override
    public boolean getAsBoolean(String name) {
        return converter.toBoolean(get(name));
    }

    @Override
    public Date getAsDate(String name) {
        return converter.toDate(get(name));
    }

    @Override
    public String[] getAsArray(String name) {
        String[] array = adapter.get().getAsArray(name);

        return array == null ? converter.toArray(get(name), ",") : array;
    }

    @Override
    public Map<String, String> getMap() {
        Map<String, String> map = adapter.get().getMap();

        return coder.isPresent() ? coder.get().decode(map) : map;
    }

    @Override
    public String getFromInputStream() {
        return adapter.get().getFromInputStream();
    }

    @Override
    public <T extends Model> T setToModel(T model) {
        Map<String, String> map = getMap();
        if (validator.isEmpty(map))
            return model;

        ModelTable modelTable = modelTables.get(model.getClass());
        try {
            for (String name : map.keySet()) {
                if ("id".equals(name)) {
                    model.setId(map.get(name));

                    continue;
                }

                fillToModel(modelTable, model, name, map.get(name));
            }
        } catch (Exception e) {
            logger.warn(e, "设置参数到Model时发生异常！");
        }

        return model;
    }

    private <T extends Model> void fillToModel(ModelTable modelTable, T model, String name, String value) throws NoSuchMethodException, SecurityException {
        if ((name.endsWith("[id]") || name.endsWith(".id")) && name.indexOf('[') == name.lastIndexOf('[') && name.indexOf('.') == name.lastIndexOf('.')) {
            modelTable.set(model, name.substring(0, name.indexOf('[') + name.indexOf('.') + 1), value);

            return;
        }

        if (!validator.isMatchRegex("[a-z0-9A-Z]+", name) || value == null)
            return;

        modelTable.set(model, name, value);
    }

    @Override
    public boolean checkSign() {
        return sign.verify(getMap(), get(signName));
    }

    @Override
    public void putSign(Map<String, String> map) {
        sign.put(map, get(signName));
    }

    @Override
    public String getServerName() {
        return adapter.get().getServerName();
    }

    @Override
    public int getServerPort() {
        return adapter.get().getServerPort();
    }

    @Override
    public String getContextPath() {
        return adapter.get().getContextPath();
    }

    @Override
    public String getUrl() {
        return adapter.get().getUrl();
    }

    @Override
    public String getUri() {
        return adapter.get().getUri();
    }

    @Override
    public String getMethod() {
        return adapter.get().getMethod();
    }

    @Override
    public void set(RequestAdapter adapter) {
        this.adapter.set(adapter);
    }
}
