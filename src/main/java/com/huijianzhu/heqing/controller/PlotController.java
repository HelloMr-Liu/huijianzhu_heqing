package com.huijianzhu.heqing.controller;

import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.service.PlotService;
import com.huijianzhu.heqing.vo.SystemResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ================================================================
 * 说明：土地概况请求接口控制器
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/9  16:52            创建
 * =================================================================
 **/
@CrossOrigin //支持跨域
@RestController
@RequestMapping("/landsurvey")
public class PlotController {

    @Autowired
    private PlotService plotService;

    /**
     * 获取所有与地块名称相关的地块信息默认是查询出所有
     * @param plotName   关于地块的名称
     * @return
     */
    @PostMapping("/show/landsurvey")
    public SystemResult getPlotContentListByName(String plotName){
        return plotService.getPlotContentListByName(plotName);
    }

    /**
     * 获取指定id对应的地块信息
     * @param plotId  某一个地块信息id
     * @return
     */
    @PostMapping("/show/landsurvey/one")
    public SystemResult getPlotDescById(String plotId){
        return plotService.getPlotDescById(plotId);
    }

    /**
     * 添加地块信息
     * @param definition 封装了地块信息的实体对象
     * @return
     */
    @PostMapping("/update/landsurvey/add")
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws  Exception{
        return plotService.add(definition);
    }


    /**
     * 修改地块,地地块属性信息
     * @param definition 封装了地块修改信息及对应的,地块属性信息
     * @return
     */
    @PostMapping("/update/landsurvey/update")
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws  Exception{
        return plotService.updateContent(definition);
    }


    /**
     * 删除地块信息
     * @param plotId
     * @return
     * @throws Exception
     */
    @PostMapping("/update/landsurvey/delete")
    public SystemResult deleteById(Integer plotId)throws  Exception{
        return plotService.deleteById(plotId);
    }
}
