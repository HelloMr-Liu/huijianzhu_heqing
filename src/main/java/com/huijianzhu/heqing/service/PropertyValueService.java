package com.huijianzhu.heqing.service;

import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.pojo.AccpetPlotTypePropertyValue;
import com.huijianzhu.heqing.vo.SystemResult;

import java.util.List;

/**
 * ================================================================
 * 说明：操作属性值信息业务接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  11:48            创建
 * =================================================================
 **/
public interface PropertyValueService {



    /**
     * 添加属性值
     * @param propertyValues    某一个地块类型对应的一组属性对应的属性值信息集
     * @return
     */
    public SystemResult updatePropertyValue(List<AccpetPlotTypePropertyValue> propertyValues)throws  Exception;


}
