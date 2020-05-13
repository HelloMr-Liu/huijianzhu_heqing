package com.huijianzhu.heqing.mapper;

import com.huijianzhu.heqing.entity.HqHouseResettlement;

public interface HqHouseResettlementMapper {
    int deleteByPrimaryKey(Integer resettlementId);

    int insert(HqHouseResettlement record);

    int insertSelective(HqHouseResettlement record);

    HqHouseResettlement selectByPrimaryKey(Integer resettlementId);

    int updateByPrimaryKeySelective(HqHouseResettlement record);

    int updateByPrimaryKey(HqHouseResettlement record);
}