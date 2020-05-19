package com.huijianzhu.heqing.service;

import com.huijianzhu.heqing.entity.HqNoLiveAccount;
import com.huijianzhu.heqing.vo.SystemResult;

import java.util.List;

/**
 * ================================================================
 * 说明：非居住付款台账业务接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/12  16:26            创建
 * =================================================================
 **/
public interface NoLiveService {


    /**
     * 批量导入非居住付款台账信息
     *
     * @param noLiveList
     * @return
     */
    public SystemResult batchImport(List<HqNoLiveAccount> noLiveList);


    /**
     * 获取所有非居住付款台账信息集合
     *
     * @return
     */
    public SystemResult findAll();
}
