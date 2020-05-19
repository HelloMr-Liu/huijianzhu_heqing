package com.huijianzhu.heqing.service;

import com.huijianzhu.heqing.entity.HqPipeAccount;
import com.huijianzhu.heqing.vo.SystemResult;

import java.util.List;

/**
 * ================================================================
 * 说明：操作管道搬迁费用业务接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/13  11:54            创建
 * =================================================================
 **/
public interface PipeAccountService {


    /**
     * 查询所有有效地块对应的管道搬迁费用信息集
     *
     * @return
     */
    public SystemResult findAll();


    /**
     * 批量管道管道搬迁信息集
     *
     * @param pipeAccounts
     */
    public SystemResult batchImport(List<HqPipeAccount> pipeAccounts);


}