package com.huijianzhu.heqing.service.impl;

import cn.hutool.core.util.StrUtil;
import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.cache.WriteWayCacheManager;
import com.huijianzhu.heqing.definition.PropertyAccpetDefinition;
import com.huijianzhu.heqing.entity.HqProperty;
import com.huijianzhu.heqing.entity.HqWriteWay;
import com.huijianzhu.heqing.enums.LOGIN_STATE;
import com.huijianzhu.heqing.enums.PROPERTY_TABLE_FIELD_STATE;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.lock.PropertyLock;
import com.huijianzhu.heqing.mapper.extend.HqPropertyExtendMapper;
import com.huijianzhu.heqing.pojo.PropertyTree;
import com.huijianzhu.heqing.pojo.PropertyUpdateContent;
import com.huijianzhu.heqing.pojo.UserLoginContent;
import com.huijianzhu.heqing.service.PropertyService;
import com.huijianzhu.heqing.utils.CookieUtils;
import com.huijianzhu.heqing.vo.SystemResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.DefaultEditorKit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ================================================================
 * 说明：操作配置属性信息业务接口实现
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/7  14:06            创建
 * =================================================================
 **/
@Service
@Slf4j
public class PropertyServiceImpl implements PropertyService {
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginTokenCacheManager tokenCacheManager;       //注入登录标识管理缓存

    @Autowired
    private WriteWayCacheManager writeWayCacheManager;      //注入填写方式管理缓存

    @Autowired
    private HqPropertyExtendMapper hqPropertyExtendMapper;  //注入操作属性表mapper接口


    /**
     * 获取指定属性名称下的属性信息,如果不传递属性名称默认是查询所有
     * @param propertyName  属性名称
     * @param propertyType  属性类型
     * @return
     */
    public SystemResult getPropertiesByName(String propertyName,String propertyType){
        //对属性名进行一个校验判断
        propertyName= StrUtil.hasBlank(propertyName)?null:propertyName;


        //创建一个map存储各个父属性下对应的子属性信息
        TreeMap<Integer, PropertyTree> treeMap=new TreeMap<>();

        List<PropertyTree> propertysList = hqPropertyExtendMapper.getValidPropertys(propertyName, PROPERTY_TABLE_FIELD_STATE.DEL_FLAG_NO.KEY,propertyType);
        //遍历当前propertysList将各个子属性分组在父属性下
        propertysList.forEach(
            e->{
                if(e.getIsParent().equals(PROPERTY_TABLE_FIELD_STATE.IS_PARENT.KEY)){
                    PropertyTree parentPropertyTree = treeMap.get(e.getPropertyId());
                    if(parentPropertyTree!=null){
                        //子属性帮忙创建了父属性的最基础信息对象，并将父属性下对应的子属性集添加到当前真正父属性下
                        e.setChildren(parentPropertyTree.getChildren());
                    }else{
                        //创建一个子信息集合
                        e.setChildren(new ArrayList<>());
                    }
                    treeMap.put(e.getPropertyId(),e);
                }else{
                    PropertyTree parentPropertyTree = treeMap.get(e.getParentId());

                    if(parentPropertyTree==null){
                        //当前子属性对应的父属性没有所以子属性帮忙创建一个最基础的父属性信息
                        parentPropertyTree=new PropertyTree();
                        //创建一个存储子属性信息集合
                        ArrayList<PropertyTree> childrenList=new ArrayList<>();
                        childrenList.add(e);
                        parentPropertyTree.setChildren(childrenList);//存储到最基础的父属性上

                        //将当前最基础的父属性存储到treeMap上
                        treeMap.put(e.getParentId(),parentPropertyTree);
                    }else{

                        //将当前子属性信息存储到父属性下对应的子属性集合上
                        parentPropertyTree.getChildren().add(e);
                    }
                }
            }
        );
        //获取完后对treeMap下的对应的子属性排序
        treeMap.entrySet().stream().forEach(
            e->{
                //获取当前父属性信息
                PropertyTree parentTree = e.getValue();

                //对子属性信息排序
                List<PropertyTree> collect = parentTree.getChildren().stream().sorted(
                    (e1, e2) -> { return Integer.parseInt(e1.getProSort()) - Integer.parseInt(e2.getProSort());}
                ).collect(Collectors.toList());
                //将排序好的子属性信息集重新赋值给父属性下
                parentTree.setChildren(collect);
            }
        );
        return SystemResult.ok(treeMap.entrySet().stream().map(e->e.getValue()).collect(Collectors.toList()));
    }


    /**
     * 获取填写方式信息集合
     * @return
     */
    public SystemResult getWriteWayList(){
        return SystemResult.ok(writeWayCacheManager.getWriteWayList());
    }


