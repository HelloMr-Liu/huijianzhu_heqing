package com.huijianzhu.heqing.service.impl;

import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.entity.HqNoLiveAccount;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.enums.GLOBAL_TABLE_FILED_STATE;
import com.huijianzhu.heqing.enums.LOGIN_STATE;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.lock.HouseAccountLock;
import com.huijianzhu.heqing.mapper.extend.HqNoLiveAccountExtendMapper;
import com.huijianzhu.heqing.pojo.NoLiveMoneyData;
import com.huijianzhu.heqing.pojo.UserLoginContent;
import com.huijianzhu.heqing.service.NoLiveService;
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
 * 说明：非居住付款台账业务接口实现
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/12  16:46            创建
 * =================================================================
 **/
@Service
public class NoLiveServiceImpl implements NoLiveService {


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
     * 注入：操作非居住台账信息数据mapper数据扩展接口
     */
    @Autowired
    private HqNoLiveAccountExtendMapper hqNoLiveAccountExtendMapper;


    /**
     * 批量导入非居住付款台账信息
     *
     * @param noLiveList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult batchImport(List<HqNoLiveAccount> noLiveList) {

        //开启原子锁操作放在信息重复添加
        HouseAccountLock.UPDATE_LOCK.writeLock().lock();
        try {

            //获取当前本地用户信息
            UserLoginContent user = (UserLoginContent) request.getAttribute(LOGIN_STATE.USER_LOGIN_TOKEN.toString());


            //获取最新的地块信息集
            SystemResult plotContentListByName = plotService.getPlotContentListByName(null);
            List<HqPlot> plots = (List<HqPlot>) plotContentListByName.getResult();
            //转换成map
            Map<String, Integer> collect = plots.stream().collect(Collectors.toMap(HqPlot::getPlotName, HqPlot::getPlotId, (test1, test2) -> test1));
            if (collect == null || collect.size() < 1) {
                return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY, "当前对应的地块信息没有,所有仔细查看你选择的地块信息");
            }

            //获取所有的非居住付款台账信息集(
            List<HqNoLiveAccount> currNoLiveList = ((NoLiveMoneyData) (findAll().getResult())).getAllList();
            Map<String, HqNoLiveAccount> map = currNoLiveList.stream().collect(Collectors.toMap(HqNoLiveAccount::getPlotName, Function.identity(), (test1, test2) -> test1));

            //创建一个map用于判断本次存储名称是否有相同的
            HashMap<String, Integer> nameMap = new HashMap<>();


            //创建2个集合一个存储批量插入的一个存储批量修改的
            List<HqNoLiveAccount> batchAddList = new ArrayList<>();
            List<HqNoLiveAccount> batchUdpateList = new ArrayList<>();

            for (int index = 0; index < noLiveList.size(); index++) {
                HqNoLiveAccount account = noLiveList.get(index);
                //判断当前的房屋动迁对有的地块信息是否存在
                if (!collect.containsKey(account.getPlotName())) {
                    return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY, "在第" + (index + 3) + "行当前对应的地块信息:" + account.getPlotName() + "不存在");
                }

                //判断本次操作信息对应的地块信息是否有重复
                if (nameMap.containsKey(account.getPlotName())) {
                    return SystemResult.build(SYSTEM_RESULT_STATE.ADD_FAILURE.KEY, "在第" + (index + 3) + "行当前对应的地块编号名称:" + account.getPlotName() + "==与第" + nameMap.get(account.getPlotName()) + "行重复");
                }

                nameMap.put(account.getPlotName(), index + 3); //存储当前地块对应的,行数

                account.setUpdateUserName(user.getUserName());
                account.setUpdateTime(new Date());

                //判断当前新添加的地块对应的付款台账内容原来是否存在
                if (map != null && map.size() > 0 && map.containsKey(account.getPlotName())) {
                    HqNoLiveAccount hqNoLiveAccount = map.get(account.getPlotName());
                    //代表本次是修改操作
                    account.setNoLiveId(hqNoLiveAccount.getNoLiveId());
                    account.setPlotId(hqNoLiveAccount.getPlotId());

                    //批量修改中
                    batchUdpateList.add(account);

                } else {
                    //本次是添加操作
                    account.setPlotId(collect.get(account.getPlotName()));

                    //批量增加中
                    batchAddList.add(account);
                }
            }
            if (batchAddList != null && batchAddList.size() > 0) {
                //进行批量添加操作
                hqNoLiveAccountExtendMapper.batchAdd(batchAddList);
            }

            if (batchUdpateList != null && batchUdpateList.size() > 0) {
                //进行批量修改操作
                hqNoLiveAccountExtendMapper.batchUpdate(batchUdpateList);
            }
            return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY, "本次非居住付款台账导入成功");
        } finally {
            //解锁操作，一定要放在finally中不然 出现一次该锁就自定释放不了造成了锁阻塞
            HouseAccountLock.UPDATE_LOCK.writeLock().unlock();
        }
    }

    /**
     * 获取所有非居住付款台账信息集合
     *
     * @return
     */
    public SystemResult findAll() {

        //获取所有的台账信息
        List<HqNoLiveAccount> allList = hqNoLiveAccountExtendMapper.findAll(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        List<HqNoLiveAccount> collect = allList.stream()
                .sorted(
                        (e1, e2) -> {
                            return (int) ((e2.getUpdateTime().getTime()) / 1000 - (e1.getUpdateTime().getTime() / 1000));
                        }
                )
                .collect(Collectors.toList());


        //创建 存储已支付金额和支付总金额信息变量
        Long okPay = 0L;
        Long totalPay = 0L;

        for (HqNoLiveAccount account : collect) {
            okPay += Long.valueOf(account.getOkMoney()).longValue();              //存储每一个地块对应的已支付金额
            totalPay += Long.valueOf(account.getTotalDealMoney()).longValue();    //储每一个地块对应的支付总金额
        }
        NoLiveMoneyData data = new NoLiveMoneyData();
        data.setNoPay(totalPay - okPay);
        data.setPay(okPay);
        data.setAllList(collect);
        return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY, data);
    }

}
