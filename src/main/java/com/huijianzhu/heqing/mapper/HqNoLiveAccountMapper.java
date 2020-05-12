package com.huijianzhu.heqing.mapper;

import com.huijianzhu.heqing.entity.HqNoLiveAccount;

public interface HqNoLiveAccountMapper {
    int deleteByPrimaryKey(Integer noLiveId);

    int insert(HqNoLiveAccount record);

    int insertSelective(HqNoLiveAccount record);

    HqNoLiveAccount selectByPrimaryKey(Integer noLiveId);

    int updateByPrimaryKeySelective(HqNoLiveAccount record);

    int updateByPrimaryKey(HqNoLiveAccount record);
}