package com.huijianzhu.heqing.controller;

import ch.qos.logback.core.util.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.huijianzhu.heqing.entity.HqHouseResettlement;
import com.huijianzhu.heqing.entity.HqNoLiveAccount;
import com.huijianzhu.heqing.entity.HqPipeAccount;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.pojo.HouseResettlementData;
import com.huijianzhu.heqing.pojo.NoLiveMoneyData;
import com.huijianzhu.heqing.service.HouseResettlementService;
import com.huijianzhu.heqing.service.NoLiveService;
import com.huijianzhu.heqing.service.PipeAccountService;
import com.huijianzhu.heqing.service.PlotService;
import com.huijianzhu.heqing.utils.DownloadUtil;
import com.huijianzhu.heqing.utils.ExcelUtils;
import com.huijianzhu.heqing.vo.SystemResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ================================================================
 * 说明：数据统计请求接口控制器
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/12  17:22            创建
 * =================================================================
 **/
@Slf4j      //日志使用
@Validated  //数据校验
@Controller
@RequestMapping("/statistics")
public class DataStatisticsController {


    @Autowired
    private HttpServletResponse response;

    /**
     * 注入：地块信息业务接口
     */
    @Autowired
    private PlotService plotService;

    /**
     * 注入：非居住付款台账业务接口
     */
    @Autowired
    private NoLiveService noLiveService;

    /**
     * 注入：管道搬迁业务接口
     */
    @Autowired
    private PipeAccountService pipeAccountService;

    /**
     * 注入：房屋动迁信息业务接口
     */
    @Autowired
    private HouseResettlementService houseResettlementService;


    /**
     * 获取对应统计类型对应的数据集
     * @param type 0:动迁进度模板,1:非居住付款台账模板,2:管道搬迁费用模板
     * @return
     */
    @ResponseBody
    @PostMapping("/show/statistics")
    public SystemResult findAll(@NotNull Integer type){
        switch (type) {
            //房屋动迁模板信息集
            case 0:
                return houseResettlementService.findAll();
            //非居住付款台账信息集
            case 1:
                return noLiveService.findAll();
            //管道搬迁费用信息集
            case 2:
                return pipeAccountService.findAll();
        }
        return  null;
    }

    /**
     * 打印对应的统计数据模板
     * @param type 0:动迁进度模板,1:非居住付款台账模板,2:管道搬迁费用模板
     * @throws Exception
     */
    @GetMapping("/update/statistics/template")
    public void printTemplate(@NotNull Integer type)throws Exception{
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
        //创建存储3大模板类型对应的模板名称 (0:动迁进度模板,1:非居住付款台账模板,2:管道搬迁费用模板)
        String [] templateEnglishNameArray={"resettlementTemplate.xlsx","noLivePayTemplate.xlsx","pipeAccountTemplate.xlsx"};
        String [] templateChineseNameArray={"房屋动迁信息编辑.xlsx","非居住付款台账信息编辑.xlsx","管道搬迁信息编辑.xlsx"};
        /**
         * -----------------------------------公共区域开始------------------------------------------
         */
        //获取地块模板信息 前面两种放到jar包中 系统就会读取不到对应的文件信息会报 FileNotFoundException
        //File file = ResourceUtils.getFile("classpath:templates/"+templateEnglishNameArray[type]);
        //ClassPathResource resource = new ClassPathResource("templates/"+templateEnglishNameArray[type]);
        //这种方式在jar包中也能读取
        InputStream templateStream = this.getClass().getClassLoader().getResourceAsStream("templates/" + templateEnglishNameArray[type]);


        //将当前文件字节流读取到Excel工作簿中
        XSSFWorkbook workbook = (XSSFWorkbook) ExcelUtils.getWorkbook(templateStream, templateEnglishNameArray[type]);

        //获取对应的(第一个)sheet表
        XSSFSheet sheet1 = workbook.getSheetAt(0);  //选择sheet(表)

        //获取第3行信息
        Row tableTitleRow=sheet1.getRow(1);    	 //获取sheet第2行对象
        //获取第3行对应的第一列样式信息
        CellStyle threeRowOneCellStyle = tableTitleRow.getCell(0).getCellStyle();

        //创建行对象和列对象的引用变量
        Row row=null;
        Cell cell=null;
        /**
         * -----------------------------------公共区域结束-----------------------------------------
         */

        /**
         * ------------------------默认的显示方式开始-----------------------------
         */
        //创建第三行信息对象
        row=sheet1.createRow(2);
        cell= row.createCell(0);   //创建第一列
        cell.setCellStyle(threeRowOneCellStyle);
        cell.setCellValue("选择地块编号");
        /**
         * ------------------------默认的显示方法结束-----------------------------
         */



        /**
         * ------------------ 创建地块信息编号下拉框对象开始-------------------------------------------
         */
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet1);
        CellRangeAddressList addressList = null;
        XSSFDataValidation validation = null;
        //创建地块信息编号Excel下拉框内容
        int arrayIndex=0;
        String [] plotNameArray=new String[plots.size()];
        for(HqPlot hp:plots){
            plotNameArray[arrayIndex++]= hp.getPlotName();
        }
        //创建地块信息列表下拉框创建对象
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(plotNameArray);

