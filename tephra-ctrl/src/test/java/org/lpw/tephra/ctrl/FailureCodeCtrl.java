package org.lpw.tephra.ctrl;

import org.lpw.tephra.ctrl.execute.Execute;
import org.lpw.tephra.ctrl.validate.Validate;
import org.lpw.tephra.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller("tephra.ctrl.failure-code")
@Execute(name = "/tephra/ctrl/failure-code/", code = "10")
public class FailureCodeCtrl {
    @Execute(name = "execute", code = "01", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "name", failureCode = 1, failureArgKeys = {"tephra.ctrl.failure-code.name"})
    })
    public Object execute() {
        return "";
    }
}
