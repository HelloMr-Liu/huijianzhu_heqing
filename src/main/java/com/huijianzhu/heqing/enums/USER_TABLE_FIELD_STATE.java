package com.huijianzhu.heqing.enums;

/**
 * ================================================================
 * 说明：用户表字段状态信息
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  19:46            创建
 * =================================================================
 **/
public enum USER_TABLE_FIELD_STATE {
    USER_TYPE_USER("0", "代表当前账号是用户类型"),
    USER_TYPE_MANAGER("1", "代表当前账号是管理员");
    public String KEY;
    public String VALUE;

    USER_TABLE_FIELD_STATE(String key, String value) {
        this.KEY = key;
        this.VALUE = value;
    }
}
    
    
    