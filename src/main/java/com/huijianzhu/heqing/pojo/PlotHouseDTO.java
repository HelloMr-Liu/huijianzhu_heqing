package com.huijianzhu.heqing.pojo;

import com.huijianzhu.heqing.entity.HqPlotHouse;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * ================================================================
 * 说明：封装地块房屋动迁扩展信息
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/10  20:34            创建
 * =================================================================
 **/
@Data
public class PlotHouseDTO extends HqPlotHouse {
    List<HqPlotHouse> liveList;     //存储房屋动迁居住的信息集
    List<HqPlotHouse> NotLiveList;  //存储房屋动迁非居住的信息集
    private Integer plotId;         //地块id
    private String plotName;       //地块名称
    private Date plotCreateTime;    //地块创建时间(用作与排序用)
}
    
    
    