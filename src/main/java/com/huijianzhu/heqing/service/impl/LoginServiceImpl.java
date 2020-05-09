package com.huijianzhu.heqing.service.impl;

import cn.hutool.core.util.StrUtil;
import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.cache.PermissionCacheManager;
import com.huijianzhu.heqing.cache.UserAccountCacheManager;
import com.huijianzhu.heqing.entity.HqUser;
import com.huijianzhu.heqing.enums.LOGIN_STATE;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.enums.USER_TABLE_FIELD_STATE;
import com.huijianzhu.heqing.mapper.extend.HqUserExtendMapper;
import com.huijianzhu.heqing.pojo.UserLoginContent;
import com.huijianzhu.heqing.service.LoginService;
import com.huijianzhu.heqing.utils.CookieUtils;
import com.huijianzhu.heqing.utils.MD5Utils;
import com.huijianzhu.heqing.utils.ShareCodeUtil;
import com.huijianzhu.heqing.vo.SystemResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    @Autowired
    private PermissionCacheManager permissionCacheManager;  //注入权限信息管理缓存


    /**
     * 用户登录
     * @param userAccount     用户账号
     * @param password        用户密码
     * @return
     */
    public SystemResult login(String userAccount, String password){
        //校验当前账号是否在账号缓存管理存在
        if(!accountCacheManager.checkAccountexist(userAccount,null)){
            //当前登录的账号在缓存中不存在
            return SystemResult.build(SYSTEM_RESULT_STATE.USER_LOGIN_ERROR.KEY,SYSTEM_RESULT_STATE.USER_LOGIN_ERROR.VALUE);
        }
        //对用输入的密码加密与数据库中的内容比较
        String encryption= MD5Utils.md5(password+"heqing");
        //判断用户名或密码是否在数据库中存在
        HqUser currentUser = hqUserExtendMapper.getUserByAccountAndPassword(userAccount, encryption, USER_TABLE_FIELD_STATE.DEL_FLAG_NO.KEY);
        if(currentUser==null){
            //当前用户信息在数据库中不存在所以无法登录
            return SystemResult.build(SYSTEM_RESULT_STATE.USER_LOGIN_ERROR.KEY,SYSTEM_RESULT_STATE.USER_LOGIN_ERROR.VALUE);
        }

        //判断当前用户权限信息
        if(currentUser.getUserType().equals(USER_TABLE_FIELD_STATE.USER_TYPE_USER)){
            //判断普通用户是否有对应的模块权限id信息
            if(StrUtil.hasBlank(currentUser.getPermissionsId())){
                //标识当前用户密码都正确但是没有任何权限所以不能访问该该系统
                return SystemResult.build(SYSTEM_RESULT_STATE.USER_LOGIN_PERMISSION.KEY,SYSTEM_RESULT_STATE.USER_LOGIN_PERMISSION.VALUE);
            }
        }

        //创建一个登录标识
        String login_Token= ShareCodeUtil.getUserToken();

        //将当前登录标识存储一份在客户端Cookie中默认浏览器关闭的时候Cookie就失效
        CookieUtils.setCookie(request,response, LOGIN_STATE.USER_LOGIN_TOKEN.toString(),login_Token);

        //创建一个用户登录封装信息
        UserLoginContent content=new UserLoginContent();
        content.setUserId(currentUser.getUserId());
        content.setUserName(currentUser.getUserName());
        content.setUserType(currentUser.getUserType());
        content.setLoginTime(new Date().getTime());

        //创建一个模块id集合存储当前登录用户对应的模块id信息
        List<String> modelIst=new ArrayList<>();
        if(StrUtil.hasBlank(currentUser.getPermissionsId())){
            //将用户模块id信息分割成一个数组存储到modelIst中
            modelIst= new ArrayList<>(Arrays.asList(currentUser.getPermissionsId().split(",")));
        }
        content.setJurModelLis(modelIst);
        content.setModelTree(permissionCacheManager.getModelTree());

        //将当前登录信息存储到登录标识缓存中
        loginCacheManager.getLoginTokenCahce().put(login_Token,content);

        //登录成功
        return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY,content);
    }

    /**
     * 用户退出登录
     * @return
     */
    public SystemResult loginOut(){
        //获取本地对应的登录标识
        String loginToken = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());

        System.out.println(loginToken);

        //删除本地登录标识
        CookieUtils.deleteCookie(request,response,LOGIN_STATE.USER_LOGIN_TOKEN.toString());

        if(loginToken!=null){
            //删除登录缓存标识中对应的信息
            loginCacheManager.removeCacheContentByUserToken(loginToken);
        }

        //删除成功
        return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY,SYSTEM_RESULT_STATE.SUCCESS.VALUE);
    }
}
    
    
    