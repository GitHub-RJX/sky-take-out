package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类管理")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param categoryDTO 分类新增请求数据
     * @return 分类新增响应结果
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result<Void> save(@RequestBody CategoryDTO categoryDTO) {
//        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询分类数据
     *
     * @param categoryPageQueryDTO 分类分页查询请求数据
     * @return 分类分页查询响应数据
     */
    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult<Category>> page(CategoryPageQueryDTO categoryPageQueryDTO) {
//        log.info("分页查询：{}", categoryPageQueryDTO);
        PageResult<Category> pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除分类
     *
     * @param id 分类ID
     * @return 响应结果
     */
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result<Void> deleteById(Long id) {
//        log.info("删除分类：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改分类
     *
     * @param categoryDTO 分类修改请求数据
     * @return 分类修改响应结果
     */
    @PutMapping
    @ApiOperation("修改分类")
    public Result<Void> update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     * 启用/禁用分类
     *
     * @param status 分类状态
     * @param id     分类ID
     * @return 响应结果
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用/禁用分类")
    public Result<Void> startOrStop(@PathVariable("status") Integer status, @RequestParam Long id) {
        categoryService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     *
     * @param type 分类类型
     * @return 分类查询响应数据
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> list(Integer type) {
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
