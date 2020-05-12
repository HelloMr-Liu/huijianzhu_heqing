package com.huijianzhu.heqing.definition;

import com.huijianzhu.heqing.entity.HqPlotHouse;

/**
 * ================================================================
 * 说明：当前类说说明
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/12  14:27            创建
 * =================================================================
 **/
public class HqPlotHouseDefinition extends HqPlotHouse {


    private String plotName;  //地块名称

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }
}
