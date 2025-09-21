package com.sky.controller.user;

import com.sky.constant.RedisConstant;
import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId 分类ID
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        // 查询Redis缓存中是否有菜品数据
        List<DishVO> dishVOList =
                (List<DishVO>) redisTemplate.opsForValue().get(RedisConstant.DISH_CATEGORY_ + categoryId);
        // 若存在则直接返回数据，不查询数据库
        if (!CollectionUtils.isEmpty(dishVOList)) {
            return Result.success(dishVOList);
        }
        // 若Redis中不存在则从数据库中查询，并存入Redis缓存
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        dishVOList = dishService.listWithFlavor(dish);
        redisTemplate.opsForValue().set(RedisConstant.DISH_CATEGORY_ + categoryId, dishVOList);
        return Result.success(dishVOList);
    }

}
