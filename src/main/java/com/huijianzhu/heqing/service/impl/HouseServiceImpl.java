package com.huijianzhu.heqing.service.impl;

import cn.hutool.core.util.StrUtil;
import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.definition.HqPlotHouseDefinition;
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
import com.huijianzhu.heqing.service.PlotService;
import com.huijianzhu.heqing.service.PropertyService;
import com.huijianzhu.heqing.service.PropertyValueService;
import com.huijianzhu.heqing.utils.CookieUtils;
import com.huijianzhu.heqing.vo.SystemResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.ls.LSOutput;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
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
    private LoginTokenCacheManager loginTokenCacheManager;             //注入操作用户登录标识缓存管理

    @Autowired
    private HqPlotHouseExtendMapper hqPlotHouseExtendMapper;            //操作房屋搬迁信息表mapper接口

    @Autowired
    private HqPropertyValueExtendMapper hqPropertyValueExtendMapper;    //操作属性值信息表mapper接口

    @Autowired
    private PlotService plotService;                                    //注入房屋信息业务接口

    @Autowired
    private PropertyService propertyService;                            //注入属性业务接口

    @Autowired
    private PropertyValueService propertyValueService;                  //注入属性值业务接口

    /**
     * 获取所有与房屋搬迁名称相关的房屋信息默认是查询出所有
     *
     * @param houseName 关于房屋的名称
     * @return
     */
    public SystemResult getHosueContentListByName(String houseName) {
        //判断是否有指定查询的内容没有默认是为null
        houseName = StrUtil.hasBlank(houseName) ? null : houseName;

        //创建一个map存储房屋对应的房屋搬迁信息
        TreeMap<String, PlotHouseDTO> plotHouseMap = new TreeMap<>();

        //获取将房屋动迁信息集以房屋的方式分组
        List<PlotHouseDTO> plotHouseByName = hqPlotHouseExtendMapper.getPlotHouseByName(houseName, GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        plotHouseByName.forEach(
                e -> {
                    //获取当前对应的房屋信息
                    PlotHouseDTO plotHouseDTO = plotHouseMap.get(e.getPlotName());
                    if (plotHouseDTO == null) {
                        //由于没有房屋信息所以创建一个新的房屋房屋信息
                        plotHouseDTO = new PlotHouseDTO();
                        plotHouseDTO.setPlotId(e.getPlotId());                  //房屋id
                        plotHouseDTO.setPlotName(e.getPlotName());              //房屋名称
                        plotHouseDTO.setPlotCreateTime(e.getPlotCreateTime());  //房屋创建时间
                        plotHouseDTO.setLiveList(new ArrayList<>());            //居住
                        plotHouseDTO.setNotLiveList(new ArrayList<>());         //非居住

                        plotHouseMap.put(e.getPlotName(), plotHouseDTO);     //将当前房屋房屋搬迁信息存储到plotHouseMap中
                    }
                    //判断当前房屋时什么类型的
                    if (HOUSE_TABLE_FILED_STATE.STATE.LIVE.equals(e.getHouseType())) {
                        //将当前房屋动迁存储到居住集中
                        plotHouseDTO.getLiveList().add(e);
                    } else if (HOUSE_TABLE_FILED_STATE.STATE.NOLIVE.equals(e.getHouseType())) {
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
     *
     * @param definition 封装了房屋搬迁信息的实体对象
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws Exception {
        //开启原子锁操作
        HouseLock.HOUSE_UPDATE_LOCK.writeLock().lock();
        try {
            //判断当前新添加到名称是否在数据库中存在
            HqPlotHouse plot = hqPlotHouseExtendMapper.getHouseByName(definition.getContentName(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY, null);
            if (plot != null) {
                return SystemResult.build(SYSTEM_RESULT_STATE.UPDATE_FAILURE.KEY, "当前房屋名称已经存在,请添加其它房屋名称");
            }
            //获取用户本地登录标识信息
            UserLoginContent loginUserContent = (UserLoginContent) request.getAttribute(LOGIN_STATE.USER_LOGIN_TOKEN.toString());

            //创建一个房屋信息
            HqPlotHouse newHosue = new HqPlotHouse();
            newHosue.setHouseName(definition.getContentName());             //房屋名称
            newHosue.setHousePlotMark(definition.getPlotMark());            //地标信息
            newHosue.setPlotId(definition.getPlotId());                     //指定某一个房屋
            newHosue.setHouseType(definition.getHouseType());               //指定房屋类型 LIVE(居住) NOLIVE(非居住)
            newHosue.setCreateTime(new Date());                             //创建时间
            newHosue.setUpdateTime(new Date());                             //修改时间
            newHosue.setExtend1(definition.getColor());                      //颜色信息
            newHosue.setExtend2(definition.getLucency());                    //透明度
            newHosue.setExtend3(definition.getEntityId());                  //实体id
            newHosue.setUpdateUserName(loginUserContent.getUserName());      //最近一次谁操作了该记录
            newHosue.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);  //默认是有效信息

            //持久化到数据库中
            hqPlotHouseExtendMapper.insertSelective(newHosue);


            //默认获取所有房屋相关的属性信息(树结构)
            SystemResult propertiesByName = propertyService.getPropertiesByName(null, PLOT_HOUSE_PIPE_TYPE.HOUSE_TYPE.KEY);
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
                                value.setPlotType(PLOT_HOUSE_PIPE_TYPE.HOUSE_TYPE.KEY);
                                value.setPlotTypeId(newHosue.getHouseId());
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


            return SystemResult.ok("房屋信息添加成功");
        } catch (Exception e) {
            throw e;
        } finally {
            //解锁操作
            HouseLock.HOUSE_UPDATE_LOCK.writeLock().unlock();
        }
    }

    /**
     * 获取指定id对应的房屋信息
     *
     * @param houseId 某一个房屋信息id
     * @return
     */
    public SystemResult getHouseDescById(String houseId) {

        //创建一个封装本次房屋信息及对应的房屋属性信息
        PlotOrHouseOrPipeDesc pipeDesc = new PlotOrHouseOrPipeDesc();

        //默认获取所有房屋相关的属性信息(树结构)
        SystemResult propertiesByName = propertyService.getPropertiesByName(null, PLOT_HOUSE_PIPE_TYPE.HOUSE_TYPE.KEY);
        List<PropertyTree> treeList = (List) propertiesByName.getResult();
        pipeDesc.setPropertyTrees(treeList); //封装属性信息

        //获取当前指定房屋对应的属性值信息
        List<HqPropertyValueWithBLOBs> propertyValues = hqPropertyValueExtendMapper.getPropertyValues(PLOT_HOUSE_PIPE_TYPE.HOUSE_TYPE.KEY, Integer.parseInt(houseId), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        pipeDesc.setPropertyValues(propertyValues); //封装本次房屋属性值信息
        return SystemResult.ok(pipeDesc);
    }


    /**
     * 修改房屋搬迁,房屋搬迁属性信息
     *
     * @param definition 封装了房屋搬迁修改信息及对应的,房屋搬迁属性信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws Exception {
        //开启修改原子锁.防止修改同房屋名
        HouseLock.HOUSE_UPDATE_LOCK.writeLock().lock();
        try {
            //判断当前新修改的房屋名称,是否已经存在
            HqPlotHouse plot = hqPlotHouseExtendMapper.getHouseByName(definition.getContentName(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY, definition.getContentId());
            if (plot != null) {
                return SystemResult.build(SYSTEM_RESULT_STATE.UPDATE_FAILURE.KEY, "本次修改失败,当前的房屋名有相同");
            }

            if (definition.getPropertyValueList() == null) {

                //获取当前本地用户信息
                UserLoginContent userContent = (UserLoginContent) request.getAttribute(LOGIN_STATE.USER_LOGIN_TOKEN.toString());


                //获取当前源房屋名称
                HqPlotHouse hqPlotHouse = hqPlotHouseExtendMapper.selectByPrimaryKey(definition.getContentId());


                //修改房屋信息
                HqPlotHouse updateHqplotHouse = new HqPlotHouse();
                updateHqplotHouse.setUpdateTime(new Date());                            //最近的一次修改时间
                updateHqplotHouse.setUpdateUserName(userContent.getUserName());         //记录谁操作了本次记录
                updateHqplotHouse.setHouseId(definition.getContentId());                //修改指定的房屋id
                updateHqplotHouse.setHouseType(definition.getHouseType());              //房屋类型
                updateHqplotHouse.setHouseName(definition.getContentName());            //修改新的房屋名称
                updateHqplotHouse.setHousePlotMark(definition.getPlotMark());           //修改新的地标信息
                updateHqplotHouse.setExtend1(definition.getColor());                      //颜色信息
                updateHqplotHouse.setExtend2(definition.getLucency());                    //透明度
                updateHqplotHouse.setExtend3(definition.getEntityId());                  //实体id

                //将房屋信息持久化到数据库中
                hqPlotHouseExtendMapper.updateByPrimaryKeySelective(updateHqplotHouse);


                //当前房屋对应的子属性
                List<HqPropertyValueWithBLOBs> propertyValues = hqPropertyValueExtendMapper.getPropertyValues(PLOT_HOUSE_PIPE_TYPE.HOUSE_TYPE.KEY, definition.getContentId(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
                //遍历子属性值信息获取对应的房屋房屋名称
                for (HqPropertyValueWithBLOBs glb : propertyValues) {
                    if (glb.getPropertyValue().equals(hqPlotHouse.getHouseName())) {
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

                //获取当前房屋信息
                HqPlotHouse hqPlotHouse = hqPlotHouseExtendMapper.selectByPrimaryKey(propertyValueList.get(0).getPlotTypeId());

                //当前房屋对应的子属性
                List<HqPropertyValueWithBLOBs> propertyValues = hqPropertyValueExtendMapper.getPropertyValues(PLOT_HOUSE_PIPE_TYPE.HOUSE_TYPE.KEY, hqPlotHouse.getHouseId(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
                //遍历子属性值信息获取对应的房屋名称
                for (HqPropertyValueWithBLOBs glb : propertyValues) {
                    if (glb.getPropertyValue().equals(hqPlotHouse.getHouseName())) {

                        //判断本次修改的属性值对应的房屋名称是否有改变
                        for (AccpetPlotTypePropertyValue value : propertyValueList) {
                            if (value.getPropertyValueId() != null && value.getPropertyValueId().toString().equals(glb.getPropertyValueId().toString())) {
                                if (!value.getPropertyValue().equals(glb.getPropertyValue())) {

                                    //房屋名称已经改变重新修改房屋名称
                                    hqPlotHouse.setHouseName(value.getPropertyValue());
                                    hqPlotHouseExtendMapper.updateByPrimaryKeySelective(hqPlotHouse);
                                }
                            }
                        }
                    }
                }


                propertyValueList.forEach(
                        e -> {
                            e.setPlotType(PLOT_HOUSE_PIPE_TYPE.HOUSE_TYPE.KEY);
                        }
                );
                //更新房屋对应的属性值信息
                propertyValueService.updatePropertyValue(definition.getPropertyValueList());
            }

            return SystemResult.ok("房屋信息修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            //关闭原子锁
            HouseLock.HOUSE_UPDATE_LOCK.writeLock().unlock();
        }
    }


    /**
     * 删除房屋搬迁信息
     *
     * @param houseId 房屋搬迁对应的id
     * @return
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult deleteById(Integer houseId) throws Exception {

        //开启原子锁操作,防止修改房屋信息时有可能是已经被删除的房屋信息
        HouseLock.HOUSE_UPDATE_LOCK.writeLock().lock();
        try {
            //获取当前本地用户信息
            UserLoginContent loginUserContent = (UserLoginContent) request.getAttribute(LOGIN_STATE.USER_LOGIN_TOKEN.toString());

            HqPlotHouse deleteHosue = new HqPlotHouse();
            deleteHosue.setHouseId(houseId);
            deleteHosue.setUpdateUserName(loginUserContent.getUserName());   //记录谁操作了本次记录信息
            deleteHosue.setUpdateTime(new Date());                           //记录最近的一次修改时间
            deleteHosue.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_YES.KEY);

            //持久化到数据库中
            hqPlotHouseExtendMapper.updateByPrimaryKeySelective(deleteHosue);

            return SystemResult.ok("房屋信息删除成功");
        } catch (Exception e) {
            throw e;
        } finally {
            //解锁操作
            HouseLock.HOUSE_UPDATE_LOCK.writeLock().unlock();
        }
    }


    /**
     * 批量插入房屋动迁信息
     *
     * @param houses 房屋动迁信息集
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult batchInsertHouses(List<HqPlotHouseDefinition> houses) {

        //获取最新的房屋信息集
        SystemResult plotContentListByName = plotService.getPlotContentListByName(null);
        List<HqPlot> plots = (List<HqPlot>) plotContentListByName.getResult();
        //转换成map
        Map<String, Integer> collect = plots.stream().collect(Collectors.toMap(HqPlot::getPlotName, HqPlot::getPlotId, (test1, test2) -> test1));
        if (collect == null || collect.size() < 1) {
            return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY, "当前对应的房屋信息没有,所有仔细查看你选择的房屋信息");
        }

        //创建一个map用于判断本次存储房屋动名称是否有相同的
        HashMap<String, String> houseNameMap = new HashMap<>();

        //获取当前本地用户信息
        UserLoginContent user = (UserLoginContent) request.getAttribute(LOGIN_STATE.USER_LOGIN_TOKEN.toString());

        //对houses进行属性内容扩展添加
        for (int index = 0; index < houses.size(); index++) {
            HqPlotHouseDefinition house = houses.get(index);

            //判断当前的房屋动迁对有的房屋信息是否存在
            if (!collect.containsKey(house.getPlotName())) {
                return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY, "在第" + (index + 3) + "行当前对应的房屋信息:" + house.getPlotName() + "不存在");
            }

            HqPlotHouse currentHouse = hqPlotHouseExtendMapper.getHouseByName(house.getHouseName(), GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY, null);
            //判断动迁名称是否在houseNameMap中存在
            if (houseNameMap.containsKey(house.getHouseName()) || currentHouse != null || StrUtil.hasBlank(house.getHouseName())) {
                return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY, "在第" + (index + 3) + "行当前对应的房屋动迁名称:" + house.getHouseName() + "重复/异常");
            }

            //房屋房屋居住类型是否合法
            if (!house.getHouseType().equals(HOUSE_TABLE_FILED_STATE.STATE.LIVE) &&
                    !house.getHouseType().equals(HOUSE_TABLE_FILED_STATE.STATE.NOLIVE)
            ) {
                return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY, "在第" + (index + 3) + "行当前对应的房屋动迁类型" + house.getHouseType() + "名称异常");
            }


            //将动迁新添加的房屋名称存储到houseNameMap
            houseNameMap.put(house.getHouseName(), house.getHouseName());

            house.setPlotId(collect.get(house.getPlotName()));
            house.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
            house.setCreateTime(new Date());
            house.setUpdateTime(new Date());
            house.setUpdateUserName(user.getUserName());
        }

        //判断当前是否有新的信息存在
        if (houses != null && plots.size() > 0) {
            //并批量插入持久化到数据库中
            hqPlotHouseExtendMapper.batchInsertHouses(houses);

        }
        return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY, "房屋动迁信息导入成功");
    }


}
