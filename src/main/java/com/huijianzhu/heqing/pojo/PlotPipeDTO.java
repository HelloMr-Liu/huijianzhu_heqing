package com.huijianzhu.heqing.pojo;

import com.huijianzhu.heqing.entity.HqPlotPipe;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * ================================================================
 * 说明：封装地块管道搬迁拓展信息
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/10  20:59            创建
 * =================================================================
 **/
@Data
public class PlotPipeDTO extends HqPlotPipe  {
    private Integer plotId;         //地块id
    private String  plotName;       //地块名称
    private Date PlotCreateTime;    //地块创建时间(用作与排序用)
    List<HqPlotPipe> PipeList;      //存储管道搬迁信息集
}
    
    
    