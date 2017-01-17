package org.lpw.tephra.script;

import net.sf.json.JSONObject;
import org.lpw.tephra.ctrl.validate.ValidateWrapper;
import org.lpw.tephra.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

/**
 * @author lpw
 */
@Controller(ScriptValidator.NAME)
public class ScriptValidatorImpl extends ValidatorSupport {
    @Inject
    private ScriptService scriptService;
    private ThreadLocal<JSONObject> json = new ThreadLocal<>();

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        json.remove();
        json.set(scriptService.validate(validate.getString(), parameter));

        return getCode() == 0;
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return getCode();
    }

    @Override
    public String getFailureMessage(ValidateWrapper validate) {
        int failureCode = getFailureCode(validate);
        if (failureCode == 9999)
            return message.get("tephra.ctrl.exception");

        JSONObject json = this.json.get();
        if (failureCode == 9996)
            return message.get("tephra.script.validate.name.not-exists", json.getString("name"));

        return json.getString("failure");
    }

    private int getCode() {
        JSONObject json = this.json.get();

        return json == null ? 0 : json.getInt("code");
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return null;
    }
}
