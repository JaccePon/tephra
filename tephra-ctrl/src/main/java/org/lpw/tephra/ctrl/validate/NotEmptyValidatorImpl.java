package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.NOT_EMPTY)
public class NotEmptyValidatorImpl extends ValidatorSupport {
    private static final String DEFAULT_FAILURE_MESSAGE_KEY = Validators.PREFIX + "empty";

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return !validator.isEmpty(parameter);
    }

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        for (String parameter : parameters)
            if (!validator.isEmpty(parameter))
                return true;

        return false;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return DEFAULT_FAILURE_MESSAGE_KEY;
    }
}
