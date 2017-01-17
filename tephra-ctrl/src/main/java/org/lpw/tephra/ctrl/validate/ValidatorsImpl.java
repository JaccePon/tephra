package org.lpw.tephra.ctrl.validate;

import org.lpw.tephra.bean.BeanFactory;
import org.lpw.tephra.ctrl.FailureCode;
import org.lpw.tephra.ctrl.context.Request;
import org.lpw.tephra.ctrl.template.Template;
import org.lpw.tephra.ctrl.template.Templates;
import org.lpw.tephra.util.Converter;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.validate.validators")
public class ValidatorsImpl implements Validators {
    @Inject
    private org.lpw.tephra.util.Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private FailureCode failureCode;
    private Map<Validate, ValidateWrapper> wrappers = new HashMap<>();

    @Override
    public Object validate(Validate[] validates, Template template) {
        if (validator.isEmpty(validates))
            return null;

        for (Validate validate : validates) {
            ValidateWrapper wrapper = wrappers.computeIfAbsent(validate, v -> BeanFactory.getBean(ValidateWrapper.class).setValidate(v));
            Object object = validate(wrapper, template);
            if (object != null)
                return object;
        }

        return null;
    }

    @Override
    public Object validate(ValidateWrapper[] validates, Template template) {
        if (validator.isEmpty(validates))
            return null;

        for (ValidateWrapper validate : validates) {
            Object object = validate(validate, template);
            if (object != null)
                return object;
        }

        return null;
    }

    private Object validate(ValidateWrapper validate, Template template) {
        Validator validator = BeanFactory.getBean(validate.getValidator(), Validator.class);
        if (validator == null)
            throw new NullPointerException("验证器[" + validate.getValidator() + "]不存在！");

        if (this.validator.isEmpty(validate.getParameters())) {
            String parameter = this.validator.isEmpty(validate.getParameter()) ? null : request.get(validate.getParameter());
            if ((validate.isEmptyable() && this.validator.isEmpty(parameter)) || validator.validate(validate, parameter))
                return null;

            return getTemplate(template).failure(getFailureCode(validate, validator), validator.getFailureMessage(validate), validate.getParameter(), parameter);
        }

        String[] parameters = new String[validate.getParameters().length];
        for (int i = 0; i < parameters.length; i++)
            parameters[i] = request.get(validate.getParameters()[i]);
        if (validator.validate(validate, parameters))
            return null;

        return getTemplate(template).failure(getFailureCode(validate, validator), validator.getFailureMessage(validate),
                converter.toString(validate.getParameters()), converter.toString(parameters));
    }

    private Template getTemplate(Template template) {
        return template == null ? templates.get() : template;
    }

    private int getFailureCode(ValidateWrapper validate, Validator validator) {
        int failureCode = validate.getFailureCode();
        if (failureCode <= 0)
            failureCode = validator.getFailureCode(validate);

        if (failureCode < 100)
            return this.failureCode.get(failureCode);

        return failureCode;
    }
}
