package org.lpw.tephra.ctrl.validate;

import org.lpw.tephra.ctrl.TrustfulIp;
import org.lpw.tephra.ctrl.context.Header;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(Validators.SIGN)
public class SignValidatorImpl extends ValidatorSupport {
    private static final String DEFAULT_FAILURE_MESSAGE_KEY = Validators.PREFIX + "illegal-sign";

    @Inject
    private Header header;
    @Inject
    private TrustfulIp trustfulIp;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return trustfulIp.contains(header.getIp()) || request.checkSign();
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return validate.getFailureCode() > 0 ? validate.getFailureCode() : 91;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return DEFAULT_FAILURE_MESSAGE_KEY;
    }
}
