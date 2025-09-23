package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.ShoppingCart;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 动态条件查询
     *
     * @param shoppingCart 购物车查询请求数据
     * @return 购物车查询响应数据
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据id修改商品数量
     *
     * @param shoppingCart 购物车修改请求数据
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    @AutoFill(value = OperationType.UPDATE)
    void updateNumberById(ShoppingCart shoppingCart);

    /**
     * 插入购物车数据
     *
     * @param shoppingCart 购物车插入请求数据
     */
    @Insert("insert into shopping_cart (name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, image, create_time) " +
            " values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
    @AutoFill(value = OperationType.INSERT)
    void insert(ShoppingCart shoppingCart);

    /**
     * 根据用户id删除购物车数据
     *
     * @param userId 当前用户ID
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);

    /**
     * 根据id删除购物车数据
     *
     * @param id 购物车ID
     */
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(Long id);

    /**
     * 批量插入购物车数据
     *
     * @param shoppingCartList 购物车批量插入请求数据
     */
    void insertBatch(List<ShoppingCart> shoppingCartList);
}
