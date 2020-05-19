package com.huijianzhu.heqing.definition;

import com.huijianzhu.heqing.pojo.AccpetPlotTypePropertyValue;
import lombok.Data;

import java.util.List;

/**
 * ================================================================
 * 说明：封装修改 地块或房屋或管道信息的接收类
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  17:04            创建
 * =================================================================
 **/
@Data
public class PlotOrHouseOrPipeUpdateAccpetDefinition {

    //封装每一组对应的属性值(plotTypeId,propertyId,propertyValue)等参数
    List<AccpetPlotTypePropertyValue> propertyValueList;
    private Integer contentId;   //接收对应的内容id
    private String contentName; //接收内容名称
    //private String contentType; //接收内容类型 (1:代表当前是地块类型信息 2:代表当前是地块房屋类型信息 3:代表当前是地块管道类型信息)
    private String plotMark;    //接收一个地标信息
    //内容类型为1
    private String color;       //颜色
    private String lucency;     //透明度

}
