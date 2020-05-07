package com.huijianzhu.heqing.pojo;

import com.huijianzhu.heqing.entity.HqProperty;
import lombok.Data;

import java.util.List;

/**
 * ================================================================
 * 说明：存储各个属性信息对应的属性树结构
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/7  13:20            创建
 * =================================================================
 **/
@Data
public class PropertyTree extends HqProperty {

    //父属性下对应的子属性信息集
    private List<PropertyTree> children;
}
