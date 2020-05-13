package com.huijianzhu.heqing.mapper;

import com.huijianzhu.heqing.entity.HqPipeAccount;

public interface HqPipeAccountMapper {
    int deleteByPrimaryKey(Integer pipeAccountId);

    int insert(HqPipeAccount record);

    int insertSelective(HqPipeAccount record);

    HqPipeAccount selectByPrimaryKey(Integer pipeAccountId);

    int updateByPrimaryKeySelective(HqPipeAccount record);

    int updateByPrimaryKey(HqPipeAccount record);
}