package com.huijianzhu.heqing.controller;

import cn.hutool.json.JSONUtil;
import com.huijianzhu.heqing.definition.HqPlotPipeDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================================
 * 说明：管道搬迁请求接口控制器
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/10  16:52            创建
 * =================================================================
 **/
@Slf4j
@Controller
@RequestMapping("/removal")
public class PipeController {

    @Autowired
    private HttpServletResponse response;

    /**
     * 注入地块信息业务接口
     */
    @Autowired
    private PlotService plotService;


    /**
     * 注入：管道搬迁业务接口
     */
    @Autowired
    private PipeService pipeService;



    /**
     * 获取所有与管道名称相关的管道信息默认是查询出所有
     * @param pipeName   关于管道的名称
     * @return
     */
    @ResponseBody
    @PostMapping("/show/removal")
    public SystemResult getPipeContentListByName(String pipeName){
        return pipeService.getPipeContentListByName(pipeName);
    }

    /**
     * 获取指定id对应的管道信息
     * @param pipeId  某一个管道信息id
     * @return
     */
    @ResponseBody
    @PostMapping("/show/removal/one")
    public SystemResult getPipeDescById(String pipeId){
        return pipeService.getPipeDescById(pipeId);
    }


    /**
     * 打印管道搬迁模板
     */
    @GetMapping("/update/removal/template")
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

        //获取地块模板信息 前面两种放到jar包中 系统就会读取不到对应的文件信息会报 FileNotFoundException
        //File file = ResourceUtils.getFile("classpath:templates/"+templateEnglishNameArray[type]);
        //ClassPathResource resource = new ClassPathResource("templates/"+templateEnglishNameArray[type]);
        //这种方式在jar包中也能读取
        InputStream templateStream = this.getClass().getClassLoader().getResourceAsStream("templates/pipeTemplate.xlsx");

        //将当前文件字节流读取到Excel工作簿中
        XSSFWorkbook workbook = (XSSFWorkbook) ExcelUtils.getWorkbook(templateStream, "pipeTemplate.xlsx");

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
     * 读取管道信息
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/update/removal/template/import")
    public  SystemResult readPipeTemplate(@RequestParam("file") MultipartFile file)throws Exception{
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
    
    
    
    


    /**
     * 添加管道信息
     * @param definition 封装了管道信息的实体对象
     * @return
     */
    @ResponseBody
    @PostMapping("/update/removal/add")
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws  Exception{
        return pipeService.add(definition);
    }

    /**
     * 修改管道,地管道属性信息
     * @param definition 封装了管道修改信息及对应的,管道属性信息
     * @return
     */
    @ResponseBody
    @PostMapping("/update/removal/update")
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws  Exception{
        return pipeService.updateContent(definition);
    }

    /**
     * 删除管道信息
     * @param pipeId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/update/removal/delete")
    public SystemResult deleteById(Integer pipeId)throws  Exception{
        return pipeService.deleteById(pipeId);
    }
}
