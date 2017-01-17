# 请求参数验证
验证规则通过Validate注解进行设置：
```java
package org.lpw.tephra.ctrl.validate;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证规则。
 *
 * @author lpw
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
    /**
     * 验证器Bean名称。
     *
     * @return 验证器Bean名称。
     */
    String validator();

    /**
     * 验证参数名。
     *
     * @return 验证参数名。
     */
    String parameter() default "";

    /**
     * 验证参数名集；同时验证多个参数，或多个参数之间进行关联验证。
     *
     * @return 验证参数名集。
     */
    String[] parameters() default {};

    /**
     * 是否允许为空。
     *
     * @return 是否允许为空。
     */
    boolean emptyable() default false;

    /**
     * 整数配置值数组。用于设置验证规则需要的整数参数值。
     *
     * @return 整数配置值数组。
     */
    int[] number() default {};

    /**
     * 字符串配置值数组。用于设置验证规则需要的字符串参数值。
     *
     * @return 字符串配置值数组。
     */
    String[] string() default {};

    /**
     * 验证失败错误编码。
     *
     * @return 验证失败错误编码。
     */
    int failureCode() default 0;

    /**
     * 验证失败错误信息资源key。
     *
     * @return 验证失败错误信息资源key。
     */
    String failureKey() default "";

    /**
     * 验证失败错误信息参数资源key。
     *
     * @return 验证失败错误信息参数资源key。
     */
    String[] failureArgKeys() default {};
}
```
Ctrl模块提供的验证规则：
```java
package org.lpw.tephra.ctrl.validate;

import org.lpw.tephra.ctrl.template.Template;

/**
 * 验证器集。
 *
 * @author lpw
 */
public interface Validators {
    /**
     * Bean名称前缀。
     */
    String PREFIX = "tephra.ctrl.validate.";

    /**
     * 不为空验证器Bean名称。如果要验证的参数值不为空则返回true，否则返回false。
     */
    String NOT_EMPTY = PREFIX + "not-empty";

    /**
     * 最大长度验证器Bean名称。如果要验证的参数值字符串长度不超过设置值则返回true；否则返回false。
     */
    String MAX_LENGTH = PREFIX + "max-length";

    /**
     * 格式验证器Bean名称。如果要验证的字符串格式合法则返回true；否则返回false。
     */
    String MATCH_REGEX = PREFIX + "match-regex";

    /**
     * Email格式验证器Bean名称。如果要验证的Email格式合法则返回true；否则返回false。
     */
    String EMAIL = PREFIX + "email";

    /**
     * 相同验证器Bean名称。如果要验证的两个参数值相同则返回true；否则返回false。
     */
    String EQUALS = PREFIX + "equals";

    /**
     * 不相同验证器Bean名称。如果要验证的两个参数值不相同则返回true；否则返回false。
     */
    String NOT_EQUALS = PREFIX + "not-equals";

    /**
     * 大于验证器Bean名称。如果要验证的参数值大于设置值则返回true；否则返回false。
     */
    String GREATER_THAN = PREFIX + "greater-than";

    /**
     * 小于验证器Bean名称。如果要验证的参数值小于设置值则返回true；否则返回false。
     */
    String LESS_THAN = PREFIX + "less-than";

    /**
     * 介于验证器Bean名称。如果要验证的参数值介于设置范围则返回true；否则返回false。
     */
    String BETWEEN = PREFIX + "between";
    /**
     * 日期时间格式验证器Bean名称。如果验证的参数为日期时间格式数据则返回true；否则返回false。
     */
    String DATE_TIME = PREFIX + "date-time";

    /**
     * 参数签名验证器Bean名称。如果参数签名验证合法则返回true；否则返回false。
     */
    String SIGN = PREFIX + "sign";
}
```
使用示例：
```java
@Execute(name = "info", validates = {
        @Validate(validator = Validators.MAX_LENGTH, parameter = "docTitle", number = {100}, failureCode = 1031, failureArgKeys = {"spark.user.setting.info.doc-title"}),
        @Validate(validator = Validators.MAX_LENGTH, parameter = "realname", number = {100}, failureCode = 1032, failureArgKeys = {"spark.user.setting.info.realname"}),
        @Validate(validator = Validators.MAX_LENGTH, parameter = "mobile", number = {100}, failureCode = 1033, failureArgKeys = {"spark.user.setting.info.mobile"}),
        @Validate(validator = Validators.MAX_LENGTH, parameter = "email", number = {100}, failureCode = 1034, failureArgKeys = {"spark.user.setting.info.email"}),
        @Validate(validator = Validators.EMAIL, emptyable = true, parameter = "email", failureCode = 1035, failureArgKeys = {"spark.user.setting.info.email"}),
        @Validate(validator = Validators.MAX_LENGTH, parameter = "address", number = {100}, failureCode = 1036, failureArgKeys = {"spark.user.setting.info.address"})})
public Object info() {
    return templates.get().success("", "spark.user.setting.info.saved");
}
```
可以在Execute中定义key，则failureArgKeys可自动设置为【key+"."+parameter】。如：
```java
@Execute(name = "info", key = "spark.user.setting.info", validates = {
        @Validate(validator = Validators.MAX_LENGTH, parameter = "docTitle", number = {100}, failureCode = 1031, failureArgKeys = {"spark.user.setting.info.doc-title"}),
        @Validate(validator = Validators.MAX_LENGTH, parameter = "realname", number = {100}, failureCode = 1032),
        @Validate(validator = Validators.MAX_LENGTH, parameter = "mobile", number = {100}, failureCode = 1033),
        @Validate(validator = Validators.MAX_LENGTH, parameter = "email", number = {100}, failureCode = 1034),
        @Validate(validator = Validators.EMAIL, emptyable = true, parameter = "email", failureCode = 1035),
        @Validate(validator = Validators.MAX_LENGTH, parameter = "address", number = {100}, failureCode = 1036)})
public Object info() {
    return templates.get().success("", "spark.user.setting.info.saved");
}
```
并且如果类Execute设置了key而执行服务未设置key，则使用类设置。
## 自定义参数验证规则
自定义验证器只需实现Validator接口，或继承ValidatorSupport，实现相应接口即可。如：
```java
package org.lpw.spark.user;
 
import org.lpw.tephra.ctrl.validate.ValidateWrapper;
import org.lpw.tephra.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

/**
 * @author lpw
 */
@Controller(UserService.VALIDATOR_USERNAME)
public class UsernameValidatorImpl extends ValidatorSupport {
    @Override
    protected String getDefaultFailureMessageKey() {
        return "spark.user.username.invalid";
    }
 
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return validator.isMatchRegex("1[0-9]{10}$", parameter) || validator.isEmail(parameter);
    }
}
```
使用自定义的验证器：
```java
@Execute(name = "sign-in", validates = {
        @Validate(validator = Validators.NOT_EMPTY, parameter = "username", failureCode = 1001, failureArgKeys = "spark.user.username"),
        @Validate(validator = Validators.NOT_EMPTY, parameter = "password", failureCode = 1002, failureArgKeys = "spark.user.password"),
        @Validate(validator = UserService.VALIDATOR_USERNAME, parameter = "username", failureCode = 1003),
        @Validate(validator = UserService.VALIDATOR_EXISTS, parameter = "username", failureCode = 1004)})
public Object signIn() {
    return 1;
}
```