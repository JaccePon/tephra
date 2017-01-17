package org.lpw.tephra.ctrl.template.excel;

import net.sf.json.JSONArray;
import org.lpw.tephra.ctrl.context.Response;
import org.lpw.tephra.dao.model.Model;
import org.lpw.tephra.dao.model.ModelHelper;
import org.lpw.tephra.poi.Excel;
import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.Validator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.OutputStream;
import java.util.Collection;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.template.excel.builder")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ExcelBuilderImpl implements ExcelBuilder {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Excel excel;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Response response;
    private String[] titles;
    private String[] names;
    private Collection<?> collection;
    private String name;

    public ExcelBuilder build(String[] titles, String[] names, Collection<?> collection) {
        this.titles = titles;
        this.names = names;
        this.collection = collection;

        return this;
    }

    @Override
    public ExcelBuilder download(String name) {
        this.name = name;

        return this;
    }

    @Override
    public void write(OutputStream outputStream) {
        JSONArray array;
        if (collection instanceof JSONArray)
            array = (JSONArray) collection;
        else {
            array = new JSONArray();
            collection.forEach(object -> {
                if (object instanceof Model)
                    array.add(modelHelper.toJson((Model) object));
                else
                    array.add(JSONArray.fromObject(object));
            });
        }

        if (!validator.isEmpty(name))
            response.setHeader("Content-Disposition",
                    "attachment; filename*=UTF-8''" + converter.encodeUrl(name, null) + ".xls");

        excel.write(titles, names, array, outputStream);
    }
}
