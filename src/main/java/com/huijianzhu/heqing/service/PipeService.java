package com.huijianzhu.heqing.service;

import com.huijianzhu.heqing.definition.HqPlotPipeDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.entity.HqPlotHouse;
import com.huijianzhu.heqing.entity.HqPlotPipe;
import com.huijianzhu.heqing.vo.SystemResult;

import java.util.List;

/**
 * ================================================================
 * 说明：操作管道搬迁信息业务接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  14:30            创建
 * =================================================================
 **/
public interface PipeService {

    /**
     * 获取所有与管道搬迁名称相关的管道信息默认是查询出所有
     * @param pipeName   关于管道的名称
     * @return
     */
    public SystemResult getPipeContentListByName(String pipeName);

    /**
     * 获取指定id对应的管道信息
     * @param pipeId  某一个管道信息id
     * @return
     */
    public SystemResult getPipeDescById(String pipeId);

    /**
     * 添加管道搬迁信息
     * @param definition 封装了管道搬迁信息的实体对象
     * @return
     */
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws  Exception;


    /**
     * 修改管道搬迁,管道搬迁属性信息
     * @param definition 封装了管道搬迁修改信息及对应的,管道搬迁属性信息
     * @return
     */
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws  Exception;


    /**
     * 删除管道搬迁信息
     * @param pipeId  管道搬迁对应的id
     * @return
     * @throws Exception
     */
    public SystemResult deleteById(Integer pipeId)throws  Exception;


    /**
     * 批量插入管道搬迁信息
     * @param pipes  管道搬迁信息集
     * @return
     */
    public SystemResult batchInsertPipes(List<HqPlotPipeDefinition> pipes);

}