package com.huijianzhu.heqing.pojo;

import com.huijianzhu.heqing.entity.HqPropertyValueWithBLOBs;
import lombok.Data;

import java.util.List;

/**
 * ================================================================
 * 说明：封装显示 地块或房屋或管道对应的信息实体
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  15:07            创建
 * =================================================================
 **/
@Data
public class PlotOrHouseOrPipeDesc {

    private List<PropertyTree> propertyTrees; //封装对应该地块或房屋或管道的一组属性信息树

    private List<HqPropertyValueWithBLOBs> propertyValues;  //对应属性信息树下对应的值内容

}
