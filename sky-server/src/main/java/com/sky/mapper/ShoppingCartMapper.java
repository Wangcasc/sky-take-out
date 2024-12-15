package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 列出购物车中的符合查询条件的项
     * @param shoppingCart  the shopping cart
     * @return the shopping cart list
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 更新购物车中某项的数量
     * @param cart  the shopping cart
     */
    @Update("UPDATE shopping_cart SET number =#{number} WHERE id = #{id}")
    void update(ShoppingCart cart);

    /**
     * 插入新的项到购物车中
     * @param shoppingCart  the shopping cart
     */
    @Insert("INSERT INTO shopping_cart(name, image, user_id, dish_id, setmeal_id, dish_flavor,number, amount, create_time) " +
            "VALUES (#{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    void insert(ShoppingCart shoppingCart);


    /**
     * 删除特定数据
     * @param shoppingCart  the shopping cart
     */
    void delete(ShoppingCart shoppingCart);
}
