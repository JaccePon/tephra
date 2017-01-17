package org.lpw.tephra.weixin;

import org.lpw.tephra.ctrl.validate.ValidateWrapper;
import org.lpw.tephra.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(WeixinService.VALIDATOR_EXISTS_PAY_GATEWAY)
public class ExistsPayGatewayValidatorImpl extends ValidatorSupport {
    @Inject
    private WeixinHelper weixinHelper;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return weixinHelper.getPayGateway(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return "tephra.weixin.pay-gateway.not-exists";
    }
}
