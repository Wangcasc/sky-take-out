package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
