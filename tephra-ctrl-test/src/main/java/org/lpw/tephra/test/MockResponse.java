package org.lpw.tephra.test;

import net.sf.json.JSONObject;
import org.lpw.tephra.ctrl.context.ResponseAdapter;

/**
 * @author lpw
 */
public interface MockResponse extends ResponseAdapter {
    /**
     * 获取返回内容类型。
     *
     * @return 返回内容类型。
     */
    String getContentType();

    /**
     * 获取跳转地址。
     *
     * @return 跳转地址。
     */
    String getRedirectTo();

    /**
     * 获取JSON格式的输出。
     *
     * @return 输出。
     */
    JSONObject asJson();
}
