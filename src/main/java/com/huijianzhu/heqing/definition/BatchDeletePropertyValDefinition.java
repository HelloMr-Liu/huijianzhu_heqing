package com.huijianzhu.heqing.definition;

import lombok.Data;

/**
 * 描述：按照对应的删除条件
 * 批量删除对应的属性值信息
 *
 * @author 刘梓江
 * @date 2020/6/8  14:08
 */
@Data
@SuppressWarnings("all")
public class BatchDeletePropertyValDefinition {


    /**
     * 地块类型(地块、房屋、管道
     */
    private String type;

    /**
     * 地块类型表信息id
     */
    private String typeId;


    /**
     * 删除标志
     */
    private String delFalg;

}
