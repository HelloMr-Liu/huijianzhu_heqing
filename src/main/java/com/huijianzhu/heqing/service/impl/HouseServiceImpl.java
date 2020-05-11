package com.huijianzhu.heqing.service.impl;

import cn.hutool.core.util.StrUtil;
import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.entity.HqPlotHouse;
import com.huijianzhu.heqing.entity.HqPropertyValueWithBLOBs;
import com.huijianzhu.heqing.enums.*;
import com.huijianzhu.heqing.lock.HouseLock;
import com.huijianzhu.heqing.mapper.extend.HqPlotHouseExtendMapper;
import com.huijianzhu.heqing.mapper.extend.HqPropertyValueExtendMapper;
import com.huijianzhu.heqing.pojo.*;
import com.huijianzhu.heqing.service.HouseService;
import com.huijianzhu.heqing.service.PropertyService;
import com.huijianzhu.heqing.service.PropertyValueService;
import com.huijianzhu.heqing.utils.CookieUtils;
import com.huijianzhu.heqing.vo.SystemResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ================================================================
 * 说明：操作房屋信息业务接口实现
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  15:41            创建
 * =================================================================
 **/
@Service
public class HouseServiceImpl implements HouseService {


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginTokenCacheManager  loginTokenCacheManager;             //注入操作用户登录标识缓存管理

    @Autowired
    private HqPlotHouseExtendMapper hqPlotHouseExtendMapper;            //操作房屋搬迁信息表mapper接口

    @Autowired
    private HqPropertyValueExtendMapper hqPropertyValueExtendMapper;    //操作属性值信息表mapper接口

    @Autowired
    private PropertyService propertyService;                            //注入属性业务接口

    @Autowired
    private PropertyValueService propertyValueService;                  //注入属性值业务接口

