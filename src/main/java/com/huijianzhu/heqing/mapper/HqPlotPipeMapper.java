package com.huijianzhu.heqing.mapper;

import com.huijianzhu.heqing.entity.HqPlotPipe;

public interface HqPlotPipeMapper {
    int deleteByPrimaryKey(Integer pipeId);

    int insert(HqPlotPipe record);

    int insertSelective(HqPlotPipe record);

    HqPlotPipe selectByPrimaryKey(Integer pipeId);

    int updateByPrimaryKeySelective(HqPlotPipe record);

    int updateByPrimaryKeyWithBLOBs(HqPlotPipe record);

    int updateByPrimaryKey(HqPlotPipe record);
}