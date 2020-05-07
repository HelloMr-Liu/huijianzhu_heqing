package com.huijianzhu.heqing.mapper;

import com.huijianzhu.heqing.entity.HqWriteWay;

public interface HqWriteWayMapper {
    int deleteByPrimaryKey(String writeId);

    int insert(HqWriteWay record);

    int insertSelective(HqWriteWay record);

    HqWriteWay selectByPrimaryKey(String writeId);

    int updateByPrimaryKeySelective(HqWriteWay record);

    int updateByPrimaryKey(HqWriteWay record);
}