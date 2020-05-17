package com.huijianzhu.heqing.controller;

import com.huijianzhu.heqing.definition.PropertyAccpetDefinition;
import com.huijianzhu.heqing.service.PropertyService;
import com.huijianzhu.heqing.vo.SystemResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ================================================================
 * 说明：属性请求接口控制器
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/7  16:12            创建
 * =================================================================
 **/
@Slf4j      //日志使用
@Validated  //数据校验
@RestController
@RequestMapping("/property")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;        //注入配置属性操作业务接口

    /**
     * 获取指定属性名称下的属性信息,如果不传递属性名称默认是查询所有
     * @param propertyName  属性名称
     * @param propertyType  属性类型
     * @return
     */
    @PostMapping("/get/properties/name")
    public SystemResult getPropertiesByName(String propertyName,String propertyType){
        return propertyService.getPropertiesByName(propertyName,propertyType);
    }


    /**
     * 获取填写方式信息集合
     * @return
     */
    @PostMapping("/get/write/way/list")
    public SystemResult getWriteWayList(){
        return propertyService.getWriteWayList();
    }


    /**
     * 添加属性信息
     * @param definition    封装添加属性对应的信息
     * @return
     */
    @PostMapping("/add/property")
    public SystemResult addProperty(PropertyAccpetDefinition definition)throws  Exception{
        return propertyService.addProperty(definition);
    }


    /**
     * 获取指定属性id对应属性
     * @param propertyId
     * @return
     */
    @PostMapping("/get/property/id")
    public SystemResult getPropertyById(Integer propertyId){
        return propertyService.getPropertyById(propertyId);
    }


    /**
     * 修改属性信息
     * @param definition  封装修改属性对应的信息
     * @return
     */
    @PostMapping("/update/property")
    public SystemResult updateProperty(PropertyAccpetDefinition definition)throws  Exception{
        return propertyService.updateProperty(definition);
    }


    /**
     * 删除属性信息
     * @param propertyId   属性id
     * @return
     */
    @PostMapping("/delete/property/id")
    public SystemResult deleteProperty(Integer propertyId)throws  Exception{
        return propertyService.deleteProperty(propertyId);
    }
}
