package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.RequestParamInvalidException;
import com.sky.exception.StatusSwitchException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 套餐业务实现
 */
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDTO 套餐新增请求数据
     */
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        // 新增套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        // 保存套餐与菜品的关联关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
        setmealDishMapper.insertBatch(setmealDishes);
//        log.info("套餐和菜品的关联关系保存成功");
//        log.info("套餐保存成功");
    }

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO 分页查询请求数据
     * @return 分页查询响应数据
     */
    @Override
    public PageResult<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
//        log.info("分页查询结果：{}", page);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    /**
     * 批量删除套餐
     *
     * @param ids 套餐ID列表
     */
    @Override
    public void deleteBatch(List<Long> ids) {
        // 检查请求参数的合法性
        if (CollectionUtils.isEmpty(ids)) {
            throw new RequestParamInvalidException(MessageConstant.REQUEST_PARAM_IS_NULL_OR_EMPTY);
        }
        // 批量删除套餐
        setmealMapper.deleteByIds(ids);
        // 删除套餐关联的菜品数据
        setmealDishMapper.deleteBySetmealIds(ids);
    }

    /**
     * 根据id查询套餐和关联的菜品数据
     *
     * @param id 套餐ID
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        SetmealVO setmealVO = setmealMapper.getByIdWithDish(id);
        return setmealVO == null ? new SetmealVO() : setmealVO;
    }

    /**
     * 修改套餐
     *
     * @param setmealDTO 套餐修改请求数据
     */
    @Override
    public void update(SetmealDTO setmealDTO) {
        // 修改套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        // 删除套餐原本关联菜品的关联关系
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        // 保存套餐与菜品的关联关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmeal.getId()));
        setmealDishMapper.insertBatch(setmealDishes);
    }

    /**
     * 套餐启售、停售
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        setmealMapper.startOrStop(status, id);
    }

    /**
     * 条件查询
     *
     * @param setmeal 条件查询请求数据
     * @return 条件查询响应数据
     */
    public List<Setmeal> list(Setmeal setmeal) {
        return setmealMapper.list(setmeal);
    }

    /**
     * 根据id查询菜品选项
     *
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }

    @Override
    public SetmealVO getById(Long id) {
        return setmealMapper.getByIdWithDish(id);
    }
}
