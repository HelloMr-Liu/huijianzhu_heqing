package com.huijianzhu.heqing.pojo;

import com.huijianzhu.heqing.entity.HqNoLiveAccount;
import lombok.Data;

import java.util.List;

/**
 * ================================================================
 * 说明：封装非居住付款台账金额实体
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/13  9:57            创建
 * =================================================================
 **/
@Data
public class NoLiveMoneyData {
    private Long noPay;
    private Long pay;
    private List<HqNoLiveAccount> allList;


}
