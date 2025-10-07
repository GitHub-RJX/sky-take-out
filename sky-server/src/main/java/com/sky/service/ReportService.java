package com.sky.service;

import java.time.LocalDate;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;

public interface ReportService {

    /**
     * 根据时间区间统计营业额
     *
     * @param begin 统计开始时间
     * @param end   统计结束时间
     */
    TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end);

    /**
     * 根据时间区间统计用户数量
     *
     * @param begin 统计开始时间
     * @param end   统计结束时间
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 根据时间区间统计订单数量
     *
     * @param begin 统计开始时间
     * @param end   统计结束时间
     */
    OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end);

    /**
     * 查询指定时间区间内的销量排名top10
     *
     * @param begin 统计开始时间
     * @param end   统计结束时间
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    /**
     * 导出运营报表
     *
     * @param response 报表导出响应
     */
    void exportExcel(HttpServletResponse response);

}