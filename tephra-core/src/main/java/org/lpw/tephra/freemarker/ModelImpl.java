package org.lpw.tephra.freemarker;

import org.lpw.tephra.util.Converter;
import org.lpw.tephra.util.DateTime;
import org.lpw.tephra.util.Message;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller("tephra.freemarker.model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ModelImpl implements Model {
    @Inject
    private Converter converter;
    @Inject
    private Message message;
    @Inject
    private DateTime dateTime;
    private Object data;

    @Override
    public Converter getConverter() {
        return converter;
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public DateTime getDatetime() {
        return dateTime;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public Model setData(Object data) {
        this.data = data;

        return this;
    }
}