    /**
     * 获取指定属性id对应属性
     * @param propertyId
     * @return
     */
    public SystemResult getPropertyById(Integer propertyId){
        //开启属性操作信息原子锁防止显示的数据是一个无效数据
        PropertyLock.PROPERTY_UPDATE_LOCK.writeLock().lock();
        try{
            //获取指定id对应的属性信息
            PropertyUpdateContent updateContent = hqPropertyExtendMapper.getPropertyById(PROPERTY_TABLE_FIELD_STATE.DEL_FLAG_NO.KEY, propertyId);
            if(updateContent==null){
                //有可能当前显示的属性修改信息以及被删除了
                return SystemResult.build(SYSTEM_RESULT_STATE.PROPERTY_NOT_EXITE.KEY,SYSTEM_RESULT_STATE.PROPERTY_NOT_EXITE.VALUE);
            }
            //获取填写方式列表信息
            List<HqWriteWay> writeWayList = writeWayCacheManager.getWriteWayList();
            updateContent.setWriteWays(writeWayList);
            return SystemResult.build(SYSTEM_RESULT_STATE.SUCCESS.KEY,updateContent);
        }catch(Exception e) {
            throw e;
        }finally {
            //解锁操作
            PropertyLock.PROPERTY_UPDATE_LOCK.writeLock().unlock();
        }

    }

    /**
     * 添加属性信息
     * @param definition    封装添加属性对应的信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult addProperty(PropertyAccpetDefinition definition)throws  Exception{
        //开启属性操作信息原子锁防止重复添加
        PropertyLock.PROPERTY_UPDATE_LOCK.writeLock().lock();
        try{
            //判断新添加对应的属性名是否已经存在
            HqProperty propertyContent = hqPropertyExtendMapper.getPropertyContent(definition.getPropertyName(), PROPERTY_TABLE_FIELD_STATE.DEL_FLAG_NO.KEY, null);
            if(propertyContent!=null){
                //新添加的属性名已经存在请输入其他属性名
                return SystemResult.build(SYSTEM_RESULT_STATE.PROPERTY_NAME_EXITE.KEY,SYSTEM_RESULT_STATE.PROPERTY_NAME_EXITE.VALUE);
            }
            //获取当前登录的用户信息
            //获取出当前用户登录标识获取对应的用户信息
            String loginToken = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent currentLoginUser = tokenCacheManager.getCacheUserByLoginToken(loginToken);

            //创建封装新属性信息
            HqProperty newProperty=new HqProperty();
            newProperty.setPropertyName(definition.getPropertyName()); //属性名称
            newProperty.setProSort(definition.getProSort());           //刷新排序
            newProperty.setShowCondition(definition.getShowCondition());//显示条件
            if(definition.getIsParent().equals(PROPERTY_TABLE_FIELD_STATE.IS_PARENT.KEY)){
                //当前添加的是父属性
                newProperty.setShowWay(definition.getShowWay());//显示方式
                newProperty.setIsParent(PROPERTY_TABLE_FIELD_STATE.IS_PARENT.KEY);//代表的是父属性信息
            }else{
                //当前添加的是子属性信息
                newProperty.setWriteId(definition.getWriteId());        //填写id
                newProperty.setWriteName(definition.getWriteName());    //填写名称
                newProperty.setUnitContent(definition.getUnitContent());//单位内容信息
                newProperty.setIsParent(PROPERTY_TABLE_FIELD_STATE.NO_PARENT.KEY);//代表的是子属性信息
                newProperty.setParentId(definition.getParentId());      //父id信息
            }
            newProperty.setCreateTime(new Date()); //创建时间
            newProperty.setUpdateTime(new Date()); //修改时间
            newProperty.setDelFlag(PROPERTY_TABLE_FIELD_STATE.DEL_FLAG_NO.KEY); //默认是一个有效数据
            newProperty.setUpdateUserName(currentLoginUser.getUserName()); //谁操作了该属性添加
            newProperty.setPropertyType(definition.getPropertyType()); //属性类型

            //持久化到数据库中
            hqPropertyExtendMapper.insertSelective(newProperty);
            return SystemResult.ok("属性添加成功");
        }catch(Exception e) {
            throw e;
        }finally {
            //解锁操作
            PropertyLock.PROPERTY_UPDATE_LOCK.writeLock().unlock();
        }
    }


    /**
     * 修改属性信息
     * @param definition  封装修改属性对应的信息
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult updateProperty(PropertyAccpetDefinition definition)throws  Exception{
        //开启属性操作信息原子锁防止修改重复的名称
        PropertyLock.PROPERTY_UPDATE_LOCK.writeLock().lock();
        try{
            //判断修改对应的属性名是否已经存在
            HqProperty propertyContent = hqPropertyExtendMapper.getPropertyContent(definition.getPropertyName(), PROPERTY_TABLE_FIELD_STATE.DEL_FLAG_NO.KEY, definition.getPropertyId());
            if(propertyContent!=null){
                //修改的属性名已经存在请输入其他属性名
                return SystemResult.build(SYSTEM_RESULT_STATE.PROPERTY_NAME_EXITE.KEY,SYSTEM_RESULT_STATE.PROPERTY_NAME_EXITE.VALUE);
            }
            //获取当前登录的用户信息
            //获取出当前用户登录标识获取对应的用户信息
            String loginToken = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent currentLoginUser = tokenCacheManager.getCacheUserByLoginToken(loginToken);

            //创建封装新属性信息
            HqProperty newProperty=new HqProperty();
            newProperty.setPropertyId(definition.getPropertyId());     //属性id
            newProperty.setPropertyName(definition.getPropertyName()); //属性名称
            newProperty.setProSort(definition.getProSort());           //刷新排序
            newProperty.setShowCondition(definition.getShowCondition());//显示条件
            if(definition.getIsParent().equals(PROPERTY_TABLE_FIELD_STATE.IS_PARENT.KEY)){
                //当前修改的是父属性
                newProperty.setShowWay(definition.getShowWay());//显示方式
                newProperty.setIsParent(PROPERTY_TABLE_FIELD_STATE.IS_PARENT.KEY);//代表的是父属性信息
            }else{
                //当前添加的是子属性信息
                newProperty.setWriteId(definition.getWriteId());        //填写id
                newProperty.setWriteName(definition.getWriteName());    //填写名称
                newProperty.setUnitContent(definition.getUnitContent());//单位内容信息
                newProperty.setIsParent(PROPERTY_TABLE_FIELD_STATE.NO_PARENT.KEY);//代表的是子属性信息
            }
            newProperty.setUpdateTime(new Date()); //修改时间
            newProperty.setUpdateUserName(currentLoginUser.getUserName()); //谁操作了该属性添加
            newProperty.setPropertyType(definition.getPropertyType()); //属性类型

            //持久化到数据库中
            hqPropertyExtendMapper.updateByPrimaryKeySelective(newProperty);
            return SystemResult.ok("属性修改成功");
        }catch(Exception e) {
                throw e;
        }finally {
            //解锁操作
            PropertyLock.PROPERTY_UPDATE_LOCK.writeLock().unlock();
        }
    }


    /**
     * 删除属性信息
     * @param propertyId   属性id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemResult deleteProperty(Integer propertyId)throws  Exception{

        //开启属性操作信息原子锁,这样删除该标志的时候其他任务不能修改被删除的属性信息
        PropertyLock.PROPERTY_UPDATE_LOCK.writeLock().lock();
        try{

            //获取该节点信息
            HqProperty hqProperty = hqPropertyExtendMapper.selectByPrimaryKey(propertyId);
            if(hqProperty.getIsParent().equals(PROPERTY_TABLE_FIELD_STATE.IS_PARENT.KEY)){
                //代表当前节点是父节点信息
                List<HqProperty> hqProperties = hqPropertyExtendMapper.childrenPropertiesExist(PROPERTY_TABLE_FIELD_STATE.DEL_FLAG_NO.KEY, propertyId);
                if(hqProperties.size()>0){
                    //由于当前父节点下还有子节点信息所以不能直接删除父节点信息
                    return SystemResult.build(SYSTEM_RESULT_STATE.CHILDREN_PROPERTY_EXITE.KEY,SYSTEM_RESULT_STATE.CHILDREN_PROPERTY_EXITE.VALUE);
                }
            }

            //获取当前登录的用户信息
            //获取出当前用户登录标识获取对应的用户信息
            String loginToken = CookieUtils.getCookieValue(request, LOGIN_STATE.USER_LOGIN_TOKEN.toString());
            UserLoginContent currentLoginUser = tokenCacheManager.getCacheUserByLoginToken(loginToken);


            //创建封装新属性信息
            HqProperty newProperty=new HqProperty();
            newProperty.setPropertyId(propertyId);     //属性id
            newProperty.setUpdateTime(new Date());     //修改时间
            newProperty.setUpdateUserName(currentLoginUser.getUserName()); //谁修改了该用户信息
            newProperty.setDelFlag(PROPERTY_TABLE_FIELD_STATE.DEL_FLAG_YES.KEY); //代表已经删除了

            //持久化到数据库中
            hqPropertyExtendMapper.updateByPrimaryKeySelective(newProperty);
            return SystemResult.ok("属性删除成功");
        }catch(Exception e) {
            throw e;
        }finally {
            //解锁操作
            PropertyLock.PROPERTY_UPDATE_LOCK.writeLock().unlock();
        }
    }
}
