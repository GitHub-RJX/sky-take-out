package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.StatusSwitchException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增菜品以及保存口味
     *
     * @param dishDTO 菜品请求数据
     */
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 向菜品表插入1条数据
        dishMapper.insert(dish);

        // 向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (!CollectionUtils.isEmpty(flavors)) {
            // 获取insert语句返回的dishId
            Long dishId = dish.getId();
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO 分页查询请求参数
     * @return 分页查询响应结果
     */
    @Override
    public PageResult<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    /**
     * 删除菜品
     *
     * @param ids 菜品ID
     */
    @Transactional
    @Override
    public void deleteBatch(Long[] ids) {
        // 判断菜品是否处于启售中
        if (Arrays.stream(ids)
                .map(dishMapper::getById)
                .anyMatch(dish -> StatusConstant.ENABLE.equals(dish.getStatus()))) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
        }
        // 判断菜品是否已被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(Arrays.asList(ids));
        if (!CollectionUtils.isEmpty(setmealIds)) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 删除菜品数据和菜品关联的口味数据
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 根据ID查询菜品信息
     *
     * @param id 菜品ID
     * @return 菜品信息
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        DishVO dishVO = new DishVO();
        if (dish != null) {
            BeanUtils.copyProperties(dish, dishVO);
            dishVO.setFlavors(dishFlavorMapper.getByDishId(id));
        }
        return dishVO;
    }

    /**
     * 更新菜品以及口味
     *
     * @param dishDTO 菜品修改请求数据
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        // 修改菜品表基本信息
        dishMapper.update(dish);
        // 删除原有口味数据 + 增加新口味数据
        dishFlavorMapper.deleteByDishId(dish.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (!CollectionUtils.isEmpty(flavors)) {
            flavors.forEach(flavor -> flavor.setDishId(dish.getId()));
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 启售/停售菜品
     *
     * @param status 菜品状态
     * @param id     菜品ID
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 检查状态值是否非法
        if (!StatusConstant.ENABLE.equals(status) && !StatusConstant.DISABLE.equals(status)) {
            throw new StatusSwitchException(MessageConstant.STATUS_IS_INVALID);
        }
        // 检查当前菜品是否关联正在启售中的套餐
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(Collections.singletonList(id));
        Integer enableCount = setmealMapper.checkIfExistEnable(setmealIds);
        if (enableCount > 0) {
            throw new StatusSwitchException(MessageConstant.DISH_BE_RELATED_BY_ENABLE_SETMEAL);
        }
        // 修改菜品售卖状态
        dishMapper.update(Dish.builder().id(id).status(status).build());
    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId 分类ID
     */
    @Override
    public List<Dish> list(Long categoryId) {
        return dishMapper.getListById(categoryId);
    }

    /**
     * 条件查询菜品及对应口味
     *
     * @param dish 条件查询请求数据
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        // 查询出所有的实体数据
        List<Dish> dishList = dishMapper.list(dish);
        // 封装成响应结果数据并返回
        return dishList.stream()
                .map(dishEntity -> {
                    DishVO dishVO = new DishVO();
                    BeanUtils.copyProperties(dishEntity, dishVO);
                    // 根据菜品ID查询对应的口味
                    List<DishFlavor> flavors = dishFlavorMapper.getByDishId(dishEntity.getId());
                    dishVO.setFlavors(flavors);
                    return dishVO;
                })
                .toList();
    }
}
