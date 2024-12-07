package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * 新增菜品和口味 需要操作两张表 需要保证数据的一致性 需要使用事务
     * @param dishDTO 菜品信息
     */
    @Transactional //开启事务
    public void addDishWithFlavor(DishDTO dishDTO) {
        //1、将DTO转换为实体对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //2、插入一条菜品信息
        dishMapper.insert(dish);
        //3、插入菜品口味信息
        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        if (dishFlavorList != null && !dishFlavorList.isEmpty()) {
            //为菜品口味信息设置菜品id
            for (DishFlavor dishFlavor : dishFlavorList) {
                dishFlavor.setDishId(dish.getId()); //设置菜品id
                //这个id不是前端传过来的，而是数据库自动生成的
                //在xml文件中配置了useGeneratedKeys="true" keyProperty="id"
            }
            dishFlavorMapper.insertBatch(dishFlavorList); //批量插入菜品口味信息
        }

    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageDish(DishPageQueryDTO dishPageQueryDTO) {
        //PageHelper
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<Dish> dishPage = dishMapper.pageDish(dishPageQueryDTO);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(dishPage.getTotal());
        pageResult.setRecords(dishPage.getResult());
        return pageResult;
    }
}
