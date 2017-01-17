package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.LESS_THAN)
public class LessThanValidatorImpl extends ValidatorSupport {
    private static final String DEFAULT_FAILURE_MESSAGE_KEY = Validators.PREFIX + "not-less-than";

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return converter.toInt(parameter) < validate.getNumber()[0];
    }

    @Override
    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        Object[] args = super.getFailureMessageArgs(validate);

        return new Object[]{args[0], validate.getNumber()[0]};
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return DEFAULT_FAILURE_MESSAGE_KEY;
    }
}
