package com.sky.controller.admin;

import com.sky.constant.RedisConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新增套餐
     *
     * @param setmealDTO 套餐新增请求数据
     */
    @PostMapping
    @CacheEvict(cacheNames = "setmealCache", key = "#setmealDTO.categoryId")
    public Result<Void> save(@RequestBody SetmealDTO setmealDTO) {
//        log.info("新增套餐：{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO 分页查询请求数据
     * @return 分页查询响应数据
     */
    @GetMapping("/page")
    public Result<PageResult<SetmealVO>> page(SetmealPageQueryDTO setmealPageQueryDTO) {
//        log.info("分页查询套餐列表，请求参数：{}", setmealPageQueryDTO);
        return Result.success(setmealService.pageQuery(setmealPageQueryDTO));
    }

    /**
     * 批量删除套餐
     *
     * @param ids 套餐ID列表
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<Void> deleteBatch(@RequestParam List<Long> ids) {
//        log.info("删除套餐，ids：{}", ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     *
     * @param id 套餐ID
     * @return 套餐查询响应数据
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
//        log.info("根据id查询套餐，id：{}", id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO 套餐修改请求数据
     */
    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<Void> update(@RequestBody SetmealDTO setmealDTO) {
//        log.info("修改套餐，请求参数：{}", setmealDTO);
        // 修改套餐
        setmealService.update(setmealDTO);
        // 清除Redis缓存中菜品所属分类下的菜品数据
        Set<String> categoryKeys = redisTemplate.keys(RedisConstant.SETMEAL_CATEGORY_ + "*");
        if (!CollectionUtils.isEmpty(categoryKeys)) {
            redisTemplate.delete(categoryKeys);
        }
        return Result.success();
    }

    /**
     * 启售或停售套餐
     *
     * @param status 套餐状态
     * @param id     套餐ID
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用或停用套餐")
    @CacheEvict(cacheNames = "setmealCache", allEntries = true)
    public Result<Void> startOrStop(@PathVariable Integer status, Long id) {
//        log.info("启用或停用套餐，status：{}，id：{}", status, id);
        setmealService.startOrStop(status, id);
        return Result.success();
    }
}