package com.huijianzhu.heqing.mapper;

import com.huijianzhu.heqing.entity.HqPropertyValue;
import com.huijianzhu.heqing.entity.HqPropertyValueWithBLOBs;

public interface HqPropertyValueMapper {
    int deleteByPrimaryKey(Integer propertyValueId);

    int insert(HqPropertyValueWithBLOBs record);

    int insertSelective(HqPropertyValueWithBLOBs record);

    HqPropertyValueWithBLOBs selectByPrimaryKey(Integer propertyValueId);

    int updateByPrimaryKeySelective(HqPropertyValueWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(HqPropertyValueWithBLOBs record);

    int updateByPrimaryKey(HqPropertyValue record);
}