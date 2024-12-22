package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

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
        log.info("dateList:{}", dateList);
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
        log.info("turnoverList:{}", turnoverList);


        // 3. 封装数据
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(dateString);
        turnoverReportVO.setTurnoverList(StringUtils.join(turnoverList, ",")); // 营业额以逗号分隔 例如：260,210,215


        return turnoverReportVO;
    }
}
