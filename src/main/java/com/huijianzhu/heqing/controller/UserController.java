package com.huijianzhu.heqing.controller;

import com.huijianzhu.heqing.definition.UserAccpetDefinition;
import com.huijianzhu.heqing.service.UserService;
import com.huijianzhu.heqing.vo.SystemResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ================================================================
 * 说明：用户业务请求控制器
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  22:15            创建
 * =================================================================
 **/
@CrossOrigin //开启跨域请求
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页显示用户的数据
     * @param startPage 起始页数
     * @param row       每页显示的行数
     * @return
     */
    @PostMapping("/page/users")
    public SystemResult pageUsers(Integer startPage, Integer row){
        return userService.pageUsers(startPage,row);
    }

    /**
     * 添加用户
     * @param definition 接收添加用户属性信息实体
     * @return
     */
    @PostMapping("/add/user")
    public SystemResult addUser(UserAccpetDefinition definition)throws  Exception{
        return userService.addUser(definition);
    }

    /**
     * 添加用户
     * @param definition 接收修改用户属性信息实体
     * @return
     */
    @PostMapping("/update/user")
    public SystemResult updateUser(UserAccpetDefinition definition)throws  Exception{
        return userService.updateUser(definition);
    }

    /**
     * 删除用户
     * @param userId  对应要删除的用户信息id
     * @return
     */
    @PostMapping("/delete/user")
    public SystemResult deleteUser(Integer userId)throws  Exception{
        return userService.deleteUser(userId);
    }


}
    
    
    