    /**
     * 获取所有与房屋搬迁名称相关的房屋信息默认是查询出所有
     * @param houseName   关于房屋的名称
     * @return
     */
    public SystemResult getHosueContentListByName(String houseName){
        //判断是否有指定查询的内容没有默认是为null
        houseName= StrUtil.hasBlank(houseName)?null:houseName;

        //创建一个map存储房屋对应的房屋搬迁信息
        TreeMap<String,PlotHouseDTO> plotHouseMap=new TreeMap<>();

        //获取将房屋动迁信息集以房屋的方式分组
        List<PlotHouseDTO> plotHouseByName = hqPlotHouseExtendMapper.getPlotHouseByName(houseName, GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        plotHouseByName.forEach(
            e->{
                //获取当前对应的房屋信息
                PlotHouseDTO plotHouseDTO = plotHouseMap.get(e.getPlotName());
                if(plotHouseDTO==null){
                    //由于没有房屋信息所以创建一个新的地块房屋信息
                    plotHouseDTO=new PlotHouseDTO();
                    plotHouseDTO.setPlotId(e.getPlotId());                  //房屋id
                    plotHouseDTO.setPlotName(e.getPlotName());              //房屋名称
                    plotHouseDTO.setPlotCreateTime(e.getPlotCreateTime());  //地块创建时间
                    plotHouseDTO.setLiveList(new ArrayList<>());            //居住
                    plotHouseDTO.setNotLiveList(new ArrayList<>());         //非居住

                    plotHouseMap.put(e.getPlotName(),plotHouseDTO);     //将当前地块房屋搬迁信息存储到plotHouseMap中
                }
                //判断当前房屋时什么类型的
                if(e.getHouseType().equals(HOUSE_TABLE_FILED_STATE.STATE.LIVE)){
                    //将当前房屋动迁存储到居住集中
                    plotHouseDTO.getLiveList().add(e);
                }else{
                    //将当前房屋动迁存储到非居住集中
                    plotHouseDTO.getNotLiveList().add(e);
                }
            }
        );


        //对plotHouseMap进行排序操作最新的房屋信息最前面
        List<PlotHouseDTO> plotHouseList = plotHouseMap.entrySet().stream().map(e -> e.getValue()).sorted(
            (e1, e2) -> {
                return (int) ((e2.getPlotCreateTime().getTime() / 1000) - (e1.getPlotCreateTime().getTime() / 1000));
            }
        )
        .collect(Collectors.toList());
        //获取对应的houseName所有房屋搬迁信息
        return SystemResult.ok(plotHouseList);
    }

    /**
     * 添加房屋搬迁信息
     * @param definition 封装了房屋搬迁信息的实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws  Exception{
        //开启原子锁操作
        HouseLock.HOUSE_UPDATE_LOCK.writeLock().lock();
        try{
            //判断当前新添加到名称是否在数据库中存在
            HqPlotHouse plot = hqPlotHouseExtendMapper.getHouseByName(definition.getContentName(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY,null);
            if(plot!=null){
                return SystemResult.build(SYSTEM_RESULT_STATE.UPDATE_FAILURE.KEY,"当前房屋名称已经存在,请添加其它房屋名称");
            }
            //获取当前登录用户信息
            String cookieValue = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent loginUserContent = loginTokenCacheManager.getCacheUserByLoginToken(cookieValue);

            //创建一个房屋信息
            HqPlotHouse newHosue=new HqPlotHouse();
            newHosue.setHouseName(definition.getContentName());             //房屋名称
            newHosue.setHousePlotMark(definition.getPlotMark());            //地标信息
            newHosue.setPlotId(definition.getPlotId());                     //指定某一个地块
            newHosue.setHouseType(definition.getHouseType());               //指定房屋类型 LIVE(居住) NOLIVE(非居住)
            newHosue.setCreateTime(new Date());                             //创建时间
            newHosue.setUpdateTime(new Date());                             //修改时间
            newHosue.setUpdateUserName(loginUserContent.getUserName());      //最近一次谁操作了该记录
            newHosue.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);  //默认是有效信息

            //持久化到数据库中
            hqPlotHouseExtendMapper.insertSelective(newHosue);
            return  SystemResult.ok("房屋信息添加成功");
        }catch(Exception e) {
            throw e;
        }finally {
            //解锁操作
            HouseLock.HOUSE_UPDATE_LOCK.writeLock().unlock();
        }
    }

    /**
     * 获取指定id对应的房屋信息
     * @param houseId  某一个房屋信息id
     * @return
     */
    public SystemResult getHouseDescById(String houseId){

        //创建一个封装本次房屋信息及对应的房屋属性信息
        PlotOrHouseOrPipeDesc  pipeDesc=new PlotOrHouseOrPipeDesc();

        //默认获取所有房屋相关的属性信息(树结构)
        SystemResult propertiesByName = propertyService.getPropertiesByName(null, PLOT_HOUSE_PIPE_TYPE.HOUSE_TYPE.KEY);
        List<PropertyTree>  treeList=(List)propertiesByName.getResult();
        pipeDesc.setPropertyTrees(treeList); //封装属性信息

        //获取当前指定房屋对应的属性值信息
        List<HqPropertyValueWithBLOBs> propertyValues = hqPropertyValueExtendMapper.getPropertyValues(PLOT_HOUSE_PIPE_TYPE.HOUSE_TYPE.KEY, houseId, GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        pipeDesc.setPropertyValues(propertyValues); //封装本次房屋属性值信息
        return SystemResult.ok(pipeDesc);
    }


    /**
     * 修改房屋搬迁,房屋搬迁属性信息
     * @param definition 封装了房屋搬迁修改信息及对应的,房屋搬迁属性信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws  Exception{
        //开启修改原子锁.防止修改同房屋名
        HouseLock.HOUSE_UPDATE_LOCK.writeLock().lock();
        try{
            //判断当前新修改的房屋名称,是否已经存在
            HqPlotHouse plot = hqPlotHouseExtendMapper.getHouseByName(definition.getContentName(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY,definition.getContentId());
            if(plot!=null){
                return SystemResult.build(SYSTEM_RESULT_STATE.UPDATE_FAILURE.KEY,"本次修改失败,当前的房屋名有相同");
            }

            //获取当前用户信息
            String loginToken = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent userContent = loginTokenCacheManager.getCacheUserByLoginToken(loginToken);

            //修改房屋信息
            HqPlotHouse updateHqplotHouse=new HqPlotHouse();
            updateHqplotHouse.setUpdateTime(new Date());                            //最近的一次修改时间
            updateHqplotHouse.setUpdateUserName(userContent.getUserName());         //记录谁操作了本次记录
            updateHqplotHouse.setHouseId(definition.getContentId());                //修改指定的房屋id
            updateHqplotHouse.setHouseName(definition.getContentName());            //修改新的房屋名称
            updateHqplotHouse.setHousePlotMark(definition.getPlotMark());           //修改新的地标信息

            //将房屋信息持久化到数据库中
            hqPlotHouseExtendMapper.updateByPrimaryKeySelective(updateHqplotHouse);

            if(definition.getPropertyValueList()!=null) {
                //更新房屋对应的属性值信息
                propertyValueService.updatePropertyValue(definition.getPropertyValueList());
            }

            return SystemResult.ok("房屋信息修改成功");
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
           //关闭原子锁
            HouseLock.HOUSE_UPDATE_LOCK.writeLock().unlock();
        }
    }



    /**
     * 删除房屋搬迁信息
     * @param houseId  房屋搬迁对应的id
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult deleteById(Integer houseId)throws  Exception{

        //开启原子锁操作,防止修改房屋信息时有可能是已经被删除的房屋信息
        HouseLock.HOUSE_UPDATE_LOCK.writeLock().lock();
        try{
            //获取当前登录用户信息
            String cookieValue = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent loginUserContent = loginTokenCacheManager.getCacheUserByLoginToken(cookieValue);

            HqPlotHouse deleteHosue=new HqPlotHouse();
            deleteHosue.setHouseId(houseId);
            deleteHosue.setUpdateUserName(loginUserContent.getUserName());   //记录谁操作了本次记录信息
            deleteHosue.setUpdateTime(new Date());                           //记录最近的一次修改时间
            deleteHosue.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_YES.KEY);

            //持久化到数据库中
            hqPlotHouseExtendMapper.updateByPrimaryKeySelective(deleteHosue);

            return  SystemResult.ok("房屋信息删除成功");
        }catch(Exception e) {
            throw e;
        }finally {
            //解锁操作
            HouseLock.HOUSE_UPDATE_LOCK.writeLock().unlock();
        }
    }
}
