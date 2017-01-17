package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.DATE_TIME)
public class DateValidatorImpl extends ValidatorSupport {
    private static final String DEFAULT_FAILURE_MESSAGE_KEY = Validators.PREFIX + "date-time.illegal";

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return converter.toDate(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return DEFAULT_FAILURE_MESSAGE_KEY;
    }
}
