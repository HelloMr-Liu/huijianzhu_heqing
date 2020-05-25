package com.huijianzhu.heqing.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.enums.GLOBAL_TABLE_FILED_STATE;
import com.huijianzhu.heqing.enums.LOGIN_STATE;
import com.huijianzhu.heqing.enums.USER_TABLE_FIELD_STATE;
import com.huijianzhu.heqing.mapper.extend.HqPropertyValueExtendMapper;
import com.huijianzhu.heqing.pojo.AccpetPlotTypePropertyValue;
import com.huijianzhu.heqing.pojo.UserLoginContent;
import com.huijianzhu.heqing.service.PropertyValueService;
import com.huijianzhu.heqing.utils.CookieUtils;
import com.huijianzhu.heqing.vo.SystemResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * ================================================================
 * 说明: 操作属性值业务接口实现
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  18:04            创建
 * =================================================================
 **/
@Service
public class PropertyValueServiceImpl implements PropertyValueService {

    @Autowired
    HttpServletRequest request;

    @Autowired
    LoginTokenCacheManager loginTokenCacheManager;

    @Autowired
    HqPropertyValueExtendMapper hqPropertyValueExtendMapper; //注入操作属性值表mapper接口

    /**
     * 添加属性值
     *
     * @param propertyValues 某一个地块类型对应的一组属性对应的属性值信息集
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult updatePropertyValue(List<AccpetPlotTypePropertyValue> propertyValues) throws Exception {


        //创建map集合一个封装修改属性值一个封装新添加属性值
        HashMap<String, List<AccpetPlotTypePropertyValue>> updateMap = new HashMap<>();
        updateMap.put("ADD", new ArrayList<>());
        updateMap.put("UPDATE", new ArrayList<>());

        //获取用户本地登录标识信息
        UserLoginContent userContent = (UserLoginContent) request.getAttribute(LOGIN_STATE.USER_LOGIN_TOKEN.toString());

        //将当前propertyValues中筛选出是否是添加的还是修改的数值信息
        propertyValues.forEach(
                e -> {
                    e.setUpdateTime(new Date());
                    e.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
                    e.setUpdateUserName(userContent == null ? "" : userContent.getUserName());
                    if (e.getPropertyValueId() == null || e.getPropertyValueId() < 1) {
                        e.setPropertyValueId(null);
                        e.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
                        e.setCreateTime(new Date());
                        //当前添加属性值信息
                        updateMap.get("ADD").add(e);
                    } else {
                        //当前修改属性值信息
                        updateMap.get("UPDATE").add(e);
                    }
                }
        );
        if (updateMap.get("ADD").size() > 0) {
            //批量插入属性值操作
            hqPropertyValueExtendMapper.batchInsertProperties(updateMap.get("ADD"));
        }

        if (updateMap.get("UPDATE").size() > 0) {
            //批量修改属性值操作
            hqPropertyValueExtendMapper.batchUpdateProperties(updateMap.get("UPDATE"));
        }

        return SystemResult.ok();
    }
}
