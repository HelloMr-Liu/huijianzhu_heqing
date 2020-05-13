package com.huijianzhu.heqing.service.impl;

import cn.hutool.core.util.StrUtil;
import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.entity.HqHouseResettlement;
import com.huijianzhu.heqing.entity.HqPipeAccount;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.enums.GLOBAL_TABLE_FILED_STATE;
import com.huijianzhu.heqing.enums.LOGIN_STATE;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.lock.HouseAccountLock;
import com.huijianzhu.heqing.mapper.extend.HqHouseResettlementExtendMapper;
import com.huijianzhu.heqing.pojo.HouseResettlementData;
import com.huijianzhu.heqing.pojo.UserLoginContent;
import com.huijianzhu.heqing.service.HouseResettlementService;
import com.huijianzhu.heqing.service.HouseService;
import com.huijianzhu.heqing.service.PlotService;
import com.huijianzhu.heqing.utils.CookieUtils;
import com.huijianzhu.heqing.vo.SystemResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ================================================================
 * 说明：操作房屋动迁信息业务接口实现
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/13  13:29            创建
 * =================================================================
 **/
@Service
public class HouseResettlementServiceImpl  implements HouseResettlementService {


    @Autowired
    private HttpServletRequest request;

    /**
     * 注入：地块信息业务接口
     */
    @Autowired
    private PlotService plotService;


    /**
     * 注入：登录标识信息缓存管理
     */
    @Autowired
    private LoginTokenCacheManager tokenCacheManager;


    /**
     * 注入：操作房屋动迁信息数据mapper扩展接口
     */
    @Autowired
    private HqHouseResettlementExtendMapper hqHouseResettlementExtendMapper;



    /**
     * 获取所有房屋动迁量信息集
     * @return
     */
    public SystemResult findAll(){
        List<HqHouseResettlement> all = hqHouseResettlementExtendMapper.findAll(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);

        //创建2个变量存储已动迁量,和剩余动迁量
        int okResettlement=0;
        int surplusResettlement=0;

        for(HqHouseResettlement element:all){
            okResettlement+=Integer.parseInt(StrUtil.hasBlank(element.getNoLiveNumber())?"0":element.getNoLiveNumber());
            okResettlement+=Integer.parseInt(StrUtil.hasBlank(element.getLiveNumber())?"0":element.getLiveNumber());
            surplusResettlement+=Integer.parseInt(StrUtil.hasBlank(element.getSurplusNumber())?"0":element.getSurplusNumber());  //同居剩余数量
        }
        HouseResettlementData data=new HouseResettlementData();
        data.setResettlement(okResettlement);
        data.setSurplusResettlement(surplusResettlement);
        data.setResettlements(all);
        return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY,data);

    }


    /**
     * 批量导入房屋动迁信息集
     * @param resettlements
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult batchImport(List<HqHouseResettlement> resettlements){

        //开启原子锁操作防止信息重复添加
        HouseAccountLock.UPDATE_LOCK.writeLock().lock();
        try{
            //获取当前客户端信息
            String cookieValue = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent user = tokenCacheManager.getCacheUserByLoginToken(cookieValue);


            //获取最新的地块信息集,并转换成map
            SystemResult plotContentListByName = plotService.getPlotContentListByName(null);
            List<HqPlot> plots=(List<HqPlot>)plotContentListByName.getResult();
            Map<String, Integer> collect = plots.stream().collect(Collectors.toMap(HqPlot::getPlotName, HqPlot::getPlotId, (test1, test2) -> test1));


            //获取元房屋动迁信息集
            List<HqHouseResettlement> resettlements1 = ((HouseResettlementData) findAll().getResult()).getResettlements();
            Map<String, Integer> map = resettlements1.stream().collect(Collectors.toMap(HqHouseResettlement::getPlotName, HqHouseResettlement::getResettlementId,(test1, test2) -> test1 ));


            //创建2个集合一个存储批量插入的一个存储批量修改的
            List<HqHouseResettlement> batchAddList=new ArrayList<>();
            List<HqHouseResettlement> batchUdpateList=new ArrayList<>();

            //创建一个map用于判断本次存储名称是否有相同的
            HashMap<String,Integer> nameMap=new HashMap<>();


            for(int index=0;index<resettlements.size();index++){

                //获取房屋信息
                HqHouseResettlement house = resettlements.get(index);

                //判断动迁房屋动迁对应的地块信息是否有效
                if(!collect.containsKey(house.getPlotName())){
                    return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY,"在第"+(index+3)+"行当前对应的地块信息:"+house.getPlotName()+"不存在");
                }

                //判断本次操作信息对应的地块信息是否有重复
                if(nameMap.containsKey(house.getPlotName())){
                    return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY,"在第"+(index+3)+"行当前对应的地块编号名称:"+house.getPlotName()+"==与第"+nameMap.get(house.getPlotName())+"行重复");
                }
                nameMap.put(house.getPlotName(),index+3); //存储当前地块对应的,行数

                house.setUpdateTime(new Date());
                house.setUpdateUserName(user.getUserName());
                house.setPlotId(collect.get(house.getPlotName()));

                if(map!=null&&map.size()>0&&map.containsKey(house.getPlotName())){
                    //代表当前是一个修改操作
                    house.setResettlementId(map.get(house.getPlotName()));

                    batchUdpateList.add(house);
                }else{
                    //代表添加操作
                    batchAddList.add(house);
                }
            }
            if(batchAddList.size()>0){
                //批量添加
                hqHouseResettlementExtendMapper.batchAdd(batchAddList);
            }
            if(batchUdpateList.size()>0){

                //批量修改
                hqHouseResettlementExtendMapper.batchUpdate(batchUdpateList);
            }

            return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY,"本次房屋动迁信息导入成功...");
        }finally {
            //解锁操作,一定要放在finally中不然如果出现异常 就不会释放锁,造成线程阻塞
            HouseAccountLock.UPDATE_LOCK.writeLock().unlock();
        }
    }
}
