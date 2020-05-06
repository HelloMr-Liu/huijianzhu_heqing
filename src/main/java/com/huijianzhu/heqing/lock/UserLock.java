package com.huijianzhu.heqing.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ================================================================
 * 说明：操作用户信息的锁
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  21:15            创建
 * =================================================================
 **/
public class  UserLock {

    /**
     * 用户修改锁
     */
    public static final ReentrantReadWriteLock USER_UPDATE_LOCK =
            new ReentrantReadWriteLock();

}
    
    
    