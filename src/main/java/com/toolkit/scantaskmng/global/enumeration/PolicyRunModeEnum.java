package com.toolkit.scantaskmng.global.enumeration;

public enum PolicyRunModeEnum {
//    1：python脚本；
//    2：linux shell脚本；
//    3：windows批处理文件；
//    4：命令行；
//    5：内置功能模块；
//    99：未知模式。
    PYTHON_SCRIPTS(1),
    SH_SCRIPTS(2),
    WIN_BATCH(3),
    COMMAND(4),
    INLINE_MODULE(5),
    UNKNOWN(99),
    ;

    private int mode;

    PolicyRunModeEnum(int mode) { this.mode = mode; }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

}
