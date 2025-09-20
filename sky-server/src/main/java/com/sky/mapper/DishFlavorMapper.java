package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    /**
     * 批量插入口味数据
     *
     * @param flavors 口味数据
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除对应的口味数据
     *
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据菜品id查询对应的口味数据
     *
     * @param dishId 菜品ID
     * @return 菜品关联的口味数据
     */
    @Select("SELECT * FROM dish_flavor WHERE dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

    /**
     * @param dishIds 菜品ID
     */
    void deleteByDishIds(Long[] dishIds);
}
