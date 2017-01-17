package org.lpw.tephra.script;

import net.sf.json.JSONObject;

/**
 * @author lpw
 */
public interface ScriptService {
    /**
     * 脚本方法名是否存在验证器。
     */
    String VALIDATOR_EXISTS_METHOD = "tephra.script.validator.exists-method";

    /**
     * 验证JSON格式的数据是否有效。
     *
     * @param names     JavaScript验证器名称。
     * @param parameter JSON格式的参数值。
     * @return 验证结果。
     */
    JSONObject validate(String[] names, String parameter);
}
