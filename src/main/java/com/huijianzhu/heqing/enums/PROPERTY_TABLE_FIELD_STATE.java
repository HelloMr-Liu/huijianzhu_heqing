package com.huijianzhu.heqing.enums;

/**
 * ================================================================
 * 说明：属性表对应的字段信息
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/7  13:28            创建
 * =================================================================
 **/
public enum PROPERTY_TABLE_FIELD_STATE {

    DEL_FLAG_YES("1", "代表删除"),
    DEL_FLAG_NO("0", "代表没有删除"),
    IS_PARENT("1", "代表是父属性"),
    NO_PARENT("0", "代表是子属性");

    public String KEY;
    public String VALUE;

    PROPERTY_TABLE_FIELD_STATE(String key, String value) {
        this.KEY = key;
        this.VALUE = value;
    }

}