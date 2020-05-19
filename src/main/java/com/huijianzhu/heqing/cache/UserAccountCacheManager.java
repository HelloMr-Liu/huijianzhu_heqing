package com.huijianzhu.heqing.cache;

import com.huijianzhu.heqing.entity.HqUser;
import com.huijianzhu.heqing.enums.GLOBAL_TABLE_FILED_STATE;
import com.huijianzhu.heqing.enums.USER_TABLE_FIELD_STATE;
import com.huijianzhu.heqing.mapper.extend.HqUserExtendMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ================================================================
 * 说明：用户账号缓存管理类型
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  20:55            创建
 * =================================================================
 **/

@Component
@Data
public class UserAccountCacheManager {

    @Autowired
    private HqUserExtendMapper hqUserExtendMapper;                              //注入操作用户表mapper接口

    private ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();   //存储账号的容器

    /**
     * 初始化容器
     */
    @PostConstruct
    public void initCache() {
        refresh();
    }

    /**
     * 刷新当前账号容器
     */
    public void refresh() {
        getValidUserAccount();
    }

    /**
     * 获取有效账号信息
     *
     * @return
     */
    public List<String> getValidAccountList() {
        return cache.entrySet().stream().map(e -> {
            return e.getKey();
        }).collect(Collectors.toList());
    }

    /**
     * 判断账号是否存在
     *
     * @param account 新账号
     * @return true:账号存在  false:账号不存在
     */
    public boolean checkAccountexist(String account, String userId) {
        String accountUserId = cache.get(account);
        if (accountUserId != null) {
            return accountUserId.equals(userId) ? false : true;
        }
        return false;
    }

    /**
     * 获取有效用户账号信息并刷新到容器中
     */
    private void getValidUserAccount() {
        //获取有效账号集合
        List<HqUser> validList = hqUserExtendMapper.getValidAccountAndUserId(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        //遍历账号集合并刷新到容器中
        validList.forEach(
                e -> {
                    cache.put(e.getUserAccount(), e.getUserId().toString());
                }
        );
    }

    /**
     * 校验当前用户id再账号缓存中是否存在
     *
     * @param userId
     * @return
     */
    public boolean checkAccountByUserId(String userId) {
        Set<Map.Entry<String, String>> entries = cache.entrySet();
        for (Map.Entry<String, String> entity : entries) {
            if (entity.getValue().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
    
    
    