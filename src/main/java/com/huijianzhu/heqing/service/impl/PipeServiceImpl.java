package com.huijianzhu.heqing.service.impl;

import cn.hutool.core.util.StrUtil;
import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.entity.HqPlotPipe;
import com.huijianzhu.heqing.entity.HqPropertyValueWithBLOBs;
import com.huijianzhu.heqing.enums.*;
import com.huijianzhu.heqing.lock.PipeLock;
import com.huijianzhu.heqing.mapper.extend.HqPlotPipeExtendMapper;
import com.huijianzhu.heqing.mapper.extend.HqPropertyValueExtendMapper;
import com.huijianzhu.heqing.pojo.PlotOrHouseOrPipeDesc;
import com.huijianzhu.heqing.pojo.PlotPipeDTO;
import com.huijianzhu.heqing.pojo.PropertyTree;
import com.huijianzhu.heqing.pojo.UserLoginContent;
import com.huijianzhu.heqing.service.PipeService;
import com.huijianzhu.heqing.service.PropertyService;
import com.huijianzhu.heqing.service.PropertyValueService;
import com.huijianzhu.heqing.utils.CookieUtils;
import com.huijianzhu.heqing.vo.SystemResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private HttpServletRequest request;

    @Autowired
    private LoginTokenCacheManager  loginTokenCacheManager;             //注入操作用户登录标识缓存管理

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
     * @param PipeName   关于管道的名称
     * @return
     */
    public SystemResult getPipeContentListByName(String PipeName){
        //判断是否有指定查询的内容没有默认是为null
        PipeName= StrUtil.hasBlank(PipeName)?null:PipeName;

        //创建一个map存储管道对应的管道搬迁信息
        HashMap<String,PlotPipeDTO> plotPipeMap=new HashMap<>();

        //获取将管道动迁信息集以管道的方式分组
        List<PlotPipeDTO> plotPipeByName = hqPlotPipeExtendMapper.getPlotPipeByName(PipeName, GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        plotPipeByName.forEach(
            e->{
                //获取当前对应的管道信息
                PlotPipeDTO plotPipeDTO = plotPipeMap.get(e.getPlotName());
                if(plotPipeDTO==null){
                    //由于没有管道信息所以创建一个新的管道信息
                    plotPipeDTO=new PlotPipeDTO();
                    plotPipeDTO.setPlotTd(e.getPlotTd());              //管道id
                    plotPipeDTO.setPlotName(e.getPlotName());          //管道名称
                    plotPipeDTO.setPlotCreateTime(e.getCreateTime());  //管道创建时间
                    plotPipeDTO.setPipeList(new ArrayList<>());        //管道搬迁信息集
                }
                //将动迁管道搬迁信息存储到每个地块对应的信息集中
                plotPipeDTO.getPipeList().add(e);
            }
        );
        //对plotPipeMap进行排序操作最新的管道信息最前面
        List<PlotPipeDTO> plotPipeList = plotPipeMap.entrySet().stream().map(e -> e.getValue()).sorted(
            (e1, e2) -> {
                return (int) ((e2.getCreateTime().getTime() / 1000) - (e1.getCreateTime().getTime() / 1000));
            }
        ).collect(Collectors.toList());
        //获取对应的PipeName所有管道搬迁信息
        return SystemResult.ok(plotPipeList);
    }

    /**
     * 添加管道搬迁信息
     * @param definition 封装了管道搬迁信息的实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws  Exception{
        //开启原子锁操作
        PipeLock.PIPE_UPDATE_LOCK.writeLock().lock();
        try{
            //判断当前新添加到名称是否在数据库中存在
            HqPlotPipe plot = hqPlotPipeExtendMapper.getPipeByName(definition.getContentName(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY,null);
            if(plot!=null){
                return SystemResult.build(SYSTEM_RESULT_STATE.UPDATE_FAILURE.KEY,"当前管道名称已经存在,请添加其它管道名称");
            }
            //获取当前登录用户信息
            //String cookieValue = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            //UserLoginContent loginUserContent = loginTokenCacheManager.getCacheUserByLoginToken(cookieValue);

            //创建一个管道信息
            HqPlotPipe newPipe=new HqPlotPipe();
            newPipe.setPipeName(definition.getContentName());               //管道名称
            newPipe.setPipePlotMark(definition.getPlotMark());              //地标信息
            newPipe.setCreateTime(new Date());                              //创建时间
            newPipe.setUpdateTime(new Date());                              //修改时间
            //newPlot.setUpdateUserName(loginUserContent.getUserName());    //最近一次谁操作了该记录
            newPipe.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);   //默认是有效信息

            //持久化到数据库中
            hqPlotPipeExtendMapper.insertSelective(newPipe);
            return  SystemResult.ok("管道信息添加成功");
        }catch(Exception e) {
            throw e;
        }finally {
            //解锁操作
            PipeLock.PIPE_UPDATE_LOCK.writeLock().unlock();
        }
    }

    /**
     * 获取指定id对应的管道信息
     * @param pipeId  某一个管道信息id
     * @return
     */
    public SystemResult getPipeDescById(String pipeId){

        //创建一个封装本次管道信息及对应的管道属性信息
        PlotOrHouseOrPipeDesc pipeDesc=new PlotOrHouseOrPipeDesc();

        //默认获取所有管道相关的属性信息(树结构)
        SystemResult propertiesByName = propertyService.getPropertiesByName(null, PLOT_HOUSE_PIPE_TYPE.PIPE_TYPE.KEY);
        List<PropertyTree>  treeList=(List)propertiesByName.getResult();
        pipeDesc.setPropertyTrees(treeList); //封装属性信息

        //获取当前指定管道对应的属性值信息
        List<HqPropertyValueWithBLOBs> propertyValues = hqPropertyValueExtendMapper.getPropertyValues(PLOT_HOUSE_PIPE_TYPE.PIPE_TYPE.KEY, pipeId, GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        pipeDesc.setPropertyValues(propertyValues); //封装本次管道属性值信息
        return SystemResult.ok(pipeDesc);
    }


    /**
     * 修改管道搬迁,管道搬迁属性信息
     * @param definition 封装了管道搬迁修改信息及对应的,管道搬迁属性信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws  Exception{
        //开启修改原子锁.防止修改同管道名
        PipeLock.PIPE_UPDATE_LOCK.writeLock().lock();
        try{
            //判断当前新修改的管道名称,是否已经存在
            HqPlotPipe plot = hqPlotPipeExtendMapper.getPipeByName(definition.getContentName(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY,definition.getContentId());
            if(plot!=null){
                return SystemResult.build(SYSTEM_RESULT_STATE.UPDATE_FAILURE.KEY,"本次修改失败,当前的管道名有相同");
            }

            //获取当前用户信息
            //String loginToken = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            //UserLoginContent userContent = loginTokenCacheManager.getCacheUserByLoginToken(loginToken);

            //修改管道信息
            HqPlotPipe updateHqplotPipe=new HqPlotPipe();
            updateHqplotPipe.setUpdateTime(new Date());                            //最近的一次修改时间
            //updateHqplot.setUpdateUserName(userContent.getUserName());  //记录谁操作了本次记录
            updateHqplotPipe.setPipeId(definition.getContentId());                //修改指定的管道id
            updateHqplotPipe.setPipeName(definition.getContentName());            //修改新的管道名称
            updateHqplotPipe.setPipePlotMark(definition.getPlotMark());           //修改新的地标信息

            //将管道信息持久化到数据库中
            hqPlotPipeExtendMapper.updateByPrimaryKeySelective(updateHqplotPipe);

            //更新管道对应的属性值信息
            propertyValueService.updatePropertyValue(definition.getPropertyValueList());
            return SystemResult.ok("管道信息修改成功");
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
           //关闭原子锁
            PipeLock.PIPE_UPDATE_LOCK.writeLock().lock();
        }
    }



    /**
     * 删除管道搬迁信息
     * @param pipeId  管道搬迁对应的id
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult deleteById(Integer pipeId)throws  Exception{

        //开启原子锁操作,防止修改管道信息时有可能是已经被删除的管道信息
        PipeLock.PIPE_UPDATE_LOCK.writeLock().lock();
        try{
            //获取当前登录用户信息
            String cookieValue = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent loginUserContent = loginTokenCacheManager.getCacheUserByLoginToken(cookieValue);

            HqPlotPipe deletePipe=new HqPlotPipe();
            deletePipe.setPlotId(pipeId);
            deletePipe.setUpdateUserName(loginUserContent.getUserName());   //记录谁操作了本次记录信息
            deletePipe.setUpdateTime(new Date());                           //记录最近的一次修改时间

            //持久化到数据库中
            hqPlotPipeExtendMapper.updateByPrimaryKeySelective(deletePipe);

            return  SystemResult.ok("管道信息删除成功");
        }catch(Exception e) {
            throw e;
        }finally {
            //解锁操作
            PipeLock.PIPE_UPDATE_LOCK.writeLock().unlock();
        }
    }
}
