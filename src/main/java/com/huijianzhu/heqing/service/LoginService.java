package com.huijianzhu.heqing.service;

import com.huijianzhu.heqing.vo.SystemResult;

/**
 * ================================================================
 * 说明：登录业务接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  22:21            创建
 * =================================================================
 **/
public interface LoginService {


    /**
     * 用户登录
     *
     * @param userAccount 用户账号
     * @param password    用户密码
     * @return
     */
    public SystemResult login(String userAccount, String password);

    /**
     * 用户退出登录
     *
     * @return
     */
    public SystemResult loginOut();

}
    
    
    