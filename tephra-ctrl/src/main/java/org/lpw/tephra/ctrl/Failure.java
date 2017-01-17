package org.lpw.tephra.ctrl;

/**
 * @author lpw
 */
public enum Failure {
    /**
     * 无权限。
     */
    NotPermit("tephra.ctrl.not-permit"),
    /**
     * 系统繁忙。
     */
    Busy("tephra.ctrl.busy"),
    /**
     * 运行期异常。
     */
    Exception("tephra.ctrl.exception");

    private String messageKey;

    Failure(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
