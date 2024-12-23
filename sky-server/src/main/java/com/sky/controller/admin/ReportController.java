package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Api(tags = "管理员端报表数据可视化接口")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 营业额报表 通过开始日期和结束日期查询营业额数据
     * @param begin 开始日期
     * @param end  结束日期
     * @return Result<TurnoverReportVO>
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额报表")
    public Result<TurnoverReportVO> turnoverReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额报表, startDate: {}, endDate: {}", begin, end);
        // 1. 调用service查询营业额数据
        TurnoverReportVO turnoverReportVO = reportService.getTurnoverReport(begin, end);
        // 2. 返回结果
        return Result.success(turnoverReportVO);
    }

    /**
     * 用户报表 通过开始日期和结束日期查询用户报表数据
     * @param begin 开始日期
     * @param end  结束日期
     * @return Result<UserReportVO>
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户报表")
    public Result<UserReportVO> userReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户报表, startDate: {}, endDate: {}", begin, end);
        // 1. 调用service查询用户报表数据
        UserReportVO userReportVO = reportService.getUserReport(begin, end);
        // 2. 返回结果
        return Result.success(userReportVO);
    }

    /**
     * 订单报表 通过开始日期和结束日期查询订单报表数据
     * @param begin 开始日期
     * @param end  结束日期
     * @return Result<OrderReportVO>
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单报表")
    public Result<OrderReportVO> orderReport(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单报表, startDate: {}, endDate: {}", begin, end);
        // 1. 调用service查询订单报表数据
        OrderReportVO orderReportVO = reportService.getOrderReport(begin, end);
        // 2. 返回结果
        return Result.success(orderReportVO);
    }


    /**
     * 销售额Top10报表 通过开始日期和结束日期查询销售额Top10报表数据
     * @param begin 开始日期
     * @param end  结束日期
     * @return Result<SalesTop10ReportVO>
     */
    @GetMapping("/top10")
    @ApiOperation("销售数量Top10报表")
    public Result<SalesTop10ReportVO> salesTop10Report(
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("销售数量Top10报表, startDate: {}, endDate: {}", begin, end);
        // 1. 调用service查询销售额Top10报表数据
        SalesTop10ReportVO salesTop10ReportVO = reportService.getSalesTop10Report(begin, end);
        // 2. 返回结果
        return Result.success(salesTop10ReportVO);
    }


    /**
     * 导出Excel
     */
    @GetMapping("/export")
    @ApiOperation("导出报表数据")
    public void exportExcel(HttpServletResponse response) { // 通过HttpServletResponse对象 可以将后端数据返回给前端 用于下载
        // 1. 调用service查询报表数据
        reportService.exportExcel(response);
    }

}
