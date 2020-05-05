package com.huijianzhu.heqing.mapper;

import com.huijianzhu.heqing.entity.HqPermissions;

public interface HqPermissionsMapper {
    int deleteByPrimaryKey(Integer permissionsId);

    int insert(HqPermissions record);

    int insertSelective(HqPermissions record);

    HqPermissions selectByPrimaryKey(Integer permissionsId);

    int updateByPrimaryKeySelective(HqPermissions record);

    int updateByPrimaryKey(HqPermissions record);
}