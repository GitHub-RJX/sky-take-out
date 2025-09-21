package com.sky.controller.admin;

import com.sky.constant.RedisConstant;
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
        // 新增菜品数据
        dishService.saveWithFlavor(dishDTO);
        // 清除Redis缓存中菜品所属分类下的菜品数据
        redisTemplate.delete(RedisConstant.DISH_CATEGORY_ + dishDTO.getCategoryId());
        return Result.success();
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO 分页查询请求数据
     * @return 分页查询响应数据
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult<DishVO>> page(DishPageQueryDTO dishPageQueryDTO) {
//        log.info("菜品分页查询：{}", dishPageQueryDTO);
        return Result.success(dishService.pageQuery(dishPageQueryDTO));
    }

    /**
     * 启售/停售菜品
     *
     * @param status 菜品状态
     * @param id     菜品ID
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用/停用菜品")
    public Result<Void> startOrStop(@PathVariable Integer status, Long id) {
//        log.info("启用或停用菜品：{}", id);
        // 启售/停售菜品
        dishService.startOrStop(status, id);
        // 清除Redis缓存中菜品所属分类下的菜品数据
        Set<String> categoryKeys = redisTemplate.keys(RedisConstant.DISH_CATEGORY_ + "*");
        if (!CollectionUtils.isEmpty(categoryKeys)) {
            redisTemplate.delete(categoryKeys);
        }
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
        // 批量删除菜品
        dishService.deleteBatch(ids);
        // 清除Redis缓存中菜品所属分类下的菜品数据
        Set<String> categoryKeys = redisTemplate.keys(RedisConstant.DISH_CATEGORY_ + "*");
        if (!CollectionUtils.isEmpty(categoryKeys)) {
            redisTemplate.delete(categoryKeys);
        }
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据ID查询指定菜品")
    public Result<DishVO> getByIdWithFlavor(@PathVariable Long id) {
//        log.info("根据ID查询指定菜品：{}", id);
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
        // 更新菜品信息
        dishService.updateWithFlavor(dishDTO);
        // 清除Redis缓存中菜品所属分类下的菜品数据
        Set<String> categoryKeys = redisTemplate.keys(RedisConstant.DISH_CATEGORY_ + "*");
        if (!CollectionUtils.isEmpty(categoryKeys)) {
            redisTemplate.delete(categoryKeys);
        }
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId 分类ID
     * @return 菜品列表
     */
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId) {
        List<Dish> dishList = dishService.list(categoryId);
        return Result.success(dishList);
    }
}
