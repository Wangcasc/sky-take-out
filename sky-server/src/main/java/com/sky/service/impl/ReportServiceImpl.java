package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 获取营业额报表
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 营业额报表
     */
    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate startDate, LocalDate endDate) {
        // 1. 把开始到结束的每一天的日期计算出来
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(startDate);
        LocalDate tempDate = startDate;
        while (!tempDate.equals(endDate)) { // 日期不相等 一直循环
            tempDate = tempDate.plusDays(1);
            dateList.add(tempDate);
        }
        //log.info("dateList:{}", dateList);
        String dateString = StringUtils.join(dateList, ",");// 日期以逗号分隔 例如：2022-10-01,2022-10-02,2022-10-03

        // 2. 查询每一天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            // 查询每一天的营业额 是指已完成的订单的金额合计
            //select sum(amount) from orders where status = ？ and order_time < ？ and order_time >= ？
            LocalDateTime startDateTime = localDate.atStartOfDay(); // 当天开始时间
            LocalDateTime endDateTime = LocalDateTime.of(localDate, LocalTime.MAX); // 当天结束时间
            Map<String,Object> map = new HashMap<>();
            map.put("status", Orders.COMPLETED); // 已完成
            map.put("startDateTime", startDateTime);
            map.put("endDateTime", endDateTime);

            //查询当天已完成的订单的金额合计
            Double turnover= orderMapper.sumByMap(map);
            if(turnover == null){
                turnover = 0.0;
            }
            turnoverList.add(turnover);
        }
        //log.info("turnoverList:{}", turnoverList);


        // 3. 封装数据
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(dateString);
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList, ",")); // 营业额以逗号分隔 例如：260,210,215


        return turnoverReportVO;
    }

    /**
     * 获取用户报表
     * @param begin 开始日期
     * @param end   结束日期
     * @return 用户报表
     */
    @Override
    public UserReportVO getUserReport(LocalDate begin, LocalDate end) {
        // 1. 把开始到结束的每一天的日期计算出来
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        LocalDate tempDate = begin;
        while (!tempDate.equals(end)) { // 日期不相等 一直循环
            tempDate = tempDate.plusDays(1);
            dateList.add(tempDate);
        }
        //log.info("dateList:{}", dateList);

        // 2. 查询每一天的用户数和总用户数
        List<Integer> userList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            // 查询每一天的用户数 是指当天注册的用户数
            //select count(*) from user where create_time < ？ and create_time >= ？
            LocalDateTime startDateTime = localDate.atStartOfDay(); // 当天开始时间
            LocalDateTime endDateTime = LocalDateTime.of(localDate, LocalTime.MAX); // 当天结束时间
            Map<String,Object> map = new HashMap<>();

            map.put("endDateTime", endDateTime);
            //查询总用户数
            Integer totalUserCount = userMapper.countByMap(map); //只有结束时间
            if(totalUserCount == null){
                totalUserCount = 0;
            }
            totalUserList.add(totalUserCount);

            map.put("startDateTime", startDateTime);
            //查询当天注册的用户数 也就是新增的用户数
            Integer userCount = userMapper.countByMap(map); //开始时间和结束时间
            if(userCount == null){
                userCount = 0;
            }
            userList.add(userCount);
        }


        // 3. 封装数据
        UserReportVO userReportVO = new UserReportVO();
        userReportVO.setDateList(StringUtils.join(dateList, ",")); // 日期以逗号分隔 例如：2022-10-01,2022-10-02,2022-10-03
        userReportVO.setNewUserList(StringUtils.join(userList, ",")); // 用户数以逗号分隔 例如：10,20,30
        userReportVO.setTotalUserList(StringUtils.join(totalUserList, ",")); // 总用户数以逗号分隔 例如：100,200,300

        return userReportVO;
    }

    @Override
    public OrderReportVO getOrderReport(LocalDate begin, LocalDate end) {
        // 1. 把开始到结束的每一天的日期计算出来
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        LocalDate tempDate = begin;
        while (!tempDate.equals(end)) { // 日期不相等 一直循环
            tempDate = tempDate.plusDays(1);
            dateList.add(tempDate);
        }
        //log.info("dateList:{}", dateList);

        // 2. 查询每一天的订单数（完成的有效订单数和所有）
        List<Integer> orderList = new ArrayList<>();
        List<Integer> totalOrderList = new ArrayList<>();
        for (LocalDate localDate : dateList) {
            // 查询每一天的订单数 是指当天完成的有效订单数
            //select count(*) from orders where status = ？ and order_time < ？ and order_time >= ？
            LocalDateTime startDateTime = localDate.atStartOfDay(); // 当天开始时间
            LocalDateTime endDateTime = LocalDateTime.of(localDate, LocalTime.MAX); // 当天结束时间

            //查询每天有效的订单数
            Integer orderCount = getCount(Orders.COMPLETED, startDateTime, endDateTime);
            orderList.add(orderCount);

            //查询每天的订单总数
            Integer totalOrderCount = getCount(null, startDateTime, endDateTime);
            totalOrderList.add(totalOrderCount);

        }
        //计算从开始日期到结束日期的订单总数
        Integer totalOrderCount = totalOrderList.stream().mapToInt(Integer::intValue).sum();

        //查询从开始日期到结束日期的有效订单数
        Integer validOrderCount = orderList.stream().mapToInt(Integer::intValue).sum();

        //计算订单完成率
        Double orderCompletionRate = 0.0;
        if (totalOrderCount != 0) {
            orderCompletionRate = validOrderCount * 1.0 / totalOrderCount;
        }

        // 3. 封装数据
        OrderReportVO orderReportVO = new OrderReportVO();
        orderReportVO.setDateList(StringUtils.join(dateList, ",")); // 日期以逗号分隔 例如：2022-10-01,2022-10-02,2022-10-03
        orderReportVO.setValidOrderCountList(StringUtils.join(orderList, ",")); // 每日有效订单数 例如：10,20,30
        orderReportVO.setOrderCountList(StringUtils.join(totalOrderList, ",")); // 每日订单总数 例如：100,200,300
        orderReportVO.setTotalOrderCount(totalOrderCount); // 订单总数
        orderReportVO.setValidOrderCount(validOrderCount); // 有效订单数
        orderReportVO.setOrderCompletionRate(orderCompletionRate); // 订单完成率


        return orderReportVO;
    }


    private Integer getCount(Integer status, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Map<String,Object> map = new HashMap<>();
        map.put("status", status); // 已完成 有效订单
        map.put("startDateTime", startDateTime);
        map.put("endDateTime", endDateTime);
        Integer orderCount = orderMapper.countByMap(map);
        if(orderCount == null){
            orderCount = 0;
        }
        return orderCount;
    }


    /**
     * 获取订单报表
     * @param begin 开始日期
     * @param end   结束日期
     * @return 订单报表
     */
    @Override
    public SalesTop10ReportVO getSalesTop10Report(LocalDate begin, LocalDate end) {
        //要查询订单详情表查每次订单的分数 还要查订单表查订单的状态
        //select name,sum(od.number) from order_detail od,orders o where od.order_id = o.id and o.status = 5
        // and o.order_time < ？ and o.order_time >= ？ group by name order by sum(od.number) desc limit 10
        //查询出来的结果需要实体专门封装
        List<GoodsSalesDTO> goodsSales = orderMapper.getGoodsSales(LocalDateTime.of(begin, LocalTime.MIN)
                                                                    , LocalDateTime.of(end, LocalTime.MAX));
        List<String> names = goodsSales.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");// 商品名称以逗号分隔 例如：商品1,商品2,商品3
        log.info("nameList:{}", goodsSales);
        List<Integer> numbers = goodsSales.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");// 商品销量以逗号分隔 例如：10,20,30
        //log.info("numberList:{}", numberList);

        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO();
        salesTop10ReportVO.setNameList(nameList);
        salesTop10ReportVO.setNumberList(numberList);

        return salesTop10ReportVO;
    }

    /**
     * @param response 响应 通过HttpServletResponse对象返回Excel文件
     */
    @Override
    public void exportExcel(HttpServletResponse response) {
        //1. 查询数据库，获取营业数据---查询最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        //查询概览数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(LocalDateTime.of(dateBegin, LocalTime.MIN), LocalDateTime.of(dateEnd, LocalTime.MAX));

        //2. 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        try {
            //基于模板文件创建一个新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //获取表格文件的Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");

            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("时间：" + dateBegin + "至" + dateEnd);

            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            //获得第5行
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));

                //获得某一行
                row = sheet.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }

            //3. 通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
