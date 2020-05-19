package com.huijianzhu.heqing.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================================
 * 说明：封装用户登录的信息
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  21:34            创建
 * =================================================================
 **/
@Data
public class UserLoginContent {
    private long loginTime;             //用户登录时间
    private Integer userId;             //对应的用户id
    private String userName;           //对应的用户名
    private Integer userType;          //对应的用户类型
    private List<String> jurModelLis;   //用户权限id
    private List<ModelTree> modelTree;  //系统模块所有信息
}
    
    
    