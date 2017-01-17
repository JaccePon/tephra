tephra.validator = function (name, func) {
    tephra.validators[name] = func;
};

tephra.validators = {};

tephra.validate = function () {
    try {
        console.log(tephra.args.get("parameter"));
        var json = JSON.parse(tephra.args.get("parameter"));
        var names = tephra.args.get("names");
        for (var i = 0; i < names.length; i++) {
            if (!tephra.validators[names[i]])
                return "{\"code\":9996,\"name\":" + names[i] + "}";

            var result = tephra.validators[names[i]](json);
            if (!result || result.code != 0)
                return JSON.stringify(result);
        }

        return "{\"code\":0}";
    } catch (e) {
        return "{\"code\":9999}";
    }
};
