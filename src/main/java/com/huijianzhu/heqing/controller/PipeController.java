package com.huijianzhu.heqing.controller;

import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.service.PipeService;
import com.huijianzhu.heqing.vo.SystemResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ================================================================
 * 说明：管道搬迁请求接口控制器
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/10  16:52            创建
 * =================================================================
 **/
@CrossOrigin //支持跨域
@RestController
@RequestMapping("/removal")
public class PipeController {

    @Autowired
    private PipeService PipeService;      //管道搬迁业务接口

    /**
     * 获取所有与管道名称相关的管道信息默认是查询出所有
     * @param PipeName   关于管道的名称
     * @return
     */
    @PostMapping("/show/removal")
    public SystemResult getPipeContentListByName(String PipeName){
        return PipeService.getPipeContentListByName(PipeName);
    }

    /**
     * 获取指定id对应的管道信息
     * @param PipeId  某一个管道信息id
     * @return
     */
    @PostMapping("/show/removal/one")
    public SystemResult getPipeDescById(String PipeId){
        return PipeService.getPipeDescById(PipeId);
    }

    /**
     * 添加管道信息
     * @param definition 封装了管道信息的实体对象
     * @return
     */
    @PostMapping("/update/removal/add")
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws  Exception{
        return PipeService.add(definition);
    }


    /**
     * 修改管道,地管道属性信息
     * @param definition 封装了管道修改信息及对应的,管道属性信息
     * @return
     */
    @PostMapping("/update/removal/update")
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws  Exception{
        return PipeService.updateContent(definition);
    }

    /**
     * 删除管道信息
     * @param PipeId
     * @return
     * @throws Exception
     */
    @PostMapping("/update/removal/delete")
    public SystemResult deleteById(Integer PipeId)throws  Exception{
        return PipeService.deleteById(PipeId);
    }
}
