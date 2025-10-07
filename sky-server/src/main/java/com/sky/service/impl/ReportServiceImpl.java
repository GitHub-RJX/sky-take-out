package com.sky.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sky.constant.MessageConstant;
import com.sky.constant.SymbolConstant;
import com.sky.exception.ReportExportException;
import com.sky.exception.RequestParamInvalidException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 根据时间区间统计营业额
     *
     * @param begin 统计开始时间
     * @param end   统计结束时间
     */
    @Override
    public TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end) {
        // 检查起止时间是否合法
        if (begin.isAfter(end)) {
            throw new RequestParamInvalidException(MessageConstant.BEGIN_TIME_IS_LATER_THAN_END_TIME);
        }
        // 生成x轴数据-日期列表
        List<LocalDate> dateList = new ArrayList<>();
        do {
            dateList.add(begin);
            begin = begin.plusDays(1);
        } while (!begin.isAfter(end));
        // 生成y轴数据-营业额列表
        List<Double> turnoverList = dateList.stream()
                .map(date -> {
                    Double turnover = orderMapper.sumByMap(Map.of(
                            "begin", LocalDateTime.of(date, LocalTime.MIN),
                            "end", LocalDateTime.of(date, LocalTime.MAX),
                            "status", Orders.COMPLETED
                    ));
                    return turnover != null ? turnover : 0.0;
                })
                .toList();
        // 返回结果数据
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, SymbolConstant.COMMA))
                .turnoverList(StringUtils.join(turnoverList, SymbolConstant.COMMA))
                .build();
    }

    /**
     * 根据时间区间统计用户数量
     *
     * @param begin 统计开始时间
     * @param end   统计结束时间
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 检查起止时间是否合法
        if (begin.isAfter(end)) {
            throw new RequestParamInvalidException(MessageConstant.BEGIN_TIME_IS_LATER_THAN_END_TIME);
        }
        // 生成x轴数据-日期列表
        List<LocalDate> dateList = new ArrayList<>();
        do {
            dateList.add(begin);
            begin = begin.plusDays(1);
        } while (!begin.isAfter(end));
        // 生成y轴数据-新增用户数列表
        List<Integer> newUserList = dateList.stream()
                .map(date -> userMapper.countByMap(Map.of(
                        "begin", LocalDateTime.of(date, LocalTime.MIN),
                        "end", LocalDateTime.of(date, LocalTime.MAX)
                )))
                .toList();
        // 生成y轴数据-总用户数列表
        List<Integer> totalUserList = dateList.stream()
                .map(date -> userMapper.countByMap(Map.of(
//                        "begin", null,
                        "end", LocalDateTime.of(date, LocalTime.MAX)
                )))
                .toList();
        // 返回结果数据
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, SymbolConstant.COMMA))
                .newUserList(StringUtils.join(newUserList, SymbolConstant.COMMA))
                .totalUserList(StringUtils.join(totalUserList, SymbolConstant.COMMA))
                .build();
    }

    /**
     * 根据时间区间统计订单数量
     *
     * @param begin 统计开始时间
     * @param end   统计结束时间
     */
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 检查起止时间是否合法
        if (begin.isAfter(end)) {
            throw new RequestParamInvalidException(MessageConstant.BEGIN_TIME_IS_LATER_THAN_END_TIME);
        }
        // 生成x轴数据-日期列表
        List<LocalDate> dateList = new ArrayList<>();
        do {
            dateList.add(begin);
            begin = begin.plusDays(1);
        } while (!begin.isAfter(end));
        // 获取每天订单数集合、每天有效订单数集合
        List<Integer> orderCountList = dateList.stream()
                .map(date -> userMapper.countByMap(Map.of(
                        "begin", LocalDateTime.of(date, LocalTime.MIN),
                        "end", LocalDateTime.of(date, LocalTime.MAX)
//                        "status", null
                )))
                .toList();
        List<Integer> validOrderCountList = dateList.stream()
                .map(date -> userMapper.countByMap(Map.of(
                        "begin", LocalDateTime.of(date, LocalTime.MIN),
                        "end", LocalDateTime.of(date, LocalTime.MAX),
                        "status", Orders.COMPLETED
                )))
                .toList();
        // 计算时间区间内的订单数之和、时间区间内的有效订单数之和
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).orElse(0);
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).orElse(0);
        // 计算订单完成率
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : validOrderCount.doubleValue() / totalOrderCount;
        // 返回结果数据
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, SymbolConstant.COMMA))
                .orderCountList(StringUtils.join(orderCountList, SymbolConstant.COMMA))
                .validOrderCountList(StringUtils.join(validOrderCountList, SymbolConstant.COMMA))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 查询指定时间区间内的销量排名top10
     *
     * @param begin 统计开始时间
     * @param end   统计结束时间
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        // 检查起止时间是否合法
        if (begin.isAfter(end)) {
            throw new RequestParamInvalidException(MessageConstant.BEGIN_TIME_IS_LATER_THAN_END_TIME);
        }
        // 获取商品销量排名Top10列表
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper
                .getSalesTop10(LocalDateTime.of(begin, LocalTime.MIN), LocalDateTime.of(end, LocalTime.MAX));
        String nameList = StringUtils
                .join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getName).toList(), SymbolConstant.COMMA);
        String numberList = StringUtils
                .join(goodsSalesDTOList.stream().map(GoodsSalesDTO::getNumber).toList(), SymbolConstant.COMMA);
        // 返回结果数据
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 导出运营报表
     *
     * @param response 导出运营报表
     */
    @Override
    public void exportExcel(HttpServletResponse response) {
        // 统计近30天的营业数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(
                LocalDateTime.of(LocalDate.now().minusDays(30), LocalTime.MIN),
                LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX)
        );
        // 将统计数据写入报表Excel文件
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("templates/运营数据报表模板.xlsx");
            XSSFWorkbook excel = new XSSFWorkbook(is);
            XSSFSheet sheet = excel.getSheetAt(0);
            // 写入时间范围
            sheet.getRow(1).getCell(1).setCellValue(String.format("时间：%s ~ %s",
                    LocalDate.now().minusDays(30), LocalDate.now().minusDays(1)));
            // 写入概览数据
            sheet.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());
            sheet.getRow(4).getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            sheet.getRow(4).getCell(4).setCellValue(businessDataVO.getUnitPrice());
            // 写入明细数据
            for (int i = 0; i < 30; i++) {
                // 查询每天的概览数据
                BusinessDataVO businessData = workspaceService.getBusinessData(
                        LocalDateTime.of(LocalDate.now().minusDays(30).plusDays(i), LocalTime.MIN),
                        LocalDateTime.of(LocalDate.now().minusDays(30).plusDays(i), LocalTime.MAX)
                );
                XSSFRow row = sheet.getRow(i + 7);
                row.getCell(1).setCellValue(LocalDate.now().minusDays(30).plusDays(i).toString());
                row.getCell(2).setCellValue(businessData.getTurnover());
                row.getCell(3).setCellValue(businessData.getValidOrderCount());
                row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData.getUnitPrice());
                row.getCell(6).setCellValue(businessData.getNewUsers());
            }
            // 输出流下载文件
            ServletOutputStream sos = response.getOutputStream();
            excel.write(sos);
            // 关闭资源
            sos.close();
            excel.close();
            is.close();
        } catch (IOException ioException) {
            throw new ReportExportException(MessageConstant.REPORT_EXPORT_EXCEPTION);
        }
    }
}