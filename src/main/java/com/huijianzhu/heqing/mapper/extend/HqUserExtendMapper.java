package com.huijianzhu.heqing.mapper.extend;

import com.huijianzhu.heqing.entity.HqUser;
import com.huijianzhu.heqing.mapper.HqUserMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ================================================================
 * 说明：用户信息操作扩展mapper
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  19:42            创建
 * =================================================================
 **/
public interface HqUserExtendMapper extends HqUserMapper {


    /**
     * 获取对应的所有有效用户信息
     * @param queryContent  查询内容
     * @param delFlag       删除标志
     * @return
     */
    @Select({"<script>",
            " select * from hq_user where del_flag=#{delFlag} " ,
            " <if test='queryContent!=null'>",
                " and  user_name like concat('%',#{queryContent},'%') or " ,
                " user_account like concat('%',#{queryContent},'%') ",
            " </if>",
            " order by create_time desc ",
            "</script>"})
    List<HqUser> getUser(String queryContent,String delFlag);

    /**
     * 获取有效用户账号,用户名信息
     * @param delFlag       删除标志
     * @return
     */
    @Select("select user_account,user_id from hq_user where del_flag=#{delFlag} ")
    List<HqUser> getValidAccountAndUserId(String delFlag);


    /**
     * 获取对应的用户信息
     * @param userId    用户id
     * @param delFlag   删除标志
     * @return
     */
    @Select(" select * from hq_user where user_id=#{userId} and del_flag=#{delFlag} ")
    HqUser getUserById(Integer userId,String delFlag);

    /**
     * 获取指定账号或密码的用户并且是有效用户
     * @param account       账号
     * @param password      密码
     * @param delFlag       删除标志
     * @return
     */
    @Select(" select *  from hq_user where user_account=#{account} and pass_word=#{password} and  del_flag=#{delFlag} ")
    HqUser getUserByAccountAndPassword(String account,
                                       String password,
                                       String delFlag);
}
