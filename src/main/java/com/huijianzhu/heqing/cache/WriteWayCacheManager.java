package com.huijianzhu.heqing.cache;

import com.huijianzhu.heqing.entity.HqWriteWay;
import com.huijianzhu.heqing.mapper.extend.HqWriteWayExtendMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * ================================================================
 * 说明：填写方式缓存管理
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/7  13:55            创建
 * =================================================================
 **/
@Component
@Data
public class WriteWayCacheManager {

    //创建存储填写方式信息缓存
    private ConcurrentHashMap<String, HqWriteWay> cache=new ConcurrentHashMap<>();

    @Autowired
    private HqWriteWayExtendMapper hqWriteWayExtendMapper;  //注入操作填写信息表mapper接口


    /**
     * 初始化缓存管理容器
     */
    @PostConstruct
    public void init(){
        refresh();
    }


    /**
     * 刷新当前缓存信息
     */
    public void refresh(){
        getWriteWays();
    }


    /**
     * 获取所有填写方向信息内容
     */
    private void getWriteWays(){
        List<HqWriteWay> writeWays = hqWriteWayExtendMapper.getWriteWays();
        writeWays.forEach(
            e->{cache.put(e.getWriteId(),e);}
        );
    }

    /**
     * 获取缓存中对应的填写方式信息集
     * @return
     */
    public List<HqWriteWay> getWriteWayList(){
        return cache.entrySet().stream().map(e->e.getValue()).collect(Collectors.toList());
    }
}
