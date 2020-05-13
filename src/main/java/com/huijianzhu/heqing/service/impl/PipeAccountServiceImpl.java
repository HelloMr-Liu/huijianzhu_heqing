package com.huijianzhu.heqing.service.impl;

import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.entity.HqNoLiveAccount;
import com.huijianzhu.heqing.entity.HqPipeAccount;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.enums.GLOBAL_TABLE_FILED_STATE;
import com.huijianzhu.heqing.enums.LOGIN_STATE;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.lock.PipeAccountLock;
import com.huijianzhu.heqing.mapper.extend.HqPipeAccountExtendMapper;
import com.huijianzhu.heqing.pojo.UserLoginContent;
import com.huijianzhu.heqing.service.PipeAccountService;
import com.huijianzhu.heqing.service.PlotService;
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
 * 说明：操作管道搬迁费用业务接口实现
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/13  11:57            创建
 * =================================================================
 **/
@Service
public class PipeAccountServiceImpl implements PipeAccountService {

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
     * 注入：操作管道搬迁费用信息数据mapper扩展接口
     */
    @Autowired
    private HqPipeAccountExtendMapper hqPipeAccountExtendMapper;


    /**
     * 查询所有有效地块对应的管道搬迁费用信息集
     * @return
     */
    public SystemResult findAll(){
        List<HqPipeAccount> accounts = hqPipeAccountExtendMapper.findAll(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY,accounts);
    }

    /**
     * 批量管道管道搬迁信息集
     * @param pipeAccounts
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult batchImport(List<HqPipeAccount> pipeAccounts){

        //开启原子锁操作,放在信息重复添加
        PipeAccountLock.UPDATE_LOCK.writeLock().lock();
        try{

            //获取当前客户端信息
            String cookieValue = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent user = tokenCacheManager.getCacheUserByLoginToken(cookieValue);


            //获取最新的地块信息集,并转换成map
            SystemResult plotContentListByName = plotService.getPlotContentListByName(null);
            List<HqPlot> plots=(List<HqPlot>)plotContentListByName.getResult();
            Map<String, Integer> collect = plots.stream().collect(Collectors.toMap(HqPlot::getPlotName, HqPlot::getPlotId, (test1, test2) -> test1));


            //获取元管道搬迁费用信息集,并转换成map
            List<HqPipeAccount> accounts =(List<HqPipeAccount>)findAll().getResult();
            Map<String, Integer> collect2 = accounts.stream().collect(Collectors.toMap(HqPipeAccount::getPlotName, HqPipeAccount::getPipeAccountId, (test1, test2) -> test1));


            //创建2个集合一个存储批量插入的一个存储批量修改的
            List<HqPipeAccount> batchAddList=new ArrayList<>();
            List<HqPipeAccount> batchUdpateList=new ArrayList<>();


            //创建一个map用于判断本次存储名称是否有相同的
            HashMap<String,Integer> nameMap=new HashMap<>();

            for(int index=0;index<pipeAccounts.size();index++){

                //获取当前地块管道搬迁信息
                HqPipeAccount account = pipeAccounts.get(index);

                //判断当前的房屋动迁对有的地块信息是否存在
                if(!collect.containsKey(account.getPlotName())){
                    return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY,"在第"+(index+3)+"行当前对应的地块信息:"+account.getPlotName()+"不存在");

                }

                //判断本次操作信息对应的地块信息是否有重复
                if(nameMap.containsKey(account.getPlotName())){
                    return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY,"在第"+(index+3)+"行当前对应的地块编号名称:"+account.getPlotName()+"==与第"+nameMap.get(account.getPlotName())+"行重复");
                }

                nameMap.put(account.getPlotName(),index+3); //存储当前地块对应的,行数


                account.setUpdateTime(new Date());                              //修改时间
                account.setUpdateUserName(user.getUserName());                  //谁操作了本次记录
                account.setPlotId(collect.get(account.getPlotName()));          //地块编号
                if(collect2!=null&&collect2.size()>0&&collect2.containsKey(account.getPlotName())){

                    account.setPipeAccountId(collect2.get(account.getPlotName()));  //搬迁费用id
                    //并添加到批量修改集合中
                    batchUdpateList.add(account);

                }else{

                    //并添加到批量添加集合中
                    batchAddList.add(account);
                }
            }

            if(batchAddList.size()>0){
                //持久化到数据库中
                hqPipeAccountExtendMapper.batchAdd(batchAddList);
            }

            if(batchUdpateList.size()>0){
                //持久化到数据库中
                hqPipeAccountExtendMapper.batchUpdate(batchUdpateList);
            }
            return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY,"本次管道搬迁费用信息导入成功...");
        }finally {
            //进行锁释放,一定要放在finally中不然出现异常后锁没有释放造成阻塞
            PipeAccountLock.UPDATE_LOCK.writeLock().unlock();
        }
    }

}
