package com.huijianzhu.heqing.enums;

/**
 * ================================================================
 * 说明：用于整个系统操作对应的状态码信息
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  20:30            创建
 * =================================================================
 **/
public enum SYSTEM_RESULT_STATE {

    SUCCESS(200,"本次操作成功"),
    ERROR(500,"系统内部出现异常"),
    VALIED_ERROR(100,"接收参数异常"),
    USER_ACCOUNT_EXITE(601,"该账号已经存在,不能重复"),
    USER_LOGIN_ERROR(602,"请输入正确的用户名或密码");


    public Integer KEY;    //反馈的状态码
    public String VALUE;     //反馈的描述信息
    SYSTEM_RESULT_STATE(Integer state,String mess){
        this.KEY=state;
        this.VALUE=mess;
    }
}
    
    
    