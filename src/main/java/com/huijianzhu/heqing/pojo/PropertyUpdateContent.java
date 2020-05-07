package com.huijianzhu.heqing.pojo;

import com.huijianzhu.heqing.entity.HqProperty;
import com.huijianzhu.heqing.entity.HqWriteWay;
import lombok.Data;

import java.util.List;

/**
 * ================================================================
 * 说明：用户封装显示指定修改用户信息类
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/7  16:03            创建
 * =================================================================
 **/
@Data
public class PropertyUpdateContent extends HqProperty {
    private List<HqWriteWay> writeWays;
}
