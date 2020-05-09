package com.huijianzhu.heqing.mapper;

import com.huijianzhu.heqing.entity.HqPlot;

public interface HqPlotMapper {
    int deleteByPrimaryKey(Integer plotId);

    int insert(HqPlot record);

    int insertSelective(HqPlot record);

    HqPlot selectByPrimaryKey(Integer plotId);

    int updateByPrimaryKeySelective(HqPlot record);

    int updateByPrimaryKeyWithBLOBs(HqPlot record);

    int updateByPrimaryKey(HqPlot record);
}