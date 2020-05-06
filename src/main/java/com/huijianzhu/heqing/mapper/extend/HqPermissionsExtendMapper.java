package com.huijianzhu.heqing.mapper.extend;

import com.huijianzhu.heqing.entity.HqPermissions;
import com.huijianzhu.heqing.mapper.HqPermissionsMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ================================================================
 * 说明：操作权限信息扩展mapper
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/6  11:51            创建
 * =================================================================
 **/
public interface HqPermissionsExtendMapper extends HqPermissionsMapper {

    /**
     * 获取所有权限信息
     * @return
     */
    @Select(" select * from hq_permissions ")
    List<HqPermissions> getPermissions();
}
