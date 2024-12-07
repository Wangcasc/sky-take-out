package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    /**
     * 批量插入菜品口味信息
     * @param dishFlavorList 菜品口味信息
     */
    void insertBatch(List<DishFlavor> dishFlavorList);
}
