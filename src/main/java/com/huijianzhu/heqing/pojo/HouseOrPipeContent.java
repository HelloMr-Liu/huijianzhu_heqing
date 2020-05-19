package com.huijianzhu.heqing.pojo;


import lombok.Data;

/**
 * ================================================================
 * 说明：封装指定地块id对应的房屋或管道信息,封装数据库查询出的结果实体,
 * 该实体目前用作于地块信息删除判断操作
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  14:20            创建
 * =================================================================
 **/
@Data
public class HouseOrPipeContent {

    private String plotId;      //地块id
    private String typeId;      //对应的是地块房屋表id或 地块管道表id
    private String currentType; //当前类型  值如果小于0代表的是管道信息 否则是房屋信息
}
