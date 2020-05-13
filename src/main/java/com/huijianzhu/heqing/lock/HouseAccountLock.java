package com.huijianzhu.heqing.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ================================================================
 * 说明：非居住付款台账锁
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/13  13:00            创建
 * =================================================================
 **/
public class HouseAccountLock {


    /**
     * 修改锁
     */
    public static final ReentrantReadWriteLock UPDATE_LOCK =
            new ReentrantReadWriteLock();

}
