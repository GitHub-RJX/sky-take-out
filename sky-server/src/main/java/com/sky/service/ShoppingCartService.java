package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 添加购物车商品
     *
     * @param shoppingCartDTO 购物车添加请求数据
     */
    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     *
     * @return 购物车数据列表
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空购物车
     */
    void cleanShoppingCart();

    /**
     * 删除购物车商品
     *
     * @param shoppingCartDTO 购物车删除请求数据
     */
    void subShoppingCart(ShoppingCartDTO shoppingCartDTO);
}
