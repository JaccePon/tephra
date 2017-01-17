package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.GREATER_THAN)
public class GreaterThanValidatorImpl extends ValidatorSupport {
    private static final String DEFAULT_FAILURE_MESSAGE_KEY = Validators.PREFIX + "not-greater-than";

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return converter.toInt(parameter) > validate.getNumber()[0];
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
