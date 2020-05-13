package com.huijianzhu.heqing.pojo;

import com.huijianzhu.heqing.entity.HqHouseResettlement;
import lombok.Data;

import java.util.List;

/**
 * ================================================================
 * 说明：封装房屋动迁量信息实体类
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/13  13:26            创建
 * =================================================================
 **/
@Data
public class HouseResettlementData {


    /**
     * 已动迁量
     */
    private Integer resettlement;


    /**
     * 剩余动迁量
     */
    private Integer surplusResettlement;


    /**
     * 房屋动迁量信息集
     */
    private List<HqHouseResettlement> resettlements;
}
