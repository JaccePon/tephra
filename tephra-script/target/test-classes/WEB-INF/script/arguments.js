function arguments_execute() {
    var arg = tephra.args.get("arg");
    tephra.args.set("arg", "arg from javascript");

    return arg;
};

function arguments_all() {
    var array = tephra.args.all();
    var sum = 0;
    for (var i = 0; i < 10; i++)
        sum += array["arg" + i];

    return sum;
};