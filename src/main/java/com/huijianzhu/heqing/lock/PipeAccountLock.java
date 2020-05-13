package com.huijianzhu.heqing.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ================================================================
 * 说明：当前类说说明
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/13  12:59            创建
 * =================================================================
 **/
public class PipeAccountLock {


    /**
     * 修改锁
     */
    public static final ReentrantReadWriteLock UPDATE_LOCK =
            new ReentrantReadWriteLock();

}
