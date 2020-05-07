package com.huijianzhu.heqing.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ================================================================
 * 说明：属性操作锁
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/7  13:48            创建
 * =================================================================
 **/
public class PropertyLock {

    /**
     * 属性修改锁
     */
    public static final ReentrantReadWriteLock PROPERTY_UPDATE_LOCK =
            new ReentrantReadWriteLock();
}
