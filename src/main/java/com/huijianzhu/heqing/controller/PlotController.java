package com.huijianzhu.heqing.controller;

import cn.hutool.core.util.StrUtil;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.service.PlotService;
import com.huijianzhu.heqing.utils.DownloadUtil;
import com.huijianzhu.heqing.utils.ExcelUtils;
import com.huijianzhu.heqing.vo.SystemResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================================
 * 说明：土地概况请求接口控制器
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/9  16:52            创建
 * =================================================================
 **/
@Slf4j      //日志使用
@Validated  //数据校验
@Controller
@RequestMapping("/landsurvey")
public class PlotController {


    @Autowired
    private HttpServletResponse response;


    /**
     * 注入：操作地块信息业务接口
     */
    @Autowired
    private PlotService plotService;

    /**
     * 获取所有与地块名称相关的地块信息默认是查询出所有
     * @param plotName   关于地块的名称
     * @return
     */
    @ResponseBody
    @PostMapping("/show/landsurvey")
    public SystemResult getPlotContentListByName(String plotName){
        return plotService.getPlotContentListByName(plotName);
    }

    /**
     * 获取指定id对应的地块信息
     * @param plotId  某一个地块信息id
     * @return
     */
    @ResponseBody
    @PostMapping("/show/landsurvey/one")
    public SystemResult getPlotDescById(String plotId){
        return plotService.getPlotDescById(plotId);
    }



    /**
     * 打印地块模板
     */
    @GetMapping("/update/landsurvey/template")
    public void printBlockTemplate()throws Exception{
        //获取地块模板信息 前面两种放到jar包中 系统就会读取不到对应的文件信息会报 FileNotFoundException
        //File file = ResourceUtils.getFile("classpath:templates/"+templateEnglishNameArray[type]);
        //ClassPathResource resource = new ClassPathResource("templates/"+templateEnglishNameArray[type]);
        //这种方式在jar包中也能读取
        InputStream templateStream = this.getClass().getClassLoader().getResourceAsStream("templates/blockTemplate.xlsx");

        //将当前文件字节流读取到Excel工作簿中
        XSSFWorkbook workbook = (XSSFWorkbook) ExcelUtils.getWorkbook(templateStream, "blockTemplate.xlsx");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();			//生成流对象
        workbook.write(byteArrayOutputStream);
        //将excel写入流
        //工具类，封装弹出下载框：
        DownloadUtil down = new DownloadUtil();
        down.download(byteArrayOutputStream, response, "地块编号编辑.xlsx");
    }


    /**
     * 导入地块模板信息
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/update/landsurvey/template/import")
    public  SystemResult readBlockTemplate(@RequestParam("file") MultipartFile file)throws Exception{
        log.info("获取到文件内容了。。。。"+file.getOriginalFilename());

        //获取Excel文件字节输入流
        InputStream inputStream = file.getInputStream();

        //将当前文件字节流读取到Excel工作簿中
        Workbook workbook = ExcelUtils.getWorkbook(inputStream, file.getOriginalFilename());

        //定义 sheet表个行对象引用变量
        Sheet sheet = null;
        Row row = null;

        //创建一个存储地块信息集合
        List<HqPlot> plots=new ArrayList<>();

        /**
         * 获取sheet表集
         * workbook.getNumberOfSheets() Excel中实际sheet量-1
         */
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            /**
             * 获取当前Sheet表信息
             */
            sheet = workbook.getSheetAt(i);
            if(sheet == null) {
                continue;
            }
            /**
             *  提示：sheet.getFirstRowNum()Excel中第一行在程序中就是 0
             *       sheet.getLastRowNum() Excel中最后一行在程序中就是 实际行数-1
             *       2：我这里过滤了前两行信息
             */
            for (int j = 2; j <=sheet.getLastRowNum(); j++) {
                //获取当前索引行
                row = sheet.getRow(j);
                //获取行对应的列信息
                for (int y = row.getFirstCellNum(); y <row.getLastCellNum(); y++) {
                    //获取当前行对应的列内容
                    String currentCellValue = row.getCell(y).getStringCellValue();
                    if(StrUtil.hasBlank(currentCellValue)){
                        //如果当前列内容没有就代表当前行后面的列内容全部没有信息
                        break;
                    }
                    //创建地块信息
                    HqPlot plot=new HqPlot();
                    plot.setPlotName(currentCellValue);
                    plots.add(plot);
                }

            }
        }
        return plotService.batchInsertPlots(plots);
    }



    /**
     * 添加地块信息
     * @param definition 封装了地块信息的实体对象
     * @return
     */
    @ResponseBody
    @PostMapping("/update/landsurvey/add")
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws  Exception{
        return plotService.add(definition);
    }



    /**
     * 修改地块,地地块属性信息
     * @param definition 封装了地块修改信息及对应的,地块属性信息
     * @return
     */
    @ResponseBody
    @PostMapping("/update/landsurvey/update")
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws  Exception{
        return plotService.updateContent(definition);
    }


    /**
     * 删除地块信息
     * @param plotId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/update/landsurvey/delete")
    public SystemResult deleteById(Integer plotId)throws  Exception{
        return plotService.deleteById(plotId);
    }
}
