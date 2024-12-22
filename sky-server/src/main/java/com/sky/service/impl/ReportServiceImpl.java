package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
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

    @Autowired
    private UserMapper userMapper;

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
}
