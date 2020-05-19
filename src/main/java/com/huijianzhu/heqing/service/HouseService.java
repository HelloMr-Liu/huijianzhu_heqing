package com.huijianzhu.heqing.service;

import com.huijianzhu.heqing.definition.HqPlotHouseDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.entity.HqPlotHouse;
import com.huijianzhu.heqing.vo.SystemResult;

import java.util.List;

/**
 * ================================================================
 * 说明：操作房屋搬迁信息业务接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  14:30            创建
 * =================================================================
 **/
public interface HouseService {

    /**
     * 获取所有与房屋搬迁名称相关的房屋信息默认是查询出所有
     *
     * @param houseName 关于房屋的名称
     * @return
     */
    public SystemResult getHosueContentListByName(String houseName);

    /**
     * 获取指定id对应的房屋信息
     *
     * @param houseId 某一个房屋信息id
     * @return
     */
    public SystemResult getHouseDescById(String houseId);

    /**
     * 添加房屋搬迁信息
     *
     * @param definition 封装了房屋搬迁信息的实体对象
     * @return
     */
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws Exception;


    /**
     * 修改房屋搬迁,房屋搬迁属性信息
     *
     * @param definition 封装了房屋搬迁修改信息及对应的,房屋搬迁属性信息
     * @return
     */
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws Exception;


    /**
     * 删除房屋搬迁信息
     *
     * @param houseId 房屋搬迁对应的id
     * @return
     * @throws Exception
     */
    public SystemResult deleteById(Integer houseId) throws Exception;


    /**
     * 批量插入房屋动迁信息
     *
     * @param houses 房屋动迁信息集
     * @return
     */
    public SystemResult batchInsertHouses(List<HqPlotHouseDefinition> houses);


}