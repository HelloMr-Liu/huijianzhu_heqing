package com.huijianzhu.heqing.mapper;

import com.huijianzhu.heqing.entity.HqUser;

public interface HqUserMapper {
    int deleteByPrimaryKey(Integer userId);

    int insert(HqUser record);

    int insertSelective(HqUser record);

    HqUser selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(HqUser record);

    int updateByPrimaryKey(HqUser record);
}