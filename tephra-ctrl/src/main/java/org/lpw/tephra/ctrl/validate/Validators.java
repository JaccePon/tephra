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
     * ID验证器Bean名称。
     */
    String ID = PREFIX + "id";

    /**
     * 参数签名验证器Bean名称。如果参数签名验证合法则返回true；否则返回false。
     */
    String SIGN = PREFIX + "sign";
    /**
     * 可信任IP验证器Bean名称。如果请求IP在可信任IP名单内则返回true；否则返回false。
     */
    String TRUSTFUL_IP = PREFIX + "trustful-ip";

    /**
     * 验证参数是否合法。
     *
     * @param validates 验证规则集。
     * @param template  模板实例 。
     * @return 验证不合法结果值。如果验证合法则返回null，否则返回验证失败结果。
     */
    Object validate(Validate[] validates, Template template);

    /**
     * 验证参数是否合法。
     *
     * @param validates 验证规则集。
     * @param template  模板实例 。
     * @return 验证不合法结果值。如果验证合法则返回null，否则返回验证失败结果。
     */
    Object validate(ValidateWrapper[] validates, Template template);
}
