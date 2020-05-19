package com.huijianzhu.heqing.definition;

import lombok.Data;

/**
 * ================================================================
 * 说明：封装子属性信息接收类
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/7  13:42            创建
 * =================================================================
 **/
@Data
public class PropertyAccpetDefinition {
    private Integer propertyId;          //属性id
    private String propertyName;        //属性名称
    private String proSort;             //排序条件
    private String writeId;             //填写类型id
    private String writeName;           //填写类型的名称
    private String unitContent;         //填写单位对应的内容 多个以逗号隔开
    private String showCondition;       //显示条件： 0：什么都不保障 1：保障不显示 2：非保障显示 3：根据居住/非居住变更名称
    private String showWay;             //显示方式： 0：不是竖向 1：是竖向
    private Integer parentId;            //父属性id
    private String isParent;            //判断是否是父属性
    private String propertyType;        //属性类型(1地块、2房屋、3管道)

}
