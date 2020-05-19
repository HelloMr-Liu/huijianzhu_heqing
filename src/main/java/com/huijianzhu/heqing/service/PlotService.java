package com.huijianzhu.heqing.service;

import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.vo.SystemResult;

import java.util.List;

/**
 * ================================================================
 * 说明：操作地块信息业务接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  14:30            创建
 * =================================================================
 **/
public interface PlotService {

    /**
     * 获取所有与地块名称相关的地块信息默认是查询出所有
     *
     * @param plotName 关于地块的名称
     * @return
     */
    public SystemResult getPlotContentListByName(String plotName);

    /**
     * 获取指定id对应的地块信息
     *
     * @param plotId 某一个地块信息id
     * @return
     */
    public SystemResult getPlotDescById(String plotId);

    /**
     * 添加地块信息
     *
     * @param definition 封装了地块信息的实体对象
     * @return
     */
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws Exception;


    /**
     * 修改地块,地地块属性信息
     *
     * @param definition 封装了地块修改信息及对应的,地块属性信息
     * @return
     */
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws Exception;


    /**
     * 删除地块信息
     *
     * @param plotId
     * @return
     * @throws Exception
     */
    public SystemResult deleteById(Integer plotId) throws Exception;


    /**
     * 批量插入地块信息
     *
     * @param plots 地块信息集
     * @return
     */
    public SystemResult batchInsertPlots(List<HqPlot> plots);


}