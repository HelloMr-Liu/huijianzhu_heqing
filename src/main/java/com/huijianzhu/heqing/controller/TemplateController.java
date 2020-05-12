package com.huijianzhu.heqing.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.huijianzhu.heqing.cache.LoginTokenCacheManager;
import com.huijianzhu.heqing.definition.HqPlotPipeDefinition;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.service.HouseService;
import com.huijianzhu.heqing.service.PipeService;
import com.huijianzhu.heqing.service.PlotService;
import com.huijianzhu.heqing.utils.DownloadUtil;
import com.huijianzhu.heqing.utils.ExcelUtils;
import com.huijianzhu.heqing.vo.SystemResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================================
 * 说明：模板请求接口控制器
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/12  10:51            创建
 * =================================================================
 **/

@Slf4j
@Controller
@CrossOrigin  //开启跨域
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;



    /**
     * 注入地块信息业务接口
     */
    @Autowired
    private PlotService plotService;

    /**
     * 注入房屋动迁信息业务接口
     */
    @Autowired
    private HouseService houseService;

    /**
     * 注入管道搬迁信息业务接口
     */
    @Autowired
    private PipeService pipeService;

    /**
     * 注入登录标识信息缓存管理
     */
    @Autowired
    private LoginTokenCacheManager loginTokenCacheManager;


    /**
     * 打印地块模板
     */
    @GetMapping("/blockTemplate")
    public void printBlockTemplate()throws Exception{
        //获取地块模板信息
        File file = ResourceUtils.getFile("classpath:templates/blockTemplate.xlsx");
        //以流的形式读取模板信息
        InputStream templateStream=new FileInputStream(file);
        new DownloadUtil().prototypeDownload(file,"地块编号编辑.xlsx",response,false);
    }

    /**
     * 打印房屋动迁模板
     */
    @GetMapping("/houseTemplate")
    public void printHouseTemplate()throws Exception{
        //设置缓存区编码为UTF-8编码格式
        response.setCharacterEncoding("UTF-8");
        //在响应中主动告诉浏览器使用UTF-8编码格式来接收数据
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        //可以使用封装类简写Content-Type，使用该方法则无需使用setCharacterEncoding
        response.setContentType("text/html;charset=UTF-8");


        //获取所有地块信息
        SystemResult plotContentListByName = plotService.getPlotContentListByName(null);
        List<HqPlot> plots=( List<HqPlot>)plotContentListByName.getResult();
        if(plots==null||plots.size()<1){
            PrintWriter writer = response.getWriter();
            writer.write(JSONUtil.toJsonStr(SystemResult.build(SYSTEM_RESULT_STATE.PRINT_FAILURE.KEY,"没有地块信息,所以不能导出房屋动迁模板")));
            writer.flush();
            return;
        }
        int arrayIndex=0;
        //将plots信息对应的地块名称存储到一个数组中
        String [] plotNameArray=new String[plots.size()];
        for(HqPlot hp:plots){
            plotNameArray[arrayIndex++]= hp.getPlotName();
        }

        //创建一个房屋动迁类型数组
        String []houseType={"NOLIVE","LIVE"};

        //获取地块模板信息
        File file = ResourceUtils.getFile("classpath:templates/houseTemplate.xlsx");
        //以流的形式读取模板信息
        InputStream templateStream=new FileInputStream(file);

        //将当前文件字节流读取到Excel工作簿中
        XSSFWorkbook workbook = (XSSFWorkbook)ExcelUtils.getWorkbook(templateStream, "houseTemplate.xlsx");

        //获取对应的(第一个)sheet表
        XSSFSheet sheet1 = workbook.getSheetAt(0);  //选择sheet(表)

        //创建合并单元格 合并第三行对应的1-3列,4-6列,7-9列
        CellRangeAddress region1 = new CellRangeAddress(2,2, 0, 2);
        CellRangeAddress region2 = new CellRangeAddress(2,2, 3, 5);
        CellRangeAddress region3 = new CellRangeAddress(2,2, 6, 8);
        sheet1.addMergedRegion(region1);
        sheet1.addMergedRegion(region2);
        sheet1.addMergedRegion(region3);

        //获取第3行信息
        Row tableTitleRow=sheet1.getRow(2);    	 //获取sheet第3行对象
        //获取第3行对应的第一列样式信息
        CellStyle threeRowOneCellStyle = tableTitleRow.getCell(0).getCellStyle();

        //创建第三行信息对象
        Row nRow3=sheet1.createRow(2);

        Cell cell1 = nRow3.createCell(0);   //创建第一列
        cell1.setCellStyle(threeRowOneCellStyle);
        cell1.setCellValue("选择地块编号");

        Cell cell2 = nRow3.createCell(3);   //创建第4列
        cell2.setCellStyle(threeRowOneCellStyle);
        cell2.setCellValue("选择房屋类型");

        Cell cell3 = nRow3.createCell(6);   //创建第7列

        CellRangeAddressList addressList = null;
        XSSFDataValidation validation = null;

        //创建下拉框开始
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet1);

        //创建地块信息列表下拉框
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(plotNameArray);
        addressList = new CellRangeAddressList(2,2,0,0);
        validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        sheet1.addValidationData(validation);

        //创建房屋类型列表下拉框
        XSSFDataValidationConstraint dvConstraint2 = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(houseType);
        addressList = new CellRangeAddressList(2,2,3,3);
        validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint2, addressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        sheet1.addValidationData(validation);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();			//生成流对象
        workbook.write(byteArrayOutputStream);
        //将excel写入流
        //工具类，封装弹出下载框：
        DownloadUtil down = new DownloadUtil();
        down.download(byteArrayOutputStream, response, "房屋动迁编辑信息.xlsx");
        return;
    }


    /**
     * 打印管道搬迁模板
     */
    @GetMapping("/pipeTemplate")
    public void printPipeTemplate()throws Exception{
        //设置缓存区编码为UTF-8编码格式
        response.setCharacterEncoding("UTF-8");
        //在响应中主动告诉浏览器使用UTF-8编码格式来接收数据
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        //可以使用封装类简写Content-Type，使用该方法则无需使用setCharacterEncoding
        response.setContentType("text/html;charset=UTF-8");


        //获取所有地块信息
        SystemResult plotContentListByName = plotService.getPlotContentListByName(null);
        List<HqPlot> plots=( List<HqPlot>)plotContentListByName.getResult();
        if(plots==null||plots.size()<1){
            PrintWriter writer = response.getWriter();
            writer.write(JSONUtil.toJsonStr(SystemResult.build(SYSTEM_RESULT_STATE.PRINT_FAILURE.KEY,"没有地块信息,所以不能导出管道搬迁模板")));
            writer.flush();
            return;
        }

        int arrayIndex=0;
        //将plots信息对应的地块名称存储到一个数组中
        String [] plotNameArray=new String[plots.size()];
        for(HqPlot hp:plots){
            plotNameArray[arrayIndex++]= hp.getPlotName();
        }


        //获取地块模板信息
        File file = ResourceUtils.getFile("classpath:templates/pipeTemplate.xlsx");
        //以流的形式读取模板信息
        InputStream templateStream=new FileInputStream(file);

        //将当前文件字节流读取到Excel工作簿中
        XSSFWorkbook workbook = (XSSFWorkbook)ExcelUtils.getWorkbook(templateStream, "pipeTemplate.xlsx");

        //获取对应的(第一个)sheet表
        XSSFSheet sheet1 = workbook.getSheetAt(0);  //选择sheet(表)

        //创建合并单元格 合并第三行对应的1-4列,5-8列
        CellRangeAddress region1 = new CellRangeAddress(2,2, 0, 3);
        CellRangeAddress region2 = new CellRangeAddress(2,2, 4, 7);
        sheet1.addMergedRegion(region1);
        sheet1.addMergedRegion(region2);


        //获取第3行信息
        Row tableTitleRow=sheet1.getRow(2);    	 //获取sheet第3行对象
        //获取第3行对应的第一列样式信息
        CellStyle threeRowOneCellStyle = tableTitleRow.getCell(0).getCellStyle();

        //创建第三行信息对象
        Row nRow3=sheet1.createRow(2);
        Cell cell1 = nRow3.createCell(0);   //创建第一列
        cell1.setCellStyle(threeRowOneCellStyle);
        cell1.setCellValue("选择地块编号");


        //创建下拉框开始
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet1);

        CellRangeAddressList addressList = null;
        XSSFDataValidation validation = null;
        //创建地块信息列表下拉框
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(plotNameArray);
        addressList = new CellRangeAddressList(2,2,0,0);
        validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        sheet1.addValidationData(validation);


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();			//生成流对象
        workbook.write(byteArrayOutputStream);
        //将excel写入流
        //工具类，封装弹出下载框：
        DownloadUtil down = new DownloadUtil();
        down.download(byteArrayOutputStream, response, "管道搬迁编辑信息.xlsx");
        return;
    }






    /**
     * 读取地块信息
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/readBlockTemplate")
    public  SystemResult readBlockTemplate(@RequestParam("xxx.xlsx") MultipartFile file)throws Exception{
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
     * 读取管道信息
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/readPipeTemplate")
    public  SystemResult readPipeTemplate(@RequestParam("xxx.xlsx") MultipartFile file)throws Exception{
        log.info("获取到文件内容了。。。。"+file.getOriginalFilename());

        //获取Excel文件字节输入流
        InputStream inputStream = file.getInputStream();

        //将当前文件字节流读取到Excel工作簿中
        Workbook workbook = ExcelUtils.getWorkbook(inputStream, file.getOriginalFilename());

        //定义 sheet表个行对象引用变量
        Sheet sheet = null;
        Row row = null;

        //创建一个存储管道信息集合
        List<HqPlotPipeDefinition> pipes=new ArrayList<>();

        /**
         * 获取sheet表集
         * workbook.getNu   mberOfSheets() Excel中实际sheet量-1
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

                //获取当前行对应的第1列,第5列
                String current1CellValue = row.getCell(0).getStringCellValue();
                String current2CellValue = row.getCell(4).getStringCellValue();

                //创建地块信息
                HqPlotPipeDefinition pipe=new HqPlotPipeDefinition();
                pipe.setPlotName(current1CellValue);           //地块信息
                pipe.setPipeName(current2CellValue);           //管道信息
                pipes.add(pipe);
            }
        }
        return pipeService.batchInsertPipes(pipes);
    }
}
