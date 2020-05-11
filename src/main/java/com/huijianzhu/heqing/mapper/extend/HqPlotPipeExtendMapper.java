package com.huijianzhu.heqing.mapper.extend;

import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.entity.HqPlotPipe;
import com.huijianzhu.heqing.mapper.HqPlotPipeMapper;
import com.huijianzhu.heqing.pojo.PlotHouseDTO;
import com.huijianzhu.heqing.pojo.PlotPipeDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ================================================================
 * 说明：管道搬迁拓展mapper接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/10  20:56            创建
 * =================================================================
 **/
public interface HqPlotPipeExtendMapper extends HqPlotPipeMapper {

    /**
     * 查询与指定管道搬迁的对应的搬迁信息(默认查询全部)
     * @param pipeName  管道搬迁名称
     * @param delFalg   删除标志
     * @return
     */
    @Select({
            "<script>",
            "SELECT hp.plot_id plotId,hp.plot_name plotName,hp.create_time plotCreateTime, hpp.* " ,
            "FROM hq_plot_pipe hpp left join hq_plot hp " ,
            "on hpp.plot_id=hp.plot_id " ,
            "where hpp.del_flag=#{delFalg} " ,
            " <if test='pipeName!=null'>" ,
            " and  pipe_name like concat('%',#{pipeName},'%') " ,
            " </if>" ,
            "order by hp.create_time,hpp.pipe_name desc" ,
            "</script>"})
    List<PlotPipeDTO> getPlotPipeByName(String pipeName, String delFalg);


    /**
     * 判断当前是否有对应的相同的管道搬迁信息
     * @param pipeName   管道名称
     * @param delFlag   删除标志
     * @param pipeId   指定管道id
     * @return
     */
    @Select({
            "<script>",
            " select * from hq_plot_pipe where del_flag=#{delFlag} and pipe_name=#{pipeName} ",
            " <if test='pipeId!=null'>",
            " and pipe_id!=#{pipeId} ",
            "</if>",
            "</script>"})
    HqPlotPipe getPipeByName(String pipeName, String delFlag, Integer pipeId);


}
