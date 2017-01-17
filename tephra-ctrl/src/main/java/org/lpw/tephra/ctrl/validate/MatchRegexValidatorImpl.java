package org.lpw.tephra.ctrl.validate;

import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(Validators.MATCH_REGEX)
public class MatchRegexValidatorImpl extends ValidatorSupport {
    private static final String DEFAULT_FAILURE_MESSAGE_KEY = Validators.PREFIX + "not-match-regex";

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return validator.isMatchRegex(validate.getString()[0], parameter);
    }

    @Override
    protected Object[] getFailureMessageArgs(ValidateWrapper validate) {
        Object[] args = super.getFailureMessageArgs(validate);

        return new Object[]{args[0], validate.getString()[0]};
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return DEFAULT_FAILURE_MESSAGE_KEY;
    }
}
