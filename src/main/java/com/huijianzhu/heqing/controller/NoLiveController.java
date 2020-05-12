package com.huijianzhu.heqing.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.huijianzhu.heqing.entity.HqNoLiveAccount;
import com.huijianzhu.heqing.entity.HqPlot;
import com.huijianzhu.heqing.enums.SYSTEM_RESULT_STATE;
import com.huijianzhu.heqing.service.NoLiveService;
import com.huijianzhu.heqing.service.PlotService;
import com.huijianzhu.heqing.utils.DownloadUtil;
import com.huijianzhu.heqing.utils.ExcelUtils;
import com.huijianzhu.heqing.vo.SystemResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 说明：非居住付款台账请求接口控制器
 * <p>
 * 作者          时间                    注释
 * 刘梓江    2020/5/12  17:22            创建
 * =================================================================
 **/
@Slf4j
@Controller
@CrossOrigin  //支持跨域
@RequestMapping("/statistics")
public class NoLiveController {


    @Autowired
    private HttpServletResponse response;

    /**
     * 注入地块信息业务接口
     */
    @Autowired
    private PlotService plotService;


    /**
     * 注入：非居住付款台账业务接口
     */
    @Autowired
    private NoLiveService noLiveService;


    /**
     * 获取所有非居住付款台账信息集合
     * @return
     */
    @ResponseBody
    @RequestMapping("/show/statistics")
    public SystemResult findAll(){
        return noLiveService.findAll();
    }

    /**
     * 打印非居住台账模板
     */
    @GetMapping("/update/statistics/template")
    public void printNoLiveMoneyTemplate()throws Exception{
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


        //获取地块模板信息
        File file = ResourceUtils.getFile("classpath:templates/noLivePayTemplate.xlsx");
        //以流的形式读取模板信息
        InputStream templateStream=new FileInputStream(file);

        //将当前文件字节流读取到Excel工作簿中
        XSSFWorkbook workbook = (XSSFWorkbook) ExcelUtils.getWorkbook(templateStream, "noLivePayTemplate.xlsx");

        //获取对应的(第一个)sheet表
        XSSFSheet sheet1 = workbook.getSheetAt(0);  //选择sheet(表)

        //获取第3行信息
        Row tableTitleRow=sheet1.getRow(2);    	 //获取sheet第3行对象
        //获取第3行对应的第一列样式信息
        CellStyle threeRowOneCellStyle = tableTitleRow.getCell(0).getCellStyle();

        //创建行对象和列对象的引用变量
        Row row=null;
        Cell cell=null;

        //创建地块信息编号Excel下拉框内容
        int arrayIndex=0;
        String [] plotNameArray=new String[plots.size()];
        for(HqPlot hp:plots){
            plotNameArray[arrayIndex++]= hp.getPlotName();
        }
        //创建下拉框对象
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet1);
        CellRangeAddressList addressList = null;
        XSSFDataValidation validation = null;
        //创建地块信息列表下拉框
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(plotNameArray);

        //获取原有对应的非居住付款台账信息集
        List<HqNoLiveAccount> currNoLiveList=(List<HqNoLiveAccount>)findAll().getResult();
        if(currNoLiveList!=null&&currNoLiveList.size()>0){

            /**
             * -----------------------显示原有数据信息开始-----------------------------
             */
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
            //对每一行下对应的列创建一个下拉框
            addressList = new CellRangeAddressList(2,currNoLiveList.size()-1+2,0,0);
            validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
            sheet1.addValidationData(validation);

            /**
             * -----------------------显示原有数据信息结束-----------------------------
             */

        }else{

            /**
             * ------------------------默认的显示方式开始-----------------------------
             */
            //创建第三行信息对象
            row=sheet1.createRow(2);
            cell= row.createCell(0);   //创建第一列
            cell.setCellStyle(threeRowOneCellStyle);
            cell.setCellValue("选择地块编号");

            addressList = new CellRangeAddressList(2,2,0,0);
            validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
            validation.setSuppressDropDownArrow(true);
            validation.setShowErrorBox(true);
            sheet1.addValidationData(validation);
            /**
             * ------------------------默认的显示方法结束-----------------------------
             */
        }




        //将excel写入流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();			//生成流对象
        workbook.write(byteArrayOutputStream);
        //工具类，封装弹出下载框：
        DownloadUtil down = new DownloadUtil();
        down.download(byteArrayOutputStream, response, "非居住付款台账编辑信息.xlsx");
        return;

    }


    /**
     * 批量导入非居住付款台账信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/update/statistics")
    public SystemResult batchAdd(@RequestParam("xxx.xlsx") MultipartFile file) throws Exception{
        log.info("获取到文件内容了。。。。"+file.getOriginalFilename());


        //获取Excel文件字节输入流
        InputStream inputStream = file.getInputStream();

        //将当前文件字节流读取到Excel工作簿中
        Workbook workbook = ExcelUtils.getWorkbook(inputStream, file.getOriginalFilename());

        //定义 sheet表个行对象引用变量
        Sheet sheet = null;
        Row row = null;

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
        return noLiveService.batchAdd(accounts);
    }
}
