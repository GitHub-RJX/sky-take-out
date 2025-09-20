package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result<Void> save(@RequestBody DishDTO dishDTO) {
//        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult<DishVO>> page(DishPageQueryDTO dishPageQueryDTO) {
//        log.info("菜品分页查询：{}", dishPageQueryDTO);
        return Result.success(dishService.pageQuery(dishPageQueryDTO));
    }

    /**
     * 启用或停用菜品
     *
     * @param status 菜品状态
     * @param id     菜品ID
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用/停用菜品")
    public Result<Void> startOrStop(@PathVariable Integer status, Long id) {
        // 1. 启用 0. 停用
//        log.info("启用或停用菜品：{}", id);
        dishService.startOrStop(status, id);
        clearRedis("dish_*");
        return Result.success();
    }

    /**
     * 批量删除菜品
     *
     * @param ids 菜品ID
     */
    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result<Void> delete(@RequestParam Long[] ids) {
        dishService.deleteBatch(ids);
        // 将所有菜品缓存数据清理，所有以dish_的key
        Set<String> keys = redisTemplate.keys("dish_*");
        if (!CollectionUtils.isEmpty(keys)) {
            redisTemplate.delete(keys);
        }
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询指定菜品")
    public Result<DishVO> getByIdWithFlavor(@PathVariable Long id) {
        log.info("根据ID查询指定菜品：{}", id);
        return Result.success(dishService.getByIdWithFlavor(id));
    }

    /**
     * 更新菜品信息
     *
     * @param dishDTO 菜品更新请求数据
     * @return 更新请求响应结果
     */
    @PutMapping
    @ApiOperation("更新菜品信息")
    public Result<Void> update(@RequestBody DishDTO dishDTO) {
//        log.info("更新菜品信息：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        // 更新缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        redisTemplate.delete(key);
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> dishList = dishService.list(categoryId);
        return Result.success(dishList);
    }

    private void clearRedis(String keys) {
        Set<String> cacheKeys = redisTemplate.keys(keys);
        redisTemplate.delete(cacheKeys);
    }
}
