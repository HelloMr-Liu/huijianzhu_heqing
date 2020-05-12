package com.huijianzhu.heqing.mapper.extend;

import com.huijianzhu.heqing.entity.HqNoLiveAccount;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.mapper.HqNoLiveAccountMapper;
import com.huijianzhu.heqing.pojo.AccpetPlotTypePropertyValue;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * ================================================================
 * 说明：操作非居住台账信息数据mapper数据扩展接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/12  16:38            创建
 * =================================================================
 **/
public interface HqNoLiveAccountExtendMapper  extends HqNoLiveAccountMapper {


    /**
     * 获取所有地块信息对应的非居住台账列表
     * @param delFlag
     * @return
     */
    @Select("select la.* from \n" +
            "hq_plot hp left join  \n" +
            "hq_no_live_account la on hp.plot_id=la.plot_id \n" +
            "where hp.del_flag=#{delFlag} and la.plot_name IS NOT NULL   " +
            "order by la.pay_scale desc "
            )
    List<HqNoLiveAccount> findAll(String delFlag);



    /**
     * 批量插入信息
     * @param accounts
     */
    @Insert
            ({
                "<script>",
                "insert into hq_no_live_account ( plot_id, plot_name, " ,
                "total_deal_money,ok_money,pay_scale,update_time,update_user_name)" ,
                " values ",
                "<foreach collection='accounts' item='item' index='index' separator=','>",
                "(" ,
                " #{item.plotId}, #{item.plotName},#{item.totalDealMoney},#{item.okMoney},#{item.payScale},#{item.updateTime},#{item.updateUserName}" ,
                ")",
                "</foreach>",
                "</script>"
            })
    void batchAdd(@Param("accounts") List<HqNoLiveAccount> accounts);



    /**
     * 批量修改
     * @param accounts
     */
    @Update
        ({
            "<script> " ,
                " UPDATE  hq_no_live_account",
                " <trim prefix ='set' prefixOverrides=',' > " ,

                "<trim prefix ='total_deal_money = case' suffix='end,'>",
                    "<foreach collection ='accounts' item ='item' index = 'index'> ",
                        "when no_live_id = #{item.noLiveId} then #{item.totalDealMoney} "  ,
                    "</foreach>" ,
                "</trim> " ,

                "<trim prefix ='ok_money = case' suffix='end,'>",
                    "<foreach collection ='accounts' item ='item' index = 'index'> ",
                        "when no_live_id = #{item.noLiveId} then #{item.okMoney} "  ,
                    "</foreach>" ,
                "</trim> " ,

                "<trim prefix ='pay_scale = case' suffix='end,'>",
                    "<foreach collection ='accounts' item ='item' index = 'index'> ",
                        "when no_live_id = #{item.noLiveId} then #{item.payScale} "  ,
                    "</foreach>" ,
                "</trim> " ,


                "<trim prefix ='update_time = case' suffix='end,'>",
                    "<foreach collection ='accounts' item ='item' index = 'index'> ",
                        "when no_live_id = #{item.noLiveId} then #{item.updateTime} "  ,
                    "</foreach>" ,
                "</trim> " ,

                "<trim prefix ='update_user_name = case' suffix='end'>",
                    "<foreach collection ='accounts' item ='item' index = 'index'> ",
                        "when no_live_id = #{item.noLiveId} then #{item.updateUserName} "  ,
                    "</foreach>" ,
                "</trim> " ,

                " </trim> ",
                " WHERE no_live_id in " ,
                " (" ,
                "<foreach collection='accounts' item='item' index='index' separator=','>",
                    " #{item.noLiveId}",
                "</foreach>",
                " )" ,
            "</script>"
        })
    void batchUpdate(@Param("accounts") List<HqNoLiveAccount> accounts);




}