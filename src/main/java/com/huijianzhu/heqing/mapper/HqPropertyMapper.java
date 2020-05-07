package com.huijianzhu.heqing.mapper;

import com.huijianzhu.heqing.entity.HqProperty;

public interface HqPropertyMapper {
    int deleteByPrimaryKey(Integer propertyId);

    int insert(HqProperty record);

    int insertSelective(HqProperty record);

    HqProperty selectByPrimaryKey(Integer propertyId);

    int updateByPrimaryKeySelective(HqProperty record);

    int updateByPrimaryKey(HqProperty record);
}