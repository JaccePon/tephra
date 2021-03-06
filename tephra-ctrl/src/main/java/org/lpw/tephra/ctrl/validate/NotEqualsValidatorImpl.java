package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.NOT_EQUALS)
public class NotEqualsValidatorImpl extends ValidatorSupport {
    private static final String DEFAULT_FAILURE_MESSAGE_KEY = Validators.PREFIX + "equals";

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        String[] names = converter.toArray(validate.getParameter(), ",");

        return !request.get(names[0]).equals(request.get(names[1]));
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return DEFAULT_FAILURE_MESSAGE_KEY;
    }
}
