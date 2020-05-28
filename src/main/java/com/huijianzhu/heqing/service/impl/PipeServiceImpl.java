package com.huijianzhu.heqing.service.impl;

import cn.hutool.core.util.StrUtil;
import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.definition.HqPlotHouseDefinition;
import com.huijianzhu.heqing.definition.HqPlotPipeDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.entity.HqPlotHouse;
import com.huijianzhu.heqing.entity.HqPlotPipe;
import com.huijianzhu.heqing.entity.HqPropertyValueWithBLOBs;
import com.huijianzhu.heqing.enums.*;
import com.huijianzhu.heqing.lock.PipeLock;
import com.huijianzhu.heqing.mapper.extend.HqPlotPipeExtendMapper;
import com.huijianzhu.heqing.mapper.extend.HqPropertyValueExtendMapper;
import com.huijianzhu.heqing.pojo.*;
import com.huijianzhu.heqing.service.PipeService;
import com.huijianzhu.heqing.service.PlotService;
import com.huijianzhu.heqing.service.PropertyService;
import com.huijianzhu.heqing.service.PropertyValueService;
import com.huijianzhu.heqing.utils.CookieUtils;
import com.huijianzhu.heqing.vo.SystemResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ================================================================
 * 说明：操作管道信息业务接口实现
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  15:41            创建
 * =================================================================
 **/
@Service
public class PipeServiceImpl implements PipeService {


    @Autowired
    private PlotService plotService;                                    //注入地块信息业务接口


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginTokenCacheManager loginTokenCacheManager;             //注入操作用户登录标识缓存管理

    @Autowired
    private HqPlotPipeExtendMapper hqPlotPipeExtendMapper;              //操作管道搬迁信息表mapper接口

    @Autowired
    private HqPropertyValueExtendMapper hqPropertyValueExtendMapper;    //操作属性值信息表mapper接口

    @Autowired
    private PropertyService propertyService;                            //注入属性业务接口

    @Autowired
    private PropertyValueService propertyValueService;                  //注入属性值业务接口

