package com.huijianzhu.heqing.controller.text;

import com.huijianzhu.heqing.Heqing_8989;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.enums.PLOT_HOUSE_PIPE_TYPE;
import com.huijianzhu.heqing.pojo.AccpetPlotTypePropertyValue;
import com.huijianzhu.heqing.service.PlotService;
import com.huijianzhu.heqing.vo.SystemResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ================================================================
 * 说明：当前类说说明
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/9  12:58            创建
 * =================================================================
 **/
@RunWith(SpringRunner.class)
@SpringBootTest( classes= Heqing_8989.class)
public class PathTest {

    @Autowired
    private PlotService  plotService;

    @Test
    public void test1()throws  Exception{
        //获取对应的当前项目resources目录下对应的files
        File upload = new File(new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath(),"files/");
        System.out.println(upload.getPath());
    }


    @Test
    public void addPlot(){
        /**
         *     //公共接收信息
         *     private String contentId;   //接收对应的内容id
         *     private String contentName; //接收内容名称
         *     //private String contentType; //接收内容类型 (1:代表当前是地块类型信息 2:代表当前是地块房屋类型信息 3:代表当前是地块管道类型信息)
         *     private String plotMark;    //接收一个地标信息
         *
         *
         *     //内容类型为2,3的时候接收的值属性
         *     private Integer plotId;     //用于指定那个地块的信息内容
         *
         *     //内容类型为2的时候接收的值属性
         *     private String houseType;   //房屋类型 1:居住   0：非居住
         */


        for(int index=0;index<10;index++){
            PlotOrHouseOrPipeAccpetDefinition definition=new PlotOrHouseOrPipeAccpetDefinition();
            definition.setContentName("地块000"+index);
            definition.setPlotMark("123123123123");
            try {
                plotService.add(definition);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Test
    public void getOne(){
        SystemResult plotDescById = plotService.getPlotDescById("9");
        System.out.println(plotDescById);
    }

    @Test
    public void update(){
        PlotOrHouseOrPipeUpdateAccpetDefinition definition=new PlotOrHouseOrPipeUpdateAccpetDefinition();
        definition.setContentId(9);
        definition.setContentName("地块7777");
        definition.setPlotMark("1231231231231");

        List<AccpetPlotTypePropertyValue> propertyValueList=new ArrayList<>();


        AccpetPlotTypePropertyValue value=new AccpetPlotTypePropertyValue();
        value.setPlotType(PLOT_HOUSE_PIPE_TYPE.PLOT_TYPE.KEY);
        value.setPlotTypeId(9);
        value.setPropertyValue("LIUZIJIANG");
        value.setPropertyId(9);
        propertyValueList.add(value);


        definition.setPropertyValueList(propertyValueList);
        try {
            plotService.updateContent(definition);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
