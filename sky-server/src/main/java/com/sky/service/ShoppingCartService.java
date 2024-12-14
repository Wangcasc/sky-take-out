package com.sky.service;

import com.sky.dto.ShoppingCartDTO;

public interface ShoppingCartService {

    /**
     * 加入购物车
     * @param shoppingCartDTO  加入购物车的菜品
     */
    void add(ShoppingCartDTO shoppingCartDTO);

}
