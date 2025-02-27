package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DishServiceImpl implements DishService {

    private static final Logger log = LoggerFactory.getLogger(DishServiceImpl.class);
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
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
     * @param dishPageQueryDTO  分页查询条件
     * @return 返回分页查询结果
     */
    @Override
    public PageResult pageDish(DishPageQueryDTO dishPageQueryDTO) {
        //PageHelper
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> dishPage = dishMapper.pageDish(dishPageQueryDTO);
        PageResult pageResult = new PageResult();
        pageResult.setTotal(dishPage.getTotal());
        pageResult.setRecords(dishPage.getResult());
        return pageResult;
    }

    /**
     * 删除菜品 批量删除
     * @param ids  菜品id
     */
    @Override
    @Transactional //事务
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品状态是否是上架状态 如果是上架状态不能删除
        for (Long id : ids) {
            Dish dish= dishMapper.getById(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException("菜品已上架，不能删除");
            }
        }

        //判断当前菜品是否被套餐关联 如果被套餐关联不能删除
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException("菜品已被套餐关联，不能删除");
        }

        //删除菜品
        //for (Long id : ids) {
        //    dishMapper.deleteById(id);
        //    //删除菜品口味信息
        //    dishFlavorMapper.deleteByDishId(id);
        //}

        //修改为批量删除
        // 批量删除菜品和对应的口味信息
        dishMapper.deleteBatch(ids);
        dishFlavorMapper.deleteByDishIds(ids);

    }

    @Override
    public DishVO getDishByIdWithFlavor(Long id) {
        //1、根据id查询菜品信息
        Dish dish = dishMapper.getById(id);
        //2、根据id查询菜品口味信息
        List<DishFlavor> dishFlavorList = dishFlavorMapper.getByDishId(id);
        //3、将菜品信息和菜品口味信息封装到VO对象中
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavorList);
        return dishVO;
    }


    /**
     * 更新菜品信息
     * @param dishDTO 菜品信息 更新菜品信息
     */
    @Override
    public void updateDish(DishDTO dishDTO) {
        //1、将DTO转换为实体对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //2、更新菜品信息
        dishMapper.update(dish);
        //3、删除菜品口味信息
        dishFlavorMapper.deleteByDishId(dish.getId());
        //4、插入菜品口味信息
        List<DishFlavor> dishFlavorList = dishDTO.getFlavors();
        if (dishFlavorList != null && !dishFlavorList.isEmpty()) {
            //为菜品口味信息设置菜品id
            for (DishFlavor dishFlavor : dishFlavorList) {
                dishFlavor.setDishId(dish.getId()); //设置菜品id
            }
            dishFlavorMapper.insertBatch(dishFlavorList); //批量插入菜品口味信息
        }

    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    @Override
    public void setDishStatus(Long dishId, Integer status) {
        Dish dish = new Dish();
        dish.setId(dishId);
        dish.setStatus(status);
        dishMapper.update(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
