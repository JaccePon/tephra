package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.BETWEEN)
public class BetweenValidatorImpl extends ValidatorSupport {
    private static final String DEFAULT_FAILURE_MESSAGE_KEY = Validators.PREFIX + "not-between";

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        int n = converter.toInt(parameter);

        return n >= validate.getNumber()[0] && n <= validate.getNumber()[1];
    }

    @Override
    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        Object[] args = super.getFailureMessageArgs(validate);

        return new Object[]{args[0], validate.getNumber()[0], validate.getNumber()[1]};
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return DEFAULT_FAILURE_MESSAGE_KEY;
    }
}
