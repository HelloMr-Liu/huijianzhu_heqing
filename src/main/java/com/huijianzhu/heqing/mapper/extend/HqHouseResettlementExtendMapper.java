package com.huijianzhu.heqing.mapper.extend;

import com.huijianzhu.heqing.entity.HqHouseResettlement;
import com.huijianzhu.heqing.mapper.HqHouseResettlementMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * ================================================================
 * 说明：操作房屋动迁量数据mapper扩展接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/13  11:22            创建
 * =================================================================
 **/
public interface HqHouseResettlementExtendMapper extends HqHouseResettlementMapper {


    /**
     * 查询出有效房屋动迁量信息集
     *
     * @param delFlag 删除标志
     * @return
     */
    @Select("select hhr.* from hq_plot hp  " +
            "left join  hq_house_resettlement hhr on  hp.plot_id=hhr.plot_id " +
            "where hp.del_flag=#{delFlag} and hhr.plot_name is not null " +
            "order by hhr.no_live_number,hhr.live_number,hhr.surplus_number desc")
    List<HqHouseResettlement> findAll(String delFlag);


    /**
     * 批量插入操作
     */
    @Insert
            ({
                    "<script>",
                    "insert into hq_house_resettlement ( no_live_number, live_number, ",
                    "surplus_number,plot_id,plot_name,update_time,update_user_name)",

                    " values ",
                    "<foreach collection='houses' item='item' index='index' separator=','>",
                    "(",
                    " #{item.noLiveNumber}, #{item.liveNumber},#{item.surplusNumber},#{item.plotId},#{item.plotName},#{item.updateTime},#{item.updateUserName}",
                    ")",
                    "</foreach>",
                    "</script>"
            })
    void batchAdd(@Param("houses") List<HqHouseResettlement> houses);


    /**
     * 批量修改操作
     *
     * @param houses
     */
    @Update
            ({
                    "<script> ",
                    " UPDATE  hq_house_resettlement",
                    " <trim prefix ='set' prefixOverrides=',' > ",

                    "<trim prefix ='no_live_number = case' suffix='end,'>",
                    "<foreach collection ='houses' item ='item' index = 'index'> ",
                    "when resettlement_id = #{item.resettlementId} then #{item.noLiveNumber} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='live_number = case' suffix='end,'>",
                    "<foreach collection ='houses' item ='item' index = 'index'> ",
                    "when resettlement_id = #{item.resettlementId} then #{item.liveNumber} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='surplus_number = case' suffix='end,'>",
                    "<foreach collection ='houses' item ='item' index = 'index'> ",
                    "when resettlement_id = #{item.resettlementId} then #{item.surplusNumber} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='plot_id = case' suffix='end,'>",
                    "<foreach collection ='houses' item ='item' index = 'index'> ",
                    "when resettlement_id = #{item.resettlementId} then #{item.plotId} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='plot_name = case' suffix='end,'>",
                    "<foreach collection ='houses' item ='item' index = 'index'> ",
                    "when resettlement_id = #{item.resettlementId} then #{item.plotName} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='update_time = case' suffix='end,'>",
                    "<foreach collection ='houses' item ='item' index = 'index'> ",
                    "when resettlement_id = #{item.resettlementId} then #{item.updateTime} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='update_user_name = case' suffix='end'>",
                    "<foreach collection ='houses' item ='item' index = 'index'> ",
                    "when resettlement_id = #{item.resettlementId} then #{item.updateUserName} ",
                    "</foreach>",
                    "</trim> ",

                    " </trim> ",
                    " WHERE resettlement_id in ",
                    " (",
                    "<foreach collection='houses' item='item' index='index' separator=','>",
                    " #{item.resettlementId}",
                    "</foreach>",
                    " )",
                    "</script>"
            })
    void batchUpdate(@Param("houses") List<HqHouseResettlement> houses);


}