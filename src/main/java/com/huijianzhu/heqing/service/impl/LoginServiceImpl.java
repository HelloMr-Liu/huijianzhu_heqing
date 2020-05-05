package com.huijianzhu.heqing.service.impl;

import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.cache.UserAccountCacheManager;
import com.huijianzhu.heqing.entity.HqUser;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.enums.USER_TABLE_FIELD_STATE;
import com.huijianzhu.heqing.mapper.extend.HqUserExtendMapper;
import com.huijianzhu.heqing.service.LoginService;
import com.huijianzhu.heqing.vo.SystemResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ================================================================
 * 说明：登录业务接口实现
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  22:23            创建
 * =================================================================
 **/
@Service
@Slf4j
public class LoginServiceImpl implements LoginService {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private HqUserExtendMapper hqUserExtendMapper;          //注入操作用户信息表mapper接口
    @Autowired
    private LoginTokenCacheManager loginCacheManager;       //注入登录标识缓存管理
    @Autowired
    private UserAccountCacheManager accountCacheManager;    //注入账号缓存管理


    /**
     * 用户登录
     * @param userAccount     用户账号
     * @param password        用户密码
     * @return
     */
    public SystemResult login(String userAccount, String password){
        //校验当前账号是否在账号缓存管理存在
        if(!accountCacheManager.checkAccountexist(userAccount)){
            //当前登录的账号在缓存中不存在
            return SystemResult.build(SYSTEM_RESULT_STATE.USER_LOGIN_ERROR.KEY,SYSTEM_RESULT_STATE.USER_LOGIN_ERROR.VALUE);
        }
        //判断用户名或密码是否在数据库中存在
        HqUser currentUser = hqUserExtendMapper.getUserByAccountAndPassword(userAccount, password, USER_TABLE_FIELD_STATE.DEL_FLAG_NO.KEY);
        if(currentUser==null){
            //当前用户信息在数据库中不存在所以无法登

        }
        return null;

    }

    /**
     * 用户退出登录
     * @return
     */
    public SystemResult loginOut(){
        return null;
    }




}
    
    
    