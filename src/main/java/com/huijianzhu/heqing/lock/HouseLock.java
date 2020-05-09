package com.huijianzhu.heqing.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ================================================================
 * 说明：操作地块房屋信息的锁对象
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  14:28            创建
 * =================================================================
 **/
public class HouseLock {

    /**
     * 房屋修改锁
     */
    public static final ReentrantReadWriteLock HOUSE_UPDATE_LOCK =
            new ReentrantReadWriteLock();
}
