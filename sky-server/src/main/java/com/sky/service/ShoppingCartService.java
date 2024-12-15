package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 加入购物车
     * @param shoppingCartDTO  加入购物车的菜品
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查询购物车列表
     * @return  购物车单元
     */
    List<ShoppingCart> list();

    /**
     * 删掉菜品
     * @param shoppingCartDTO  shoppingCart
     */
    void sub(ShoppingCartDTO shoppingCartDTO);

    /**
     * 清空购物车
     */
    void clean();
}
