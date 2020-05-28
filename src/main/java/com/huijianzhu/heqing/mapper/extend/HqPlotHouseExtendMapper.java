package com.huijianzhu.heqing.mapper.extend;

import com.huijianzhu.heqing.definition.HqPlotHouseDefinition;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.entity.HqPlotHouse;
import com.huijianzhu.heqing.mapper.HqPlotHouseMapper;
import com.huijianzhu.heqing.pojo.PlotHouseDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ================================================================
 * 说明：房屋动迁扩展mapper
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/10  20:25            创建
 * =================================================================
 **/
public interface HqPlotHouseExtendMapper extends HqPlotHouseMapper {


    /**
     * 查询与指定房屋动迁对应的动迁信息(默认查询全部)
     *
     * @param houseName 房屋名称
     * @param delFalg   删除标志
     * @return
     */
    @Select({
            "<script>",
            " SELECT hp.plot_id plotId,hp.plot_name plotName,hp.create_time plotCreateTime, hph.* ",
            " FROM hq_plot hp left join   (select * from hq_plot_house where del_flag=#{delFalg}) hph  ",
            " on hph.plot_id=hp.plot_id  ",
            " where hp.del_flag=#{delFalg}  ",
            " <if test='houseName!=null ' >",
            " and  house_name like concat('%',#{houseName},'%') ",
            " </if>",
            " order by hp.create_time,hph.create_time desc  ",
            "</script>"
    })
    List<PlotHouseDTO> getPlotHouseByName(String houseName, String delFalg);


    /**
     * 判断当前是否有对应的相同的房屋动迁信息
     *
     * @param houseName 房屋名称
     * @param delFlag   删除标志
     * @param houseId   指定房屋id
     * @return
     */
    @Select({
            "<script>",
            " select * from hq_plot_house where del_flag=#{delFlag} and house_name=#{houseName} ",
            " <if test='houseId!=null'>",
            "  and house_id!=#{houseId} ",
            " </if>",
            "</script>"})
    HqPlotHouse getHouseByName(String houseName, String delFlag, Integer houseId);


    /**
     * 批量插入房屋动迁信息
     *
     * @param houses
     */
    @Insert
            ({
                    "<script>",
                    "insert into hq_plot_house (house_name, house_type,plot_id,create_time,update_time,update_user_name,del_flag)",
                    " values ",
                    "<foreach collection='houses' item='item' index='index' separator=','>",
                    "(",
                    " #{item.houseName}, #{item.houseType},#{item.plotId},#{item.createTime},#{item.updateTime},#{item.updateUserName},#{item.delFlag}",
                    ")",
                    "</foreach>",
                    "</script>"
            })
    void batchInsertHouses(@Param("houses") List<HqPlotHouseDefinition> houses);
}
