package com.huijianzhu.heqing.mapper.extend;

import com.huijianzhu.heqing.entity.HqWriteWay;
import com.huijianzhu.heqing.mapper.HqWriteWayMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ================================================================
 * 说明：操作填写方式表扩展mapper
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/7  11:41            创建
 * =================================================================
 **/
public interface HqWriteWayExtendMapper extends HqWriteWayMapper {


    /**
     * 获取所有对应的填写方式信息
     *
     * @return
     */
    @Select(" SELECT * FROM `hq_write_way` order by write_id ")
    List<HqWriteWay> getWriteWays();
}