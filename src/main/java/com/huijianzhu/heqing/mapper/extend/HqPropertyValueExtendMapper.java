package com.huijianzhu.heqing.mapper.extend;

import com.huijianzhu.heqing.entity.HqPropertyValue;
import com.huijianzhu.heqing.entity.HqPropertyValueWithBLOBs;
import com.huijianzhu.heqing.mapper.HqPropertyValueMapper;
import com.huijianzhu.heqing.pojo.AccpetPlotTypePropertyValue;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * ================================================================
 * 说明：对属性值mapper接口扩展
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  15:11            创建
 * =================================================================
 **/
public interface HqPropertyValueExtendMapper extends HqPropertyValueMapper {


    /**
     * 获取指定的plotType、plotTypeId对应的属性值信息
     *
     * @param plotType   地块类型(地块、房屋、管道
     * @param plotTypeId 地块类型表信息id
     * @param delFlag    删除标志
     * @return
     */
    @Select(" select * from hq_property_value where plot_type=#{plotType} and plot_type_id=#{plotTypeId} and del_flag=#{delFlag} ")
    List<HqPropertyValueWithBLOBs> getPropertyValues(String plotType, Integer plotTypeId, String delFlag);


    /**
     * 批量插入对应的地块类型对应某一个地块信息组对应的属性值集
     *
     * @param propertyValues
     */
    @Insert
            ({
                    "<script>",
                    "insert into hq_property_value ( plot_type, plot_type_id, ",
                    "property_id,property_value,property_value_desc,",
                    "del_flag,create_time,update_time,update_user_name)",
                    " values ",
                    "<foreach collection='propertyValues' item='item' index='index' separator=','>",
                    "(",
                    " #{item.plotType}, #{item.plotTypeId}, #{item.propertyId}, #{item.propertyValue} ,",
                    " #{item.propertyValueDesc}, #{item.delFlag}, #{item.createTime}, #{item.updateTime}, #{item.updateUserName}",
                    ")",
                    "</foreach>",
                    "</script>"
            })
    void batchInsertProperties(@Param("propertyValues") List<AccpetPlotTypePropertyValue> propertyValues);


    /**
     * 批量修改对应的地块类型对应某一个地块信息组对应的属性值集
     *
     * @param propertyValues
     */
    @Update
            ({
                    "<script> ",
                    " UPDATE  hq_property_value",
                    " <trim prefix ='set' prefixOverrides=',' > ",

                    "<trim prefix ='property_value = case' suffix='end,'>",
                    "<foreach collection ='propertyValues' item ='item' index = 'index'> ",
                    "when property_value_id = #{item.propertyValueId} then #{item.propertyValue} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='property_value_desc = case' suffix='end,'>",
                    "<foreach collection ='propertyValues' item ='item' index = 'index'> ",
                    "when property_value_id = #{item.propertyValueId} then #{item.propertyValueDesc} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='del_flag = case' suffix='end,'>",
                    "<foreach collection ='propertyValues' item ='item' index = 'index'> ",
                    "when property_value_id = #{item.propertyValueId} then #{item.delFlag} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='update_time = case' suffix='end,'>",
                    "<foreach collection ='propertyValues' item ='item' index = 'index'> ",
                    "when property_value_id = #{item.propertyValueId} then #{item.updateTime} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='update_user_name = case' suffix='end'>",
                    "<foreach collection ='propertyValues' item ='item' index = 'index'> ",
                    "when property_value_id = #{item.propertyValueId} then #{item.updateUserName} ",
                    "</foreach>",
                    "</trim> ",

                    " </trim> ",
                    " WHERE property_value_id in ",
                    " (",
                    "<foreach collection='propertyValues' item='item' index='index' separator=','>",
                    " #{item.propertyValueId}",
                    "</foreach>",
                    " )",
                    "</script>"
            })
    void batchUpdateProperties(@Param("propertyValues") List<AccpetPlotTypePropertyValue> propertyValues);


    /**
     * 批量删除属性值信息
     *
     * @param type    地块类型(地块、房屋、管道
     * @param typeId  地块类型表信息id
     * @param delFalg 删除标志
     */
    @Update({
            "<script> ",
            "UPDATE hq_property_value SET ",

            "del_flag = CASE plot_type_id ",
            "WHEN #{typeId} THEN #{delFalg} ",
            "END,",

            "update_time = CASE plot_type_id ",
            "WHEN #{typeId} THEN sysdate() ",
            "END,",

            "update_user_name = CASE plot_type_id ",
            "WHEN #{typeId} THEN #{userName} ",
            "END ",
            "WHERE plot_type=#{type}  AND plot_type_id=#{typeId}",
            "</script>"
    })
    void batchDeleteProperties(String type, Integer typeId, String delFalg, String userName);
}