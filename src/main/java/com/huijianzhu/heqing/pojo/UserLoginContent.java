package com.huijianzhu.heqing.pojo;

import lombok.Data;

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
    private long loginTime;     //用户登录时间
    private Integer userId;     //对应的用户id

}
    
    
    