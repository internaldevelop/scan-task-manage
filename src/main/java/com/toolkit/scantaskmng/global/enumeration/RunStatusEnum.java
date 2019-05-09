package com.toolkit.scantaskmng.global.enumeration;

public enum RunStatusEnum {
    NOT_RUN(0),     // 未执行
    RUNNING(1),     // 执行中
    COMPLETE(2),    //执行完成
    ;

    private int status;

    RunStatusEnum(int status) { this.status = status; }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
