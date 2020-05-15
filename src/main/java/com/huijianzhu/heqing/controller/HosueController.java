package com.huijianzhu.heqing.controller;

import cn.hutool.json.JSONUtil;
import com.huijianzhu.heqing.definition.HqPlotHouseDefinition;
import com.huijianzhu.heqing.definition.HqPlotPipeDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeAccpetDefinition;
import com.huijianzhu.heqing.definition.PlotOrHouseOrPipeUpdateAccpetDefinition;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.entity.HqPlotHouse;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.service.HouseService;
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
 * 说明：房屋动迁请求接口控制器
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/10  16:52            创建
 * =================================================================
 **/
@Slf4j
@Controller
@RequestMapping("/resettlement")
public class HosueController {


    @Autowired
    private HttpServletResponse response;

    /**
     * 注入：房屋动迁业务接口
     */
    @Autowired
    private HouseService houseService;

    /**
     * 注入地块信息业务接口
     */
    @Autowired
    private PlotService plotService;


    /**
     * 获取所有与房屋名称相关的房屋信息默认是查询出所有
     * @param houseName   关于房屋的名称
     * @return
     */
    @ResponseBody
    @PostMapping("/show/resettlement")
    public SystemResult gethouseContentListByName(String houseName){
        return houseService.getHosueContentListByName(houseName);
    }

    /**
     * 获取指定id对应的房屋信息
     * @param houseId  某一个房屋信息id
     * @return
     */
    @ResponseBody
    @PostMapping("/show/resettlement/one")
    public SystemResult gethouseDescById(String houseId){
        return houseService.getHouseDescById(houseId);
    }

    /**
     * 打印房屋动迁模板
     */
    @GetMapping("/update/resettlement/template")
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

        //获取地块模板信息 前面两种放到jar包中 系统就会读取不到对应的文件信息会报 FileNotFoundException
        //File file = ResourceUtils.getFile("classpath:templates/"+templateEnglishNameArray[type]);
        //ClassPathResource resource = new ClassPathResource("templates/"+templateEnglishNameArray[type]);
        //这种方式在jar包中也能读取
        InputStream templateStream = this.getClass().getClassLoader().getResourceAsStream("templates/houseTemplate.xlsx");

        //将当前文件字节流读取到Excel工作簿中
        XSSFWorkbook workbook = (XSSFWorkbook) ExcelUtils.getWorkbook(templateStream, "houseTemplate.xlsx");

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
     * 房屋动迁模板导入
     */
    @ResponseBody
    @PostMapping("/update/resettlement/template/import")
    public SystemResult readHouseTemplate(@RequestParam("file") MultipartFile file)throws Exception{

        log.info("获取到文件内容了。。。。"+file.getOriginalFilename());

        //获取Excel文件字节输入流
        InputStream inputStream = file.getInputStream();

        //将当前文件字节流读取到Excel工作簿中
        Workbook workbook = ExcelUtils.getWorkbook(inputStream, file.getOriginalFilename());

        //定义 sheet表个行对象引用变量
        Sheet sheet = null;
        Row row = null;

        //创建一个存储房屋信息集合
        List<HqPlotHouseDefinition> houses=new ArrayList<>();


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

                //获取当前行对应的第1列,第4列,第7列
                String current1CellValue = row.getCell(0).getStringCellValue();
                String current2CellValue = row.getCell(3).getStringCellValue();
                String current3CellValue = row.getCell(6).getStringCellValue();

                //创建创建房屋信息
                HqPlotHouseDefinition house=new HqPlotHouseDefinition();
                house.setPlotName(current1CellValue);       //地块信息
                house.setHouseType(current2CellValue);      //房屋类型
                house.setHouseName(current3CellValue);      //房屋信息

                houses.add(house);
            }
        }
        return houseService.batchInsertHouses(houses);
    }



    /**
     * 添加房屋信息
     * @param definition 封装了房屋信息的实体对象
     * @return
     */
    @ResponseBody
    @PostMapping("/update/resettlement/add")
    public SystemResult add(PlotOrHouseOrPipeAccpetDefinition definition) throws  Exception{
        return houseService.add(definition);
    }


    /**
     * 修改房屋,地房屋属性信息
     * @param definition 封装了房屋修改信息及对应的,房屋属性信息
     * @return
     */
    @ResponseBody
    @PostMapping("/update/resettlement/update")
    public SystemResult updateContent(PlotOrHouseOrPipeUpdateAccpetDefinition definition) throws  Exception{
        return houseService.updateContent(definition);
    }

    /**
     * 删除房屋信息
     * @param houseId
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/update/resettlement/delete")
    public SystemResult deleteById(Integer houseId)throws  Exception{
        return houseService.deleteById(houseId);
    }
}
