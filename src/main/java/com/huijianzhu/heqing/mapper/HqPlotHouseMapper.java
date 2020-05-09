package com.huijianzhu.heqing.mapper;

import com.huijianzhu.heqing.entity.HqPlotHouse;

public interface HqPlotHouseMapper {
    int deleteByPrimaryKey(Integer houseId);

    int insert(HqPlotHouse record);

    int insertSelective(HqPlotHouse record);

    HqPlotHouse selectByPrimaryKey(Integer houseId);

    int updateByPrimaryKeySelective(HqPlotHouse record);

    int updateByPrimaryKeyWithBLOBs(HqPlotHouse record);

    int updateByPrimaryKey(HqPlotHouse record);
}