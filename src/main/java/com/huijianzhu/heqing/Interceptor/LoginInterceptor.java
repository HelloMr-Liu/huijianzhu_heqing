package com.huijianzhu.heqing.Interceptor;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.cache.PermissionCacheManager;
import com.huijianzhu.heqing.enums.LOGIN_STATE;
import com.huijianzhu.heqing.enums.USER_TABLE_FIELD_STATE;
import com.huijianzhu.heqing.pojo.UserLoginContent;
import com.huijianzhu.heqing.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * ================================================================
 * 说明：实现登录拦截器
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/6  11:20            创建
 * =================================================================
 **/
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTokenCacheManager loginTokenCacheManager;      //注入用户登录标识管理缓存

    @Autowired
    private PermissionCacheManager permissionCacheManager;      //注入模块信息管理缓存


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {       //请求进入这个拦截器

        //获取当前登录本地登录标识
        String loginToken = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());

        if(!StrUtil.hasBlank(loginToken)&&request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")){
            //判断当前登录标识在缓存中是否已经过期
            boolean isValid = loginTokenCacheManager.checkValidLoginToken(loginToken);
            if(isValid){
                //登录标识已经过期请重新登录所以直接拦截
                return false;
            }
            //获取当前登录标识对应用户登录信息
            UserLoginContent userContent = loginTokenCacheManager.getCacheUserByLoginToken(loginToken);

            //判断当前用户是否是管理员
            if(userContent.getUserType().equals(USER_TABLE_FIELD_STATE.USER_TYPE_MANAGER.KEY)){
                //由于当前登录的用户是管理员所以当前系统请求都可以访问
                return true;  //放行
            }else{
                /**
                 * http://localhost:8080/heqing/login/login
                 *
                 * request.getRequestURI(); :/heqing/login/login  //包含项目名
                 * request.getServletPath();:/login/login         //不包含项目名
                 */
                //获取当前请求路径
                String servletPath = request.getServletPath();
                //分割当前请求路径
                String[] pathArray = servletPath.trim().split("/");
                //pathArray长度要大于3不然访问的接口无权访问
                if(pathArray.length>3){

                    //进行请求路径拼接,获取对应缓存中的模块id
                   // String requestPath=servletPath.lastIndexOf("?")==-1?servletPath:servletPath.trim().substring(0,servletPath.lastIndexOf("?"));
                    String requestPath="/"+pathArray[1]+"/"+pathArray[2]+"/"+pathArray[3];

                    //获取当前请求的模块id
                    String modelId = permissionCacheManager.getPermissionRequestCache().get(requestPath);

                    //判断该用户登录对应的登录用户信息中是否有包含该modelId
                    List<String> userLoginModel = userContent.getJurModelLis();
                    if(userLoginModel.contains(modelId)){
                        //用户包含该模块id信息所以有权访问
                        return true;
                    }
                    return false; //直接拦截
                }else{
                    return false; //直接拦截
                }
            }
        }else{
            //默认只能用ajax请求普通http请求不能访问
            //由于当前没有登录标识所以无权访问该系统请求
            return false; //直接拦截
        }
    }
}
