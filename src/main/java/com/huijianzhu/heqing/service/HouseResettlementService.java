package com.huijianzhu.heqing.service;

import com.huijianzhu.heqing.entity.HqHouseResettlement;
import com.huijianzhu.heqing.vo.SystemResult;

import java.util.List;

/**
 * ================================================================
 * 说明：房屋动迁信息业务接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/13  13:26            创建
 * =================================================================
 **/
public interface HouseResettlementService {


    /**
     * 获取所有房屋动迁量信息集
     * @return
     */
    public SystemResult findAll();


    /**
     * 批量导入房屋动迁信息集
     * @param resettlements
     * @return
     */
    public SystemResult batchImport(List<HqHouseResettlement> resettlements);


}