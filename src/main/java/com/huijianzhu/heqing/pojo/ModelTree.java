package com.huijianzhu.heqing.pojo;

import lombok.Data;

import java.util.List;

/**
 * ================================================================
 * 说明：用于封装对应的树结构信息
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/6  11:33            创建
 * =================================================================
 **/
@Data
public class ModelTree {
    private String id;                  //树形id
    private String label;               //树形描述
    private String icon_class;          //树形图标]
    private String requestPath;         //默认父请求路径
    private List<ModelTree> children;  //子类树信息
}
