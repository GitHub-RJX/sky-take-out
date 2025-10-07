package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 根据时间段统计营业数据
     *
     * @param begin 统计开始时间
     * @param end   统计结束时间
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        // 统计总订单数：当日所有订单的数量
        Integer totalOrderCount = orderMapper.countByMap(Map.of(
                "begin", begin,
                "end", end
        ));
        // 统计有效订单数：当日已完成订单的数量
        Integer validOrderCount = orderMapper.countByMap(Map.of(
                "begin", begin,
                "end", end,
                "status", Orders.COMPLETED
        ));
        // 统计营业额：当日已完成订单的总金额
        Double turnover = orderMapper.sumByMap(Map.of(
                "begin", begin,
                "end", end,
                "status", Orders.COMPLETED
        ));
        // 计算订单完成率：有效订单数 / 总订单数
        Double orderCompletionRate = totalOrderCount == 0 ? 0.0 : validOrderCount.doubleValue() / totalOrderCount;
        // 计算平均客单价：营业额 / 有效订单数
        Double unitPrice = validOrderCount == 0 ? 0.0 : Double.parseDouble(String.format("%.2f", turnover / validOrderCount));
        // 新增用户数：当日新增用户的数量
        Integer newUsers = userMapper.countByMap(Map.of(
                "begin", begin,
                "end", end
        ));
        // 返回结果数据
        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 查询订单管理数据
     */
    public OrderOverViewVO getOrderOverView() {
        // 待接单
        Integer waitingOrders = orderMapper.countByMap(Map.of(
                "begin", LocalDateTime.now().with(LocalTime.MIN),
                "status", Orders.TO_BE_CONFIRMED
        ));
        // 待派送
        Integer deliveredOrders = orderMapper.countByMap(Map.of(
                "begin", LocalDateTime.now().with(LocalTime.MIN),
                "status", Orders.CONFIRMED
        ));

        // 已完成
        Integer completedOrders = orderMapper.countByMap(Map.of(
                "begin", LocalDateTime.now().with(LocalTime.MIN),
                "status", Orders.COMPLETED
        ));

        // 已取消
        Integer cancelledOrders = orderMapper.countByMap(Map.of(
                "begin", LocalDateTime.now().with(LocalTime.MIN),
                "status", Orders.CANCELLED
        ));

        // 全部订单
        Integer allOrders = orderMapper.countByMap(Map.of(
                "begin", LocalDateTime.now().with(LocalTime.MIN)
        ));
        // 返回结果数据
        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     */
    public DishOverViewVO getDishOverView() {
        // 查询已启售菜品数量
        Integer sold = dishMapper.countByMap(Map.of("status", StatusConstant.ENABLE));
        // 查询已停售菜品数量
        Integer discontinued = dishMapper.countByMap(Map.of("status", StatusConstant.DISABLE));
        // 返回结果数据
        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询套餐总览
     */
    public SetmealOverViewVO getSetmealOverView() {
        // 查询已启售套餐数量
        Integer sold = setmealMapper.countByMap(Map.of("status", StatusConstant.ENABLE));
        // 查询已停售套餐数量
        Integer discontinued = setmealMapper.countByMap(Map.of("status", StatusConstant.DISABLE));
        // 返回结果数据
        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}