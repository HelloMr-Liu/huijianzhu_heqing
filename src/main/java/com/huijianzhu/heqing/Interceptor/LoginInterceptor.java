package com.huijianzhu.heqing.Interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.github.pagehelper.PageInfo;
import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.cache.PermissionCacheManager;
import com.huijianzhu.heqing.enums.LOGIN_STATE;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.enums.USER_TABLE_FIELD_STATE;
import com.huijianzhu.heqing.pojo.UserLoginContent;
import com.huijianzhu.heqing.utils.CookieUtils;
import com.huijianzhu.heqing.vo.SystemResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.List;

/**
 * ================================================================
 * 说明：实现登录拦截器
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/6  11:20            创建
 * =================================================================
 **/
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTokenCacheManager loginTokenCacheManager;      //注入用户登录标识管理缓存

    @Autowired
    private PermissionCacheManager permissionCacheManager;      //注入模块信息管理缓存

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {       //请求进入这个拦截器
        //设置缓存区编码为UTF-8编码格式
        response.setCharacterEncoding("UTF-8");
        //在响应中主动告诉浏览器使用UTF-8编码格式来接收数据
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        //可以使用封装类简写Content-Type，使用该方法则无需使用setCharacterEncoding
        response.setContentType("text/html;charset=UTF-8");


        //获取当前登录本地登录标识
        String loginToken = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
        log.info("当前的登录标志是：" + loginToken);

        if (!StrUtil.hasBlank(loginToken)/*&&request.getHeader("x-requested-with").equalsIgnoreCase("XMLHttpRequest")*/) {
            //判断当前登录标识在缓存中是否已经过期
            boolean isValid = loginTokenCacheManager.checkValidLoginToken(loginToken);
            if (!isValid) {
                //获取当前登录标识对应用户登录信息
                UserLoginContent userContent = loginTokenCacheManager.getCacheUserByLoginToken(loginToken);

                //将用户信息存储到本次请求中,以及对应的登录标识
                request.setAttribute(LOGIN_STATE.USER_LOGIN_TOKEN.toString(), userContent);
                request.setAttribute("TOKEN", loginToken);

                //判断当前用户是否是管理员
                if (userContent.getUserType().toString().equals(USER_TABLE_FIELD_STATE.USER_TYPE_MANAGER.KEY)) {
                    //由于当前登录的用户是管理员所以当前系统请求都可以访问
                    return true;  //放行
                } else {
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
                    if (pathArray.length > 3) {

                        //进行请求路径拼接,获取对应缓存中的模块id
                        // String requestPath=servletPath.lastIndexOf("?")==-1?servletPath:servletPath.trim().substring(0,servletPath.lastIndexOf("?"));
                        String requestPath = "/" + pathArray[1] + "/" + pathArray[2] + "/" + pathArray[3];

                        //获取当前请求的模块id
                        String modelId = permissionCacheManager.getPermissionRequestCache().get(requestPath);

                        //判断该用户登录对应的登录用户信息中是否有包含该modelId
                        List<String> userLoginModel = userContent.getJurModelLis();
                        if (userLoginModel.contains(modelId)) {
                            //用户包含该模块id信息所以有权访问
                            return true;
                        }
                    }
                }
            }
        }
        //没有权限
        PrintWriter writer = response.getWriter();
        writer.write(JSONUtil.toJsonStr(SystemResult.build(SYSTEM_RESULT_STATE.USER_LOGIN_PERMISSION.KEY, SYSTEM_RESULT_STATE.USER_LOGIN_PERMISSION.VALUE)));
        writer.flush();
        writer.close();
        return false; //直接拦截
    }
}
