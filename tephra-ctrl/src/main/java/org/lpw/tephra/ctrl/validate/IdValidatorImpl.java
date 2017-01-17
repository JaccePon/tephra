package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.ID)
public class IdValidatorImpl extends ValidatorSupport {
    private static final String DEFAULT_FAILURE_MESSAGE_KEY = Validators.PREFIX + "illegal-id";

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return validator.isMatchRegex("[\\da-f-]{36}", parameter) && converter.toArray(parameter, "-").length == 5;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return DEFAULT_FAILURE_MESSAGE_KEY;
    }
}
