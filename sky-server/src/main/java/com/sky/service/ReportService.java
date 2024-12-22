package com.sky.service;

import com.sky.vo.TurnoverReportVO;
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

}
