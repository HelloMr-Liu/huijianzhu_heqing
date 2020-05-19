package com.huijianzhu.heqing.pojo;

import lombok.Data;

import java.util.List;

/**
 * ================================================================
 * 说明：封装用户对应的模块id及所有模块信息实体
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/6  13:58            创建
 * =================================================================
 **/
@Data
public class UserPermissions {
    private List<String> userModelIds;     //用户对应的模块id集合
    private List<ModelTree> treeList;      //所以的模块信息树集合
}
