package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和对应的口味
     *
     * @param dishDTO 菜品请求数据
     */
    void saveWithFlavor(DishDTO dishDTO);

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO 菜品分页查询请求数据
     * @return 分页查询响应结果
     */
    PageResult<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 菜品批量删除
     *
     * @param ids 菜品ID
     */
    void deleteBatch(Long[] ids);

    /**
     * 根据id查询菜品和对应的口味数据
     *
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 根据ID修改菜品基本信息和对应的口味信息
     *
     * @param dishDTO 菜品修改请求数据
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 菜品起售停售
     *
     * @param status 菜品状态
     * @param id     菜品ID
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    List<Dish> list(Long categoryId);

    /**
     * 条件查询菜品和口味
     *
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
