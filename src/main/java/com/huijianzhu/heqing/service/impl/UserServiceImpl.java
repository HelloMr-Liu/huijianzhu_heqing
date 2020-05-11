package com.huijianzhu.heqing.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.cache.PermissionCacheManager;
import com.huijianzhu.heqing.cache.UserAccountCacheManager;
import com.huijianzhu.heqing.definition.UserAccpetDefinition;
import com.huijianzhu.heqing.entity.HqUser;
import com.huijianzhu.heqing.enums.GLOBAL_TABLE_FILED_STATE;
import com.huijianzhu.heqing.enums.LOGIN_STATE;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.enums.USER_TABLE_FIELD_STATE;
import com.huijianzhu.heqing.lock.UserLock;
import com.huijianzhu.heqing.mapper.extend.HqUserExtendMapper;
import com.huijianzhu.heqing.pojo.ModelTree;
import com.huijianzhu.heqing.pojo.UserLoginContent;
import com.huijianzhu.heqing.pojo.UserPermissions;
import com.huijianzhu.heqing.service.UserService;
import com.huijianzhu.heqing.utils.CookieUtils;
import com.huijianzhu.heqing.utils.MD5Utils;
import com.huijianzhu.heqing.utils.ShareCodeUtil;
import com.huijianzhu.heqing.vo.SystemResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ================================================================
 * 说明：用户业务操作接口实现
 * <p>
 * 作者          时间                    注释
 * 刘梓江	2020/5/5  15:27            创建
 * =================================================================
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    
    @Autowired
    private HttpServletRequest request;
    
    @Autowired
    private HqUserExtendMapper hqUserExtendMapper;          //注入操作用户信息表mapper接口
    @Autowired
    private LoginTokenCacheManager tokenCacheManager;       //注入登录标识管理缓存
    @Autowired
    private UserAccountCacheManager  accountCacheManager;   //注入用户账号管理缓存
    @Autowired
    private PermissionCacheManager permissionCacheManager;  //注入权限信息管理缓存

    /**
     * 分页显示用户的数据
     * @param startPage 起始页数
     * @param row       每页显示的行数
     * @return
     */
    public SystemResult pageUsers(Integer startPage,Integer row){
        //开启分页
        PageHelper.startPage(startPage, row);
        //查询所有用户信息
        List<HqUser> userList = hqUserExtendMapper.getUser(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        //将查询到用户信息封装到，分页对象中
        PageInfo<HqUser> userPage=new PageInfo<HqUser>(userList);
        return SystemResult.ok(userPage);
    }

    /**
     * 添加用户
     * @param definition 接收添加用户属性信息实体
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult addUser(UserAccpetDefinition definition)throws  Exception{

        //开启用户修改锁操作,防止用户账号冲突
        UserLock.USER_UPDATE_LOCK.writeLock().lock();
        try{
            //获取一个新的账号
            String newUserAccount="";
            while(true){
                String newAccount = ShareCodeUtil.getStringRandom(6);
                if(!accountCacheManager.checkAccountexist(newAccount,null)){
                    //将当前新的有效账号赋值给newUserAccount便于用户添加使用
                    newUserAccount=newAccount;
                    break;
                };
            }
            //获取出当前用户登录标识获取对应的用户信息
            String loginToken = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent currentLoginUser = tokenCacheManager.getCacheUserByLoginToken(loginToken);

            //创建用户实体对象
            HqUser hqUser=new HqUser();
            hqUser.setUserName(definition.getUserName()); //添加对应的用户名
            hqUser.setUserAccount(newUserAccount);        //随机一个账号
            hqUser.setPassWord(MD5Utils.md5(definition.getPassword()+"heqing")); //对用户密码进行加密操作
            hqUser.setPhoneNumber(definition.getPhoneNumber()); //电话号码
            hqUser.setEmail(definition.getEmail());//邮箱
            hqUser.setUserType(definition.getUserType()); //用户类型
            hqUser.setPermissionsId(definition.getPermissionsId()); //模块id
            hqUser.setUpdateTime(new Date());   //修改时间
            hqUser.setUpdateUserName(currentLoginUser.getUserName()); //记录那个用户操作了该用户信息
            hqUser.setCreateTime(new Date());
            hqUser.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);//默认是有有效用户

            //将当前用户信息持久化到数据库中
            hqUserExtendMapper.insertSelective(hqUser);
            //同步账号缓存管理容器
            accountCacheManager.refresh();
            //日志记录
            log.info("用户id:"+currentLoginUser.getUserId()+"==操作了一条添加用户数据库");
            return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY,SYSTEM_RESULT_STATE.SUCCESS.VALUE);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            //解锁操作
            UserLock.USER_UPDATE_LOCK.writeLock().unlock();
        }
    }

    /**
     * 获取指定用户id对应的用户信息
     * @param userId
     * @return
     */
    public SystemResult getUserById(Integer userId){
        return SystemResult.ok( hqUserExtendMapper.selectByPrimaryKey(userId));
    }


    /**
     * 添加用户
     * @param definition 接收修改用户属性信息实体
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult updateUser(UserAccpetDefinition definition)throws  Exception{
        //开启用户修改锁操作,防止修改成同账号信息用户
        UserLock.USER_UPDATE_LOCK.writeLock().lock();
        try{
            //判断当前修改用户信息对应的用户id在账号缓存中是否存在,一般不存在说明对应的用户已经被删除了
            if(!accountCacheManager.checkAccountByUserId(definition.getUserId().toString())){
                return SystemResult.build(SYSTEM_RESULT_STATE.USER_PERMISSION_ERROR.KEY,SYSTEM_RESULT_STATE.USER_PERMISSION_ERROR.VALUE);
            }

            //判断新修改的账号是否存在
            if(accountCacheManager.checkAccountexist(definition.getUserAccount(),definition.getUserId().toString())){
                //账号存在不能进行修改
                return SystemResult.build(SYSTEM_RESULT_STATE.USER_ACCOUNT_EXITE.KEY,SYSTEM_RESULT_STATE.USER_ACCOUNT_EXITE.VALUE);
            }

            //获取出当前用户登录标识获取对应的用户信息
            String loginToken = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent currentLoginUser = tokenCacheManager.getCacheUserByLoginToken(loginToken);


            //创建一个修改用户信息封装对象
            HqUser  currrentUser=new HqUser();
            currrentUser.setUserId(definition.getUserId());             //修改用户id
            currrentUser.setUserName(definition.getUserName());         //修改用户名
            currrentUser.setPassWord(MD5Utils.md5(definition.getPassword()+"heqing")); //对用户密码进行加密操作
            currrentUser.setUserAccount(definition.getUserAccount());   //修改用户账号
            currrentUser.setEmail(definition.getEmail());               //修改邮件
            currrentUser.setPhoneNumber(definition.getPhoneNumber());   //修改电话号码
            currrentUser.setUserType(definition.getUserType());         //修改用户类型
            if(!StrUtil.hasBlank(definition.getPermissionsId())){
                currrentUser.setPermissionsId(definition.getPermissionsId()); //修改用户权限
            }
            currrentUser.setUpdateTime(new Date());                             //修改操作时间
            currrentUser.setUpdateUserName(currentLoginUser.getUserName());     //记录那个用户修改了该用户信息

            //将当前用户时间持久化到数据库中
            hqUserExtendMapper.updateByPrimaryKeySelective(currrentUser);
            //同步账号缓存管理容器
            accountCacheManager.refresh();
            //同步修改用户id对应在登录容器对应的用户信息
            tokenCacheManager.refreshloginUserInfo(definition.getUserId());


            //日志记录
            log.info("用户id:"+currentLoginUser.getUserId()+"==操作了一条修改用户数据");
            //修改用户成功
            return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY,SYSTEM_RESULT_STATE.SUCCESS.VALUE);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            //解锁操作
            UserLock.USER_UPDATE_LOCK.writeLock().unlock();
        }
    }

    /**
     * 删除用户
     * @param userId  对应要删除的用户信息id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult deleteUser(Integer userId)throws  Exception{
        //开启用户修改锁操作,防止用户账号冲突
        UserLock.USER_UPDATE_LOCK.writeLock().lock();
        try{
            //获取出当前用户登录标识获取对应的用户信息
            String loginToken = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent currentLoginUser = tokenCacheManager.getCacheUserByLoginToken(loginToken);

            //创建一个封装删除用户对象
            HqUser hqUser=new HqUser();
            hqUser.setUserId(userId);
            hqUser.setDelFlag(GLOBAL_TABLE_FILED_STATE.DEL_FLAG_YES.KEY);//标志当前用户已经失效
            hqUser.setUpdateTime(new Date()); //修改时间
            hqUser.setUpdateUserName(currentLoginUser.getUserName());//谁操作了该账号
            hqUser.setUserType(Integer.parseInt(USER_TABLE_FIELD_STATE.USER_TYPE_USER.KEY));//将用户类型降级为普通用户
            hqUser.setPermissionsId("");//对权限清除

            //并持久化到数据库中
            hqUserExtendMapper.updateByPrimaryKeySelective(hqUser);
            //同步账号缓存管理容器
            accountCacheManager.refresh();
            //同步修改用户id对应在登录容器对应的用户信息
            tokenCacheManager.refreshloginUserInfo(userId);
            //日志记录
            log.info("用户id:"+currentLoginUser.getUserId()+"==操作了一条删除用户数据");
            //删除用户成功
            return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY,SYSTEM_RESULT_STATE.SUCCESS.VALUE);
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            //解锁操作
            UserLock.USER_UPDATE_LOCK.writeLock().unlock();
        }
    }

    /**
     * 获取用户权限信息
     * @param userId  对应获取该id用户对应的权限信息
     * @return
     */
    public SystemResult userJurisdiction(Integer userId){
        //获取当前用户信息
        HqUser userBy = hqUserExtendMapper.getUserById(userId, GLOBAL_TABLE_FILED_STATE.DEL_FLAG_NO.KEY);
        if(userBy==null){
            //不是一个有效用户信息,所以获取不了对应的用户权限信息
            return SystemResult.build(SYSTEM_RESULT_STATE.USER_PERMISSION_ERROR.KEY,SYSTEM_RESULT_STATE.USER_PERMISSION_ERROR.VALUE);
        }

        //创建集合存储用户对应拥有的modelid信息
        List<String> modelIdList=new ArrayList<>();
        Arrays.stream(userBy.getPermissionsId().trim().split(",")).forEach(
            e->{
                if(!StrUtil.hasBlank(e)){
                    modelIdList.add(e);
                }
            }
        );
        //获取所有模块并转换成树信息集合
        List<ModelTree> modelTree = permissionCacheManager.getModelTree();

        //创建一个用户权限信息实体
        UserPermissions permissions=new UserPermissions();
        permissions.setTreeList(modelTree);
        permissions.setUserModelIds(modelIdList);
        return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY,permissions);
    }

    /**
     * 修改对应用户的权限信息
     * @param definition 接收修改用户属性信息实体
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult updateJurisdiction(UserAccpetDefinition definition){
        //开启用户修改锁操作,防止用户账号冲突
        UserLock.USER_UPDATE_LOCK.writeLock().lock();
        try{
            //判断当前修改用户信息对应的用户id在账号缓存中是否存在,一般不存在说明对应的用户已经被删除了
            if(!accountCacheManager.checkAccountByUserId(definition.getUserId().toString())){
                return SystemResult.build(SYSTEM_RESULT_STATE.USER_PERMISSION_ERROR.KEY,SYSTEM_RESULT_STATE.USER_PERMISSION_ERROR.VALUE);
            }

            //获取出当前用户登录标识获取对应的用户信息
            String loginToken = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent currentLoginUser = tokenCacheManager.getCacheUserByLoginToken(loginToken);

            //创建一个封装删除用户对象
            HqUser hqUser=new HqUser();
            hqUser.setUserId(definition.getUserId());
            hqUser.setPermissionsId(definition.getPermissionsId());
            hqUser.setUpdateTime(new Date());                             //修改操作时间
            hqUser.setUpdateUserName(currentLoginUser.getUserName());     //记录那个用户修改了该用户信息
            //并持久化到数据库中
            hqUserExtendMapper.updateByPrimaryKeySelective(hqUser);
            //同步账号缓存管理容器
            accountCacheManager.refresh();
            //同步修改用户id对应在登录容器对应的用户信息
            tokenCacheManager.refreshloginUserInfo(definition.getUserId());
            //日志记录
            log.info("用户id:"+currentLoginUser.getUserId()+"==操作了一条修改用户数据");
            //修改用户权限成功
            return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY,SYSTEM_RESULT_STATE.SUCCESS.VALUE);


        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            //解锁操作
            UserLock.USER_UPDATE_LOCK.writeLock().unlock();
        }

    }
}
    
    
    