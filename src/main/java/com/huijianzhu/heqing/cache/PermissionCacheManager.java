package com.huijianzhu.heqing.cache;

import com.github.pagehelper.PageInfo;
import com.huijianzhu.heqing.entity.HqPermissions;
import com.huijianzhu.heqing.mapper.HqPermissionsMapper;
import com.huijianzhu.heqing.mapper.extend.HqPermissionsExtendMapper;
import com.huijianzhu.heqing.pojo.ModelTree;
import com.mysql.cj.exceptions.ClosedOnExpiredPasswordException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ================================================================
 * 说明：权限缓存管理
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/6  11:44            创建
 * =================================================================
 **/
@Component
@Data
public class PermissionCacheManager {

    @Autowired
    private HqPermissionsExtendMapper hqPermissionsExtendMapper;   //注入操作权限信息表mapper接口

    //创建存储权限请求缓存容器 key：模块请求   value:模块id
    private ConcurrentHashMap<String, String> permissionRequestCache=new ConcurrentHashMap<>();

    //创建一个集合存储所有模块信息
    private List<HqPermissions>  permissionsList=new ArrayList<>();


    @PostConstruct
    public void init(){
        //初始化容器信息
        refreshCache();
    }


    /**
     * 刷新模块信息容器
     */
    public void refreshCache(){
        getPermissions();
    }

    /**
     * 获取对应的权限信息
     */
    public void getPermissions(){
        //获取所有权限信息,存储在permissionsList中
        permissionsList= hqPermissionsExtendMapper.getPermissions();
        permissionsList.forEach(
            e->{
                if(e.getIsParent()!=1) {
                    //将所有子模块信息对应的请求路径信息存储到permissionRequestCache容器中
                    permissionRequestCache.put(e.getModelPath().trim(),e.getModelId());
                }
            }
        );
    }

    /**
     * 将所有的模块信息转换成一个模块树
     * @return
     */
    public List<ModelTree> getModelTree(){
        //创建一个容器存储各个父模块对应的子模块信息
        HashMap<String,ModelTree> treeList=new HashMap<>();

        //遍历所有模块信息,获取所有的模块信息并创建
        permissionsList.forEach(
            e->{
                if(e.getIsParent()==1){
                    //创建一个树对象
                    ModelTree parentTree=new ModelTree();
                    parentTree.setChildren(new ArrayList<>());
                    parentTree.setIcon_class(e.getIconClass());
                    parentTree.setId(e.getModelId());
                    parentTree.setLabel(e.getModelName());
                    parentTree.setRequestPath(e.getModelPath());
                    treeList.put(e.getModelId(),parentTree);
                }
            }
        );


        //遍历所有模块信息,封装各个父模块下对应的子模块信息
        permissionsList.forEach(
           e->{
               if(e.getIsParent()==0){

                   //获取父模块树对象
                   ModelTree parentTree = treeList.get(e.getParentId());
                   //创建一个树对象
                   ModelTree childTree=new ModelTree();
                   childTree.setChildren(new ArrayList<>());
                   childTree.setIcon_class(e.getIconClass());
                   childTree.setId(e.getModelId());
                   childTree.setLabel(e.getModelName());

                   //获取父模块下对应的子模块集合
                   List<ModelTree> children = parentTree.getChildren();

                   //将当前子模块放到children中
                   children.add(childTree);
               }
           }
        );

        //将treeList转换成一个集合
        return treeList.entrySet().stream().map(e->{return e.getValue();}).collect(Collectors.toList());
    }

}
