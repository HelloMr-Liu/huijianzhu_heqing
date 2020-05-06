package com.huijianzhu.heqing.mapper;

import com.huijianzhu.heqing.entity.HqPermissions;

public interface HqPermissionsMapper {
    int deleteByPrimaryKey(String modelId);

    int insert(HqPermissions record);

    int insertSelective(HqPermissions record);

    HqPermissions selectByPrimaryKey(String modelId);

    int updateByPrimaryKeySelective(HqPermissions record);

    int updateByPrimaryKey(HqPermissions record);
}