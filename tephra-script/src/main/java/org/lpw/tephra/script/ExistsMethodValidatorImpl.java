package org.lpw.tephra.script;

import org.lpw.tephra.ctrl.validate.ValidateWrapper;
import org.lpw.tephra.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(ScriptService.VALIDATOR_EXISTS_METHOD)
public class ExistsMethodValidatorImpl extends ValidatorSupport {
    private static final String DEFAULT_FAILURE_MESSAGE_KEY = "tephra.script.method.not-exists";

    @Inject
    private Engine engine;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return engine.existsMethod();
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return DEFAULT_FAILURE_MESSAGE_KEY;
    }
}
