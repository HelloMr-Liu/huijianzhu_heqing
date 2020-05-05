package com.huijianzhu.heqing.definition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ================================================================
 * 说明：用户信息属性接受类
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  15:09            创建
 * =================================================================
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccpetDefinition {

    private Integer userId;         //用户id
    private Integer userType;       //用户类型
    private Integer phoneNumber;    //用户电话

    private String  email;          //用户邮件
    private String  userName;       //用户名称
    private String  userAccount;    //用户账号
    private String  permissionsId;  //权限id
    private String  password;       //用户密码
}
    
    
    