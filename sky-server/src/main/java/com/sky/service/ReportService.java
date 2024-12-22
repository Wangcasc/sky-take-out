package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;


public interface ReportService {

    /**
     * 获取营业额报表
     * @param startDate 开始日期
     * @param endDate  结束日期
     * @return 营业额报表
     */
    public TurnoverReportVO getTurnoverReport(LocalDate startDate, LocalDate endDate);

    /**
     * 获取用户报表
     * @param begin 开始日期
     * @param end   结束日期
     * @return 用户报表
     */
    UserReportVO getUserReport(LocalDate begin, LocalDate end);

    /**
     * @param begin 开始日期
     * @param end  结束日期
     * @return 订单报表
     */
    OrderReportVO getOrderReport(LocalDate begin, LocalDate end);
}
