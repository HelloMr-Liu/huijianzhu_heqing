package com.huijianzhu.heqing.definition;

import lombok.Data;

/**
 * ================================================================
 * 说明：封装地块或房屋或管道信息的接收类
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  13:05            创建
 * =================================================================
 **/
@Data
public class PlotOrHouseOrPipeAccpetDefinition {


    //公共接收信息
    private String contentId;   //接收对应的内容id
    private String contentName; //接收内容名称
    //private String contentType; //接收内容类型 (1:代表当前是地块类型信息 2:代表当前是地块房屋类型信息 3:代表当前是地块管道类型信息)
    private String plotMark;    //接收一个地标信息

    //内容类型为1
    private String color;       //颜色
    private String lucency;     //透明度
    private String type;        //地块类型


    //内容类型为2,3的时候接收的值属性
    private Integer plotId;     //用于指定那个地块的信息内容

    //内容类型为2的时候接收的值属性
    private String houseType;   //房屋类型 LIVE居住   NOLIVE非居住


    //存储对应的实体id
    private String entityId;

}
