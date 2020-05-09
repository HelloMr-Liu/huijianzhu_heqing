package com.huijianzhu.heqing.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ================================================================
 * 说明：操作管道信息的锁对象
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  14:29            创建
 * =================================================================
 **/
public class PipeLock {
    /**
     * 管道修改锁
     */
    public static final ReentrantReadWriteLock PIPE_UPDATE_LOCK =
            new ReentrantReadWriteLock();
}
