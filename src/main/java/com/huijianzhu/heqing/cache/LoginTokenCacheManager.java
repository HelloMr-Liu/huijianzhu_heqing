package com.huijianzhu.heqing.cache;

import cn.hutool.core.util.StrUtil;
import com.huijianzhu.heqing.pojo.UserLoginContent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ================================================================
 * 说明：登录标识缓存信息管理
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  19:54            创建
 * =================================================================
 **/
@Component
public class LoginTokenCacheManager {

    //创建一个存储登录标识对应的容器
    private ConcurrentHashMap<String, UserLoginContent> loginTokenCahce=new ConcurrentHashMap<>();

    /**
     * 创建一个定时任务每天晚上的23:59:59后将当前所有的用户登录标志进行失效管理清除
     */
    @Scheduled(cron = "59 59 23 ? * *")
    public void timingManagerCache(){
        //创建一个集合用户存储本次失效的登录标识Token
        List<String> failureTokenList=new ArrayList<>();

        //遍历缓存中的信息,判断哪些登录标识已经失效(默认一个效时)
        loginTokenCahce.entrySet().forEach(
            e->{
                //校验当前用户登录标识是否过期
                if(checkFailureToken(e.getValue().getLoginTime())){
                    //当前登录标识时间已经过期,将登陆标识存储到failureTokenList中
                    failureTokenList.add(e.getKey());
                }
            }
        );
        //遍历failureTokenList中标识对应loginTokenCahce中删除
        failureTokenList.forEach(
            e->{loginTokenCahce.remove(e);}
        );
    }

    /**
     * 用于校验登录标识是否失效，默认一个登录标识一个小时候算为失效
     * @param timeLong  一个long类型的时间
     * @return true:失效  false:没有失效
     */
    public boolean checkFailureToken(Long timeLong){
        //获取当前时间
        Long currentTime=new Date().getTime();
        //获取用户传递过来的登录标识时间与当前时间对应的插值 如果大于360,0000毫秒表示失效
        Long  timeDifference=currentTime-timeLong;
        return timeDifference>3600000?true:false;
    }

    /**
     * 校验当前登录标识是否已经失效
     * @param loginToken    用户登录标识
     * @return
     */
    public boolean checkValidLoginToken(String loginToken){
        //判断当前token是否还在缓存中
        if(StrUtil.hasBlank(loginToken) ||!loginTokenCahce.contains(loginToken)){
            //用户没有登录直接返回true
            return true;
        }
        //获取对应的登录时间对应这个时间的有效期
        return checkFailureToken(loginTokenCahce.get(loginToken).getLoginTime());
    }

    /**
     * 获取用户登录对应的id
     * @return
     */
    public Integer getLoginUserId(String loginToken){
        UserLoginContent userLoginContent = loginTokenCahce.get(loginToken);
        return userLoginContent.getUserId();
    }
}
    
    
    