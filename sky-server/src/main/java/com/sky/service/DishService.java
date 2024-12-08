package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和口味 需要操作两张表 需要保证数据的一致性 需要使用事务
     * @param dishDTO 菜品信息
     */
    public void addDishWithFlavor(DishDTO dishDTO);

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageDish(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除菜品 批量删除
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品
     * @param id 菜品id
     * @return 菜品信息
     */
    DishVO getDishByIdWithFlavor(Long id);

    /**
     * 更新菜品信息
     * @param dishDTO 菜品信息
     */
    void updateDish(DishDTO dishDTO);


}
