package com.huijianzhu.heqing.mapper.extend;

import com.huijianzhu.heqing.entity.HqPipeAccount;
import com.huijianzhu.heqing.mapper.HqPipeAccountMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * ================================================================
 * 说明：操作管道搬迁费用信息mapper扩展接口
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/13  11:41            创建
 * =================================================================
 **/
public interface HqPipeAccountExtendMapper extends HqPipeAccountMapper {


    /**
     * 查询所有有效管道搬迁对应的信息集
     *
     * @param delFalg 删除标志
     * @return
     */
    @Select("select hpa.* from hq_plot hp  " +
            "left join  hq_pipe_account hpa on  hp.plot_id=hpa.plot_id " +
            "where hp.del_flag=#{delFalg} and hpa.plot_name is not null " +
            "order by hpa.telecom_budget_amount,hpa.electricity_budget_amount,hpa.gas_budget_amount desc")
    List<HqPipeAccount> findAll(String delFalg);


    /**
     * 批量插入操作
     */
    @Insert
            ({
                    "<script>",
                    "insert into hq_pipe_account ( telecom_budget_amount, electricity_budget_amount, ",
                    "gas_budget_amount,water_budget_amount,telecom_audit_amount,electricity_audit_amount,",
                    "gas_audit_amount,water_audit_amount,plot_id,plot_name,",
                    "update_time,update_user_name)",

                    " values ",
                    "<foreach collection='pipes' item='item' index='index' separator=','>",
                    "(",
                    " #{item.telecomBudgetAmount}, #{item.electricityBudgetAmount},#{item.gasBudgetAmount},#{item.waterBudgetAmount},",
                    " #{item.telecomAuditAmount}, #{item.electricityAuditAmount},#{item.gasAuditAmount},#{item.waterAuditAmount},",
                    " #{item.plotId},#{item.plotName},#{item.updateTime},#{item.updateUserName}",
                    ")",
                    "</foreach>",
                    "</script>"
            })
    void batchAdd(@Param("pipes") List<HqPipeAccount> pipes);


    /**
     * 批量修改操作
     *
     * @param pipes
     */
    @Update
            ({
                    "<script> ",
                    " UPDATE  hq_pipe_account",
                    " <trim prefix ='set' prefixOverrides=',' > ",

                    "<trim prefix ='telecom_budget_amount = case' suffix='end,'>",
                    "<foreach collection ='pipes' item ='item' index = 'index'> ",
                    "when pipe_account_id = #{item.pipeAccountId} then #{item.telecomBudgetAmount} ",
                    "</foreach>",
                    "</trim> ",


                    "<trim prefix ='electricity_budget_amount = case' suffix='end,'>",
                    "<foreach collection ='pipes' item ='item' index = 'index'> ",
                    "when pipe_account_id = #{item.pipeAccountId} then #{item.electricityBudgetAmount} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='gas_budget_amount = case' suffix='end,'>",
                    "<foreach collection ='pipes' item ='item' index = 'index'> ",
                    "when pipe_account_id = #{item.pipeAccountId} then #{item.gasBudgetAmount} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='water_budget_amount = case' suffix='end,'>",
                    "<foreach collection ='pipes' item ='item' index = 'index'> ",
                    "when pipe_account_id = #{item.pipeAccountId} then #{item.waterBudgetAmount} ",
                    "</foreach>",
                    "</trim> ",


                    "<trim prefix ='telecom_audit_amount = case' suffix='end,'>",
                    "<foreach collection ='pipes' item ='item' index = 'index'> ",
                    "when pipe_account_id = #{item.pipeAccountId} then #{item.telecomAuditAmount} ",
                    "</foreach>",
                    "</trim> ",


                    "<trim prefix ='electricity_audit_amount = case' suffix='end,'>",
                    "<foreach collection ='pipes' item ='item' index = 'index'> ",
                    "when pipe_account_id = #{item.pipeAccountId} then #{item.electricityAuditAmount} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='gas_audit_amount = case' suffix='end,'>",
                    "<foreach collection ='pipes' item ='item' index = 'index'> ",
                    "when pipe_account_id = #{item.pipeAccountId} then #{item.gasAuditAmount} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='water_audit_amount = case' suffix='end,'>",
                    "<foreach collection ='pipes' item ='item' index = 'index'> ",
                    "when pipe_account_id = #{item.pipeAccountId} then #{item.waterAuditAmount} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='plot_id = case' suffix='end,'>",
                    "<foreach collection ='pipes' item ='item' index = 'index'> ",
                    "when pipe_account_id = #{item.pipeAccountId} then #{item.plotId} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='plot_name = case' suffix='end,'>",
                    "<foreach collection ='pipes' item ='item' index = 'index'> ",
                    "when pipe_account_id = #{item.pipeAccountId} then #{item.plotName} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='update_time = case' suffix='end,'>",
                    "<foreach collection ='pipes' item ='item' index = 'index'> ",
                    "when pipe_account_id = #{item.pipeAccountId} then #{item.updateTime} ",
                    "</foreach>",
                    "</trim> ",

                    "<trim prefix ='update_user_name = case' suffix='end'>",
                    "<foreach collection ='pipes' item ='item' index = 'index'> ",
                    "when pipe_account_id = #{item.pipeAccountId} then #{item.updateUserName} ",
                    "</foreach>",
                    "</trim> ",

                    " </trim> ",
                    " WHERE pipe_account_id in ",
                    " (",
                    "<foreach collection='pipes' item='item' index='index' separator=','>",
                    " #{item.pipeAccountId}",
                    "</foreach>",
                    " )",
                    "</script>"
            })
    void batchUpdate(@Param("pipes") List<HqPipeAccount> pipes);
}
