package com.huijianzhu.heqing.mapper.extend;

import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.mapper.HqPlotMapper;
import com.huijianzhu.heqing.pojo.HouseOrPipeContent;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ================================================================
 * 说明：扩展地块mapper接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/8  13:23            创建
 * =================================================================
 **/
public interface HqPlotExtendMapper extends HqPlotMapper {

    /**
     * 获取查询出与地块名称相关的所有地块信息
     * @param plotName     获取地块名称
     * @param delFlag      删除标志
     * @return
     */
    @Select({"<script>",
             " select * from hq_plot where del_flag=#{delFlag} ",
             " <if test='plotName!=null'>",
             " and  plot_name like concat('%',#{plotName},'%') ",
             "</if>",
             " order by create_time desc",
             "</script>"})
    List<HqPlot> getPlots(String plotName,String delFlag);

    /**
     * 判断当前是否有对应的相同的地块信息
     * @param plotName  地块名称
     * @param delFlag   删除标志
     * @param plotId    指定地块id
     * @return
     */
    @Select({
            "<script>",
            " select * from hq_plot where del_flag=#{delFlag} and plot_name=#{plotName} ",
            " <if test='plotId!=null'>",
            " and plot_id!=#{plotId} ",
            "</if>",
            "</script>"})
    HqPlot getPlotByName(String plotName,String delFlag,Integer plotId);


    /**
     * 获取指定地块id对应的房屋信息或管道信息,如果有该地块就不能删除操作
     * @param plotId       地块id
     * @param delFlag      删除标志
     * @return
     */
    @Select("select plot_id plotId, house_id typeId ,house_type currentType from  hq_plot_house where del_flag=#{delFlag} and plot_id=#{plotId} " +
            "UNION all " +
            "select plot_id plotId, pipe_id typeId ,if( pipe_id >0,-1, -1) currentType from  hq_plot_pipe where del_flag=#{delFlag} and plot_id=#{plotId} ")
    List<HouseOrPipeContent> getHouseOrPipeContentByPlotId(Integer plotId,String delFlag);

    /**
     * 批量插入地块信息
     * @param plots
     */
    @Insert
            ({
                "<script>",
                    "insert into hq_plot ( plot_name, create_time, " ,
                    "update_time,update_user_name,del_flag)" ,
                    " values ",
                    "<foreach collection='plots' item='item' index='index' separator=','>",
                    "(" ,
                    " #{item.plotName}, #{item.createTime},#{item.updateTime},#{item.updateUserName},#{item.delFlag}" ,
                    ")",
                    "</foreach>",
                "</script>"
            })
    void batchInsertPlots(@Param("plots") List<HqPlot> plots);

}