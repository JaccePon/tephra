package org.lpw.tephra.ctrl.validate;

import org.lpw.tephra.ctrl.TrustfulIp;
import org.lpw.tephra.ctrl.context.Header;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(Validators.TRUSTFUL_IP)
public class TrustfulIpValidatorImpl extends ValidatorSupport {
    @Inject
    private Header header;
    @Inject
    private TrustfulIp trustfulIp;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return trustfulIp.contains(header.getIp());
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "distrust-ip";
    }
}
