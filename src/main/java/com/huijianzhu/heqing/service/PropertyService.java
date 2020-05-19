package com.huijianzhu.heqing.service;

import com.huijianzhu.heqing.definition.PropertyAccpetDefinition;
import com.huijianzhu.heqing.vo.SystemResult;

/**
 * ================================================================
 * 说明：操作配置属性信息业务接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/7  13:24            创建
 * =================================================================
 **/
public interface PropertyService {


    /**
     * 获取指定属性名称下的属性信息,如果不传递属性名称默认是查询所有
     *
     * @param propertyName 属性名称
     * @param propertyType 属性类型
     * @return
     */
    public SystemResult getPropertiesByName(String propertyName, String propertyType);


    /**
     * 获取填写方式信息集合
     *
     * @return
     */
    public SystemResult getWriteWayList();


    /**
     * 添加属性信息
     *
     * @param definition 封装添加属性对应的信息
     * @return
     */
    public SystemResult addProperty(PropertyAccpetDefinition definition) throws Exception;


    /**
     * 获取指定属性id对应属性
     *
     * @param propertyId
     * @return
     */
    public SystemResult getPropertyById(Integer propertyId);


    /**
     * 修改属性信息
     *
     * @param definition 封装修改属性对应的信息
     * @return
     */
    public SystemResult updateProperty(PropertyAccpetDefinition definition) throws Exception;


    /**
     * 删除属性信息
     *
     * @param propertyId 属性id
     * @return
     */
    public SystemResult deleteProperty(Integer propertyId) throws Exception;
}
