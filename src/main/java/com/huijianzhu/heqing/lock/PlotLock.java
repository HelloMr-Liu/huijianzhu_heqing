package com.huijianzhu.heqing.lock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ================================================================
 * 说明：用于对地块信息操作的锁对象
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  14:27            创建
 * =================================================================
 **/
public class PlotLock {

    /**
     * 地块修改锁
     */
    public static final ReentrantReadWriteLock PLOT_UPDATE_LOCK =
            new ReentrantReadWriteLock();
}
