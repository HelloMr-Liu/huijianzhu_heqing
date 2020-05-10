package com.huijianzhu.heqing.controller;

import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.service.HouseService;
import com.huijianzhu.heqing.vo.SystemResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ================================================================
 * 说明：房屋动迁请求接口控制器
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/10  16:52            创建
 * =================================================================
 **/
@CrossOrigin //支持跨域
@RestController
@RequestMapping("/resettlement")
public class HosueController {

    @Autowired
    private HouseService houseService;      //房屋动迁业务接口

    /**
     * 获取所有与房屋名称相关的房屋信息默认是查询出所有
     * @param houseName   关于房屋的名称
     * @return
     */
    @PostMapping("/show/resettlement")
    public SystemResult gethouseContentListByName(String houseName){
        return houseService.getHosueContentListByName(houseName);
    }

    /**
     * 获取指定id对应的房屋信息
     * @param houseId  某一个房屋信息id
     * @return
     */
    @PostMapping("/show/resettlement/one")
    public SystemResult gethouseDescById(String houseId){
        return houseService.getHouseDescById(houseId);
    }

    /**
     * 添加房屋信息
     * @param definition 封装了房屋信息的实体对象
     * @return
     */
    @PostMapping("/update/resettlement/add")
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws  Exception{
        return houseService.add(definition);
    }


    /**
     * 修改房屋,地房屋属性信息
     * @param definition 封装了房屋修改信息及对应的,房屋属性信息
     * @return
     */
    @PostMapping("/update/resettlement/update")
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws  Exception{
        return houseService.updateContent(definition);
    }

    /**
     * 删除房屋信息
     * @param houseId
     * @return
     * @throws Exception
     */
    @PostMapping("/update/resettlement/delete")
    public SystemResult deleteById(Integer houseId)throws  Exception{
        return houseService.deleteById(houseId);
    }
}