    /**
     * 获取所有与管道搬迁名称相关的管道信息默认是查询出所有
     *
     * @param PipeName 关于管道的名称
     * @return
     */
    public SystemResult getPipeContentListByName(String PipeName) {
        //判断是否有指定查询的内容没有默认是为null
        PipeName = StrUtil.hasBlank(PipeName) ? null : PipeName;

        //创建一个map存储管道对应的管道搬迁信息
        HashMap<String, PlotPipeDTO> plotPipeMap = new HashMap<>();

        //获取将管道动迁信息集以管道的方式分组
        List<PlotPipeDTO> plotPipeByName = hqPlotPipeExtendMapper.getPlotPipeByName(PipeName, GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        plotPipeByName.forEach(
                e -> {
                    //获取当前对应的管道信息
                    PlotPipeDTO plotPipeDTO = plotPipeMap.get(e.getPlotName());
                    if (plotPipeDTO == null) {
                        //由于没有管道信息所以创建一个新的管道信息
                        plotPipeDTO = new PlotPipeDTO();
                        plotPipeDTO.setPlotId(e.getPlotId());                   //管道id
                        plotPipeDTO.setPlotName(e.getPlotName());               //管道名称
                        plotPipeDTO.setPlotCreateTime(e.getPlotCreateTime());   //地块创建时间
                        plotPipeDTO.setPipeList(new ArrayList<>());             //管道搬迁信息集


                        plotPipeMap.put(e.getPlotName(), plotPipeDTO);
                    }
                    //将动迁管道搬迁信息存储到每个地块对应的信息集中
                    plotPipeDTO.getPipeList().add(e);
                }
        );
        //对plotPipeMap进行排序操作最新的管道信息最前面
        List<PlotPipeDTO> plotPipeList = plotPipeMap.entrySet().stream().map(e -> e.getValue()).sorted(
                (e1, e2) -> {
                    return (int) ((e2.getPlotCreateTime().getTime() / 1000) - (e1.getPlotCreateTime().getTime() / 1000));
                }
        ).collect(Collectors.toList());
        //获取对应的PipeName所有管道搬迁信息
        return SystemResult.ok(plotPipeList);
    }

    /**
     * 添加管道搬迁信息
     *
     * @param definition 封装了管道搬迁信息的实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws Exception {
        //开启原子锁操作
        PipeLock.PIPE_UPDATE_LOCK.writeLock().lock();
        try {
            //判断当前新添加到名称是否在数据库中存在
            HqPlotPipe plot = hqPlotPipeExtendMapper.getPipeByName(definition.getContentName(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY, null);
            if (plot != null) {
                return SystemResult.build(SYSTEM_RESULT_STATE.UPDATE_FAILURE.KEY, "当前管道名称已经存在,请添加其它管道名称");
            }
            //获取当前客户端信息
            UserLoginContent loginUserContent = (UserLoginContent) request.getAttribute(LOGIN_STATE.USER_LOGIN_TOKEN.toString());

            //创建一个管道信息
            HqPlotPipe newPipe = new HqPlotPipe();
            newPipe.setPipeName(definition.getContentName());               //管道名称
            newPipe.setPipePlotMark(definition.getPlotMark());              //地标信息
            newPipe.setPlotId(definition.getPlotId());                     //指定某一个地块
            newPipe.setCreateTime(new Date());                              //创建时间
            newPipe.setUpdateTime(new Date());                              //修改时间
            newPipe.setUpdateUserName(loginUserContent.getUserName());      //最近一次谁操作了该记录
            newPipe.setExtend1(definition.getColor());                      //颜色信息
            newPipe.setExtend2(definition.getLucency());                    //透明度
            newPipe.setExtend3(definition.getEntityId());                   //实体id
            newPipe.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);   //默认是有效信息

            //持久化到数据库中
            hqPlotPipeExtendMapper.insertSelective(newPipe);

            //默认获取所有地块相关的属性信息(树结构)
            SystemResult propertiesByName = propertyService.getPropertiesByName(null, PLOT_HOUSE_PIPE_TYPE.PIPE_TYPE.KEY);
            List<PropertyTree> treeList = (List) propertiesByName.getResult();
            //用于默认创建一个子属性信息
            boolean flag = false;
            if (treeList != null && treeList.size() > 0) {
                //遍历查询出对应的属性中是否有对应的名称细腻些
                for (PropertyTree pro : treeList) {
                    //遍历子的属性

                    List<PropertyTree> childrens = pro.getChildren();
                    if (childrens != null && childrens.size() > 0) {
                        for (PropertyTree child : childrens) {
                            if (child.getPropertyName().indexOf("名称") > -1) {
                                //创建本次本次属性id信息
                                HqPropertyValueWithBLOBs value = new HqPropertyValueWithBLOBs();
                                value.setPlotType(PLOT_HOUSE_PIPE_TYPE.PIPE_TYPE.KEY);
                                value.setPlotTypeId(newPipe.getPipeId());
                                value.setPropertyId(child.getPropertyId());
                                value.setPropertyValue(definition.getContentName());
                                value.setPropertyValueDesc("");
                                value.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
                                value.setCreateTime(new Date());
                                value.setUpdateTime(new Date());
                                value.setUpdateUserName(loginUserContent.getUserName());

                                //持久化到数据库中
                                hqPropertyValueExtendMapper.insertSelective(value);
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            break;
                        }
                    }
                }
            }


            return SystemResult.ok("管道信息添加成功");
        } catch (Exception e) {
            throw e;
        } finally {
            //解锁操作
            PipeLock.PIPE_UPDATE_LOCK.writeLock().unlock();
        }
    }

    /**
     * 获取指定id对应的管道信息
     *
     * @param pipeId 某一个管道信息id
     * @return
     */
    public SystemResult getPipeDescById(String pipeId) {

        //创建一个封装本次管道信息及对应的管道属性信息
        PlotOrHouseOrPipeDesc pipeDesc = new PlotOrHouseOrPipeDesc();

        //默认获取所有管道相关的属性信息(树结构)
        SystemResult propertiesByName = propertyService.getPropertiesByName(null, PLOT_HOUSE_PIPE_TYPE.PIPE_TYPE.KEY);
        List<PropertyTree> treeList = (List) propertiesByName.getResult();
        pipeDesc.setPropertyTrees(treeList); //封装属性信息

        //获取当前指定管道对应的属性值信息
        List<HqPropertyValueWithBLOBs> propertyValues = hqPropertyValueExtendMapper.getPropertyValues(PLOT_HOUSE_PIPE_TYPE.PIPE_TYPE.KEY, Integer.parseInt(pipeId), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        pipeDesc.setPropertyValues(propertyValues); //封装本次管道属性值信息
        return SystemResult.ok(pipeDesc);
    }


    /**
     * 修改管道搬迁,管道搬迁属性信息
     *
     * @param definition 封装了管道搬迁修改信息及对应的,管道搬迁属性信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws Exception {
        //开启修改原子锁.防止修改同管道名
        PipeLock.PIPE_UPDATE_LOCK.writeLock().lock();
        try {
            //判断当前新修改的管道名称,是否已经存在
            HqPlotPipe plot = hqPlotPipeExtendMapper.getPipeByName(definition.getContentName(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY, definition.getContentId());
            if (plot != null) {
                return SystemResult.build(SYSTEM_RESULT_STATE.UPDATE_FAILURE.KEY, "本次修改失败,当前的管道名有相同");
            }

            if (definition.getPropertyValueList() == null) {
                //获取当前客户端信息
                UserLoginContent userContent = (UserLoginContent) request.getAttribute(LOGIN_STATE.USER_LOGIN_TOKEN.toString());


                //获取原管道搬迁信息
                HqPlotPipe hqPlotPipe = hqPlotPipeExtendMapper.selectByPrimaryKey(definition.getContentId());

                //修改管道信息
                HqPlotPipe updateHqplotPipe = new HqPlotPipe();
                updateHqplotPipe.setUpdateTime(new Date());                            //最近的一次修改时间
                updateHqplotPipe.setUpdateUserName(userContent.getUserName());         //记录谁操作了本次记录
                updateHqplotPipe.setPipeId(definition.getContentId());                 //修改指定的管道id
                updateHqplotPipe.setPipeName(definition.getContentName());             //修改新的管道名称
                updateHqplotPipe.setPipePlotMark(definition.getPlotMark());            //修改新的地标信息
                updateHqplotPipe.setExtend1(definition.getColor());                      //颜色信息
                updateHqplotPipe.setExtend2(definition.getLucency());                    //透明度
                updateHqplotPipe.setExtend3(definition.getEntityId());                  //实体id
                //将管道信息持久化到数据库中
                hqPlotPipeExtendMapper.updateByPrimaryKeySelective(updateHqplotPipe);


                //当前地块对应的子属性
                List<HqPropertyValueWithBLOBs> propertyValues = hqPropertyValueExtendMapper.getPropertyValues(PLOT_HOUSE_PIPE_TYPE.PIPE_TYPE.KEY, definition.getContentId(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
                //遍历子属性值信息获取对应的地块管道名称
                for (HqPropertyValueWithBLOBs glb : propertyValues) {
                    if (glb.getPropertyValue().equals(hqPlotPipe.getPipeName())) {
                        //判断名称是否和原来一样
                        if (!glb.getPropertyValue().equals(definition.getContentName())) {
                            //名称不一样修改对应的属性值信息
                            glb.setPropertyValue(definition.getContentName());
                            hqPropertyValueExtendMapper.updateByPrimaryKeyWithBLOBs(glb);
                            break;
                        }
                    }
                }


            } else {
                List<AccpetPlotTypePropertyValue> propertyValueList = definition.getPropertyValueList();
                propertyValueList.forEach(
                        e -> {
                            e.setPlotType(PLOT_HOUSE_PIPE_TYPE.PIPE_TYPE.KEY);
                        }
                );
                //更新管道对应的属性值信息
                propertyValueService.updatePropertyValue(definition.getPropertyValueList());
            }
            return SystemResult.ok("管道信息修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            //关闭原子锁
            PipeLock.PIPE_UPDATE_LOCK.writeLock().unlock();
        }
    }


    /**
     * 删除管道搬迁信息
     *
     * @param pipeId 管道搬迁对应的id
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult deleteById(Integer pipeId) throws Exception {

        //开启原子锁操作,防止修改管道信息时有可能是已经被删除的管道信息
        PipeLock.PIPE_UPDATE_LOCK.writeLock().lock();
        try {
            //获取当前客户端信息
            UserLoginContent loginUserContent = (UserLoginContent) request.getAttribute(LOGIN_STATE.USER_LOGIN_TOKEN.toString());

            HqPlotPipe deletePipe = new HqPlotPipe();
            deletePipe.setPipeId(pipeId);
            deletePipe.setUpdateUserName(loginUserContent.getUserName());   //记录谁操作了本次记录信息
            deletePipe.setUpdateTime(new Date());                           //记录最近的一次修改时间
            deletePipe.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_YES.KEY);

            //持久化到数据库中
            hqPlotPipeExtendMapper.updateByPrimaryKeySelective(deletePipe);

            return SystemResult.ok("管道信息删除成功");
        } catch (Exception e) {
            throw e;
        } finally {
            //解锁操作
            PipeLock.PIPE_UPDATE_LOCK.writeLock().unlock();
        }
    }


    /**
     * 批量插入管道搬迁信息
     *
     * @param pipes 管道搬迁信息集
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult batchInsertPipes(List<HqPlotPipeDefinition> pipes) {

        //获取最新的地块信息集
        SystemResult plotContentListByName = plotService.getPlotContentListByName(null);
        List<HqPlot> plots = (List<HqPlot>) plotContentListByName.getResult();
        //转换成map
        Map<String, Integer> collect = plots.stream().collect(Collectors.toMap(HqPlot::getPlotName, HqPlot::getPlotId, (test1, test2) -> test1));
        if (collect == null || collect.size() < 1) {
            return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY, "当前对应的地块信息没有,所有仔细查看你选择的地块信息");
        }

        //创建一个map用于判断本次存储管道名称是否有相同的
        HashMap<String, String> pipeNameMap = new HashMap<>();


        //获取当前客户端信息
        UserLoginContent user = (UserLoginContent) request.getAttribute(LOGIN_STATE.USER_LOGIN_TOKEN.toString());


        //对pipes进行属性内容扩展添加
        for (int index = 0; index < pipes.size(); index++) {
            HqPlotPipeDefinition pipe = pipes.get(index);

            //判断当前的房屋动迁对有的地块信息是否存在
            if (!collect.containsKey(pipe.getPlotName())) {
                return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY, "在第" + (index + 3) + "行当前对应的地块信息:" + pipe.getPlotName() + "不存在");
            }

            HqPlotPipe currentPipe = hqPlotPipeExtendMapper.getPipeByName(pipe.getPipeName(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY, null);
            //判断动迁名称是否在pipeNameMap中存在
            if (pipeNameMap.containsKey(pipe.getPipeName()) || currentPipe != null || StrUtil.hasBlank(pipe.getPipeName())) {
                return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY, "在第" + (index + 3) + "行当前对应的管道搬迁名称:" + pipe.getPipeName() + "重复/异常");
            }

            pipeNameMap.put(pipe.getPipeName(), pipe.getPipeName());

            pipe.setPlotId(collect.get(pipe.getPlotName()));
            pipe.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
            pipe.setCreateTime(new Date());
            pipe.setUpdateTime(new Date());
            pipe.setUpdateUserName(user.getUserName());
        }

        //判断当前是否有新的信息存在
        if (pipes != null && plots.size() > 0) {
            //并批量插入持久化到数据库中
            hqPlotPipeExtendMapper.batchInsertPipes(pipes);
        }

        return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY, "管道信息导入成功");
    }
}
