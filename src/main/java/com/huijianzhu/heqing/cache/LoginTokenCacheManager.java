package com.huijianzhu.heqing.cache;

import cn.hutool.core.util.StrUtil;
import com.huijianzhu.heqing.entity.HqUser;
import com.huijianzhu.heqing.mapper.extend.HqUserExtendMapper;
import com.huijianzhu.heqing.pojo.UserLoginContent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
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
@Data
@Slf4j
public class LoginTokenCacheManager {


    @Autowired
    private HqUserExtendMapper hqUserExtendMapper;      //注入操作用户表信息mapper接口


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
        log.info("===========对一些失效的登录标识给清除=====失效的有"+failureTokenList.size());
        //遍历failureTokenList中标识对应loginTokenCahce中删除
        failureTokenList.forEach(
                e->{loginTokenCahce.remove(e);}
        );
}

    /**
     * 校验当前登录标识是否已经失效
     * @param loginToken    用户登录标识
     * @return true:失效  false:没有失效
     */
    public boolean checkValidLoginToken(String loginToken){
        //判断当前token是否还在缓存中
        if(StrUtil.hasBlank(loginToken) ||!loginTokenCahce.containsKey(loginToken)){
            //用户没有登录直接返回true
            return true;
        }
        //获取对应的登录时间对应这个时间的有效期
        return checkFailureToken(loginTokenCahce.get(loginToken).getLoginTime());
    }

    /**
     * 用于校验登录标识是否失效，默认一个登录标识一个小时候算为失效
     * @param timeLong  一个long类型的时间
     * @return true:失效  false:没有失效
     */
    private boolean checkFailureToken(Long timeLong){
        //获取当前时间
        Long currentTime=new Date().getTime();
        //获取用户传递过来的登录标识时间与当前时间对应的插值 如果大于360,0000毫秒表示失效
        Long  timeDifference=currentTime-timeLong;
        return timeDifference>3600000?true:false;
    }

    /**
     * 删除指定用户登录标识缓存信息
     * @param userToken
     */
    public void removeCacheContentByUserToken(String userToken){
        loginTokenCahce.remove(userToken);
    }


    /**
     * 获取缓存中是否有该用户登录标识对应的用户,该方法一般用在于权限校验使用,操作其他用户时候用的到
     * @param   userToken    用户登录标识
     * @return 返回封装好的登录用户对应的登录信息实体
     */
    public UserLoginContent getCacheUserByLoginToken(String  userToken){
        Set<Map.Entry<String, UserLoginContent>> entries = loginTokenCahce.entrySet();
        for(Map.Entry<String, UserLoginContent> currentUser:entries){
            if(currentUser.getKey().equals(userToken)){
                return currentUser.getValue();
            }
        }
        return null;
    }

    /**
     * 刷新用户id对应登录用户信息方法
     * @param userId
     */
    public void refreshloginUserInfo(Integer userId){

        //创建一个集合存储当前用户id对应在缓存中的登录标识
        List<String> loginTokenByUserIdList=new ArrayList<>();
        loginTokenCahce.entrySet().stream().forEach(
            e->{
               if(e.getValue().getUserId().equals(userId)) {
                   loginTokenByUserIdList.add(e.getKey());
               }
            }
        );

        //判断本次用户id在登录标识缓存中是否存在
        if(loginTokenByUserIdList.size()>0){
            //创建一个存储用户最新modelid集合
            List<String> newModelIdList=new ArrayList<>();

            //表示有对应的登录缓存标识信息，获取最新用户信息
            HqUser hqUser = hqUserExtendMapper.selectByPrimaryKey(userId);

            //将用户信息权限id分割获取
            String[] split = hqUser.getPermissionsId().trim().split(",");
            Arrays.asList(split).stream().forEach(
                e->{
                   if(!StrUtil.hasBlank(e)) {
                       //将最新用户模块id存储到newModelIdList集合中
                       newModelIdList.add(e);
                   }
                }
            );

            //遍历loginTokenByUserId中对应的当前用户id标识
            loginTokenByUserIdList.forEach(
                e->{
                    UserLoginContent userLoginContent = loginTokenCahce.get(e);

                    //更新用户类型
                    userLoginContent.setUserType(hqUser.getUserType());

                    //更新最新的用户名称信息
                    userLoginContent.setUserName(hqUser.getUserName());

                    //更新当前最新模块id信息到对应的用户id的用户信息中
                    userLoginContent.setJurModelLis(newModelIdList);
                }
            );
        }
    }






}
    
    
    