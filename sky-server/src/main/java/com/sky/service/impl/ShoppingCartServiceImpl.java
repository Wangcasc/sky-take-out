package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //判断当前添加的商品是否在购物车中，如果在，数量加1，如果不在，直接添加
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        //UserId
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        if (shoppingCartList!=null && !shoppingCartList.isEmpty()){
            shoppingCart = shoppingCartList.get(0); //严格查询只会有一个结果 因为一个人名下重复的会被过滤掉只在数量上加1
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.update(shoppingCart);
        }
        else {
            // 如果不存在，查询缺失信息后插入
            //如果是菜品，查询菜品的名称，价格和图片
            if(shoppingCartDTO.getDishId()!=null){
                //查询菜品名称，图片和价格
                Dish dish = dishMapper.getById(shoppingCartDTO.getDishId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());
             }
            //如果是套餐，查询套餐的名称，图片和描述
            else if (shoppingCartDTO.getSetmealId()!=null){
                //查询套餐名称，图片和描述
                Setmeal setmeal = setmealMapper.getById(shoppingCartDTO.getSetmealId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());

            }

            shoppingCartMapper.insert(shoppingCart);

        }



    }

    @Override
    public List<ShoppingCart> list() {

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(userId);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.list(shoppingCart);
        return shoppingCartList;
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);

        //UserId
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        //查询
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        if(list != null && !list.isEmpty()){
            shoppingCart = list.get(0); //严格查询，只会有一条

            Integer number = shoppingCart.getNumber();
            if(number == 1){
                //当前商品在购物车中的份数为1，直接删除当前记录
                shoppingCartMapper.delete(shoppingCart);
            }else {
                //当前商品在购物车中的份数不为1，修改份数即可
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                //shoppingCart.setCreateTime(LocalDateTime.now());
                shoppingCartMapper.update(shoppingCart);
            }
        }
    }
}
