package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 新增分类
     *
     * @param categoryDTO 分类新增请求数据
     */
    void save(CategoryDTO categoryDTO);

    /**
     * 分页查询分类数据
     *
     * @param categoryPageQueryDTO 分类分页查询请求数据
     * @return 分类分页查询响应数据
     */
    PageResult<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id删除分类
     *
     * @param id 分类ID
     */
    void deleteById(Long id);

    /**
     * 修改分类
     *
     * @param categoryDTO 分类修改请求数据
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 启用/禁用分类
     *
     * @param status 分类状态
     * @param id     分类ID
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据类型查询分类
     *
     * @param type 分类类型
     * @return 分类查询响应数据
     */
    List<Category> list(Integer type);
}
