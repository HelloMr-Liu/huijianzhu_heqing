package com.huijianzhu.heqing.enums;

/**
 * ================================================================
 * 说明：全局表字段状态信息
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  13:16            创建
 * =================================================================
 **/
public enum GLOBAL_TABLE_FILED_STATE {

    DEL_FLAG_YES("1", "代表当前记录被删除"),
    DEL_FLAG_NO("0", "代表当前记录没有被删除");
    public String KEY;       //反馈的值
    public String VALUE;     //反馈的描述信息

    GLOBAL_TABLE_FILED_STATE(String state, String mess) {
        this.KEY = state;
        this.VALUE = mess;
    }
}