        //定义2个变量用于存储 下拉框对应的位置(默认是第三行,第一列)
        int firstRow=2;
        int lastRow=2;
        /**
         * ------------------ 创建地块信息编号下拉框对象结束--------------------------------------------
         */


        /**
         * ----------------------------判断显示指定模板类型对应的模板信息开始-----------------------------
         */
        switch (type){

            //房屋动迁模板信息模板
            case 0:
                List<HqHouseResettlement> resettlements = ((HouseResettlementData) (findAll(0).getResult())).getResettlements();
                if(resettlements!=null&&resettlements.size()>0){

                    for(int index=0;index<resettlements.size();index++){

                        //获取当前房屋动迁信息
                        HqHouseResettlement resettlement = resettlements.get(index);

                        //创建 index+2行对象
                        row=sheet1.createRow(index+2);

                        cell= row.createCell(0);   //创建第一列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(resettlement.getPlotName());


                        cell= row.createCell(3);   //创建第二列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(resettlement.getNoLiveNumber());

                        cell= row.createCell(5);   //创建第三列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(resettlement.getLiveNumber());


                        cell= row.createCell(7);   //创建第四列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(resettlement.getSurplusNumber());
                    }
                    //指定下拉框最后显示的位置
                    lastRow=resettlements.size()-1+2;
                }

                break;
            //非居住付款台账信息模板
            case 1:

                //获取原有对应的非居住付款台账信息集
                List<HqNoLiveAccount> currNoLiveList=((NoLiveMoneyData)(findAll(1).getResult())).getAllList();
                if(currNoLiveList!=null&&currNoLiveList.size()>0){

                    for(int index=0;index<currNoLiveList.size();index++){

                        //获取当前台账对象信息
                        HqNoLiveAccount currentAccount = currNoLiveList.get(index);

                        //创建 index+2行对象
                        row=sheet1.createRow(index+2);

                        cell= row.createCell(0);   //创建第一列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(currentAccount.getPlotName());

                        cell= row.createCell(3);   //创建第二列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(currentAccount.getTotalDealMoney());


                        cell= row.createCell(6);   //创建第三列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(currentAccount.getOkMoney());


                        cell= row.createCell(9);   //创建第四列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(currentAccount.getPayScale());

                    }
                    //指定下拉框最后显示的位置
                    lastRow=currNoLiveList.size()-1+2;

                }
                break;


            //管道搬迁费用信息模板
            case 2:
                //获取管道搬迁费用信息姐
                List<HqPipeAccount> accounts=(List<HqPipeAccount>)findAll(2).getResult();

                if(accounts!=null &&accounts.size()>0){
                    for(int index=0;index<accounts.size();index++){

                        //获取当前管道搬迁费用信息
                        HqPipeAccount hqPipeAccount = accounts.get(index);

                        //创建 index+2行对象
                        row=sheet1.createRow(index+2);

                        cell= row.createCell(0);   //创建第一列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(hqPipeAccount.getPlotName());

                        cell= row.createCell(3);   //创建第二列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(hqPipeAccount.getTelecomBudgetAmount());


                        cell= row.createCell(5);   //创建第三列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(hqPipeAccount.getElectricityBudgetAmount());

                        cell= row.createCell(7);   //创建第四列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(hqPipeAccount.getGasBudgetAmount());


                        cell= row.createCell(9);   //创建第五列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(hqPipeAccount.getWaterBudgetAmount());

                        cell= row.createCell(11);   //创建第六列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(hqPipeAccount.getTelecomAuditAmount());

                        cell= row.createCell(13);   //创建第七列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(hqPipeAccount.getElectricityAuditAmount());

                        cell= row.createCell(15);   //创建第八列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(hqPipeAccount.getGasAuditAmount());


                        cell= row.createCell(17);   //创建第九列
                        cell.setCellStyle(threeRowOneCellStyle);
                        cell.setCellValue(hqPipeAccount.getWaterAuditAmount());

                    }
                    //指定下拉框最后显示的位置
                    lastRow=accounts.size()-1+2;
                }
            break;
        }


        /**
         * ------------------------指定下拉框位置开始----------------------------
         */
        addressList = new CellRangeAddressList(firstRow,lastRow,0,0);
        validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
        validation.setSuppressDropDownArrow(true);
        validation.setShowErrorBox(true);
        sheet1.addValidationData(validation);
        /**
         * ------------------------指定下拉框位置结束----------------------------
         */


        /**
         * ------------------------将当前的模板进行打印流操作----------------------
         */
        //将excel写入流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();			//生成流对象
        workbook.write(byteArrayOutputStream);
        //工具类，封装弹出下载框：
        DownloadUtil down = new DownloadUtil();
        down.download(byteArrayOutputStream, response, templateChineseNameArray[type]);
        return;

    }


    /**
     * 批量导入非居住付款台账信息
     * @return
     */
    @ResponseBody
    @PostMapping("/update/statistics")
    public SystemResult batchAdd(@RequestParam("file") MultipartFile file,@NotNull  Integer type) throws Exception{
        log.info("获取到文件内容了。。。。"+file.getOriginalFilename());

        //获取Excel文件字节输入流
        InputStream inputStream = file.getInputStream();

        //将当前文件字节流读取到Excel工作簿中
        Workbook workbook = ExcelUtils.getWorkbook(inputStream, file.getOriginalFilename());

        //定义 sheet表个行对象引用变量
        Sheet sheet = null;
        Row row = null;


        switch (type){

            //解析房屋动迁信息集
            case 0:
                //创建房屋动迁信息集合
                List<HqHouseResettlement> resettlements=new ArrayList<>();

                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

                    //获取当前sheet表
                    sheet = workbook.getSheetAt(i);
                    if(sheet == null) {
                        continue;
                    }

                    for (int j = 2; j <=sheet.getLastRowNum(); j++) {

                        //获取当前sheet对应的索引行
                        row = sheet.getRow(j);

                        //获取第1列,第四列,第6列,第8列
                        String stringCellValue = row.getCell(0).getStringCellValue();
                        Cell cell1 = row.getCell(3);
                        Cell cell2 = row.getCell(5);
                        Cell cell3 = row.getCell(7);
                        cell1.setCellType(CellType.STRING);
                        cell2.setCellType(CellType.STRING);
                        cell3.setCellType(CellType.STRING);

                        String current2CellValue = cell1.getStringCellValue();
                        String current3CellValue = cell2.getStringCellValue();
                        String current4CellValue = cell3.getStringCellValue();


                        //判断单元格内容是否为空
                        if(
                                StrUtil.hasBlank(stringCellValue)||StrUtil.hasBlank(current2CellValue)||
                                        StrUtil.hasBlank(current3CellValue)||StrUtil.hasBlank(current4CellValue)
                        )break;

                        HqHouseResettlement resettlement=new HqHouseResettlement();
                        resettlement.setPlotName(stringCellValue);              //地块信息
                        resettlement.setNoLiveNumber(current2CellValue);        //非居住动迁量
                        resettlement.setLiveNumber(current3CellValue);          //居住动迁量
                        resettlement.setSurplusNumber(current4CellValue);       //剩余动迁量

                        resettlements.add(resettlement);
                    }
                }
                return houseResettlementService.batchImport(resettlements);

            case 1:

                //创建一个台账信息集
                List<HqNoLiveAccount> accounts=new ArrayList<>();

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


                        //获取当前行对应的第1列,第4列,第7列,第10列
                        String current1CellValue = row.getCell(0).getStringCellValue();
                        Cell cell1 = row.getCell(3);
                        Cell cell2 = row.getCell(6);
                        Cell cell3 = row.getCell(9);
                        cell1.setCellType(CellType.STRING);
                        cell2.setCellType(CellType.STRING);
                        cell3.setCellType(CellType.STRING);
                        String current2CellValue = cell1.getStringCellValue();
                        String current3CellValue = cell2.getStringCellValue();
                        String current4CellValue = cell3.getStringCellValue();


                        //判断单元格内容是否为空
                        if(
                                StrUtil.hasBlank(current1CellValue)||StrUtil.hasBlank(current2CellValue)||
                                        StrUtil.hasBlank(current3CellValue)||StrUtil.hasBlank(current4CellValue)
                        )break;

                        //创建地块信息
                        HqNoLiveAccount account=new HqNoLiveAccount();
                        account.setPlotName(current1CellValue);           //地块信息
                        account.setTotalDealMoney(current2CellValue);     //协议金额（总金额）
                        account.setOkMoney(current3CellValue);            //协议金额（已支付）
                        account.setPayScale(current4CellValue);           //支付比例

                        accounts.add(account);
                    }
                }
                //批量导入操作·
                return noLiveService.batchImport(accounts);
            case 2:

                //创建管道搬迁费用信息集
                List<HqPipeAccount> accounts1=new ArrayList<>();

                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

                    //获取当前sheet表
                    sheet = workbook.getSheetAt(i);
                    if (sheet == null) {
                        continue;
                    }

                    for (int j = 2; j <=sheet.getLastRowNum(); j++) {

                        //获取当前sheet对应的索引行
                        row = sheet.getRow(j);

                        //获取第1列,第4列,第6列,第8列,第10列,第12列,第14列,第16列,第18列
                        String current1CellValue = row.getCell(0).getStringCellValue();
                        Cell cell1 = row.getCell(3);
                        Cell cell2 = row.getCell(5);
                        Cell cell3 = row.getCell(7);
                        Cell cell4 = row.getCell(9);
                        Cell cell5 = row.getCell(11);
                        Cell cell6 = row.getCell(13);
                        Cell cell7 = row.getCell(15);
                        Cell cell8 = row.getCell(17);
                        cell1.setCellType(CellType.STRING);
                        cell2.setCellType(CellType.STRING);
                        cell3.setCellType(CellType.STRING);
                        cell4.setCellType(CellType.STRING);
                        cell5.setCellType(CellType.STRING);
                        cell6.setCellType(CellType.STRING);
                        cell7.setCellType(CellType.STRING);
                        cell8.setCellType(CellType.STRING);
                        String current2CellValue = cell1.getStringCellValue();
                        String current3CellValue = cell2.getStringCellValue();
                        String current4CellValue = cell3.getStringCellValue();
                        String current5CellValue = cell4.getStringCellValue();
                        String current6CellValue = cell5.getStringCellValue();
                        String current7CellValue = cell6.getStringCellValue();
                        String current8CellValue = cell7.getStringCellValue();
                        String current9CellValue = cell8.getStringCellValue();


                        //判断单元格内容是否为空
                        if(
                            StrUtil.hasBlank(current1CellValue)||StrUtil.hasBlank(current2CellValue)||
                                    StrUtil.hasBlank(current3CellValue)||StrUtil.hasBlank(current4CellValue)||
                                    StrUtil.hasBlank(current4CellValue)||StrUtil.hasBlank(current5CellValue)||
                                    StrUtil.hasBlank(current6CellValue)||StrUtil.hasBlank(current7CellValue)||
                                    StrUtil.hasBlank(current8CellValue)||StrUtil.hasBlank(current9CellValue)
                        )break;



                        //创建一个管道搬迁费用信息对象
                        HqPipeAccount account=new HqPipeAccount();
                        account.setPlotName(current1CellValue);                 //地块信息
                        account.setTelecomBudgetAmount(current2CellValue);      //电信预算金额
                        account.setElectricityBudgetAmount(current3CellValue);  //电力预算金额
                        account.setGasBudgetAmount(current4CellValue);          //燃气预算金额
                        account.setWaterBudgetAmount(current5CellValue);        //水预算金额
                        account.setTelecomAuditAmount(current6CellValue);       //电信审核金额
                        account.setElectricityAuditAmount(current7CellValue);   //电力审核金额
                        account.setGasAuditAmount(current8CellValue);           //燃气审核金额
                        account.setWaterAuditAmount(current9CellValue);         //水审核金额

                        accounts1.add(account);
                    }
                }
                return pipeAccountService.batchImport(accounts1);
        }

        return null;
    }
}
