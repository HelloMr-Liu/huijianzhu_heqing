package com.huijianzhu.heqing.enums;

/**
 * ================================================================
 * 说明： 封装区分当前是地块还是房屋还是管道
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  13:10            创建
 * =================================================================
 **/
public enum PLOT_HOUSE_PIPE_TYPE {
    PLOT_TYPE("1", "代表当前是地块类型信息"),
    HOUSE_TYPE("2", "代表当前是地块房屋类型信息"),
    PIPE_TYPE("3", "代表当前是地块管道类型信息");
    public String KEY;       //反馈的值
    public String VALUE;     //反馈的描述信息

    PLOT_HOUSE_PIPE_TYPE(String state, String mess) {
        this.KEY = state;
        this.VALUE = mess;
    }
}
