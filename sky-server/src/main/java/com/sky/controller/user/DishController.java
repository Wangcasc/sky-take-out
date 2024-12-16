package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags = "C端-菜品浏览接口")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate; // 注入redisTemplate

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId the category id
     * @return the list of dishes with flavor info, or an error message if the category id is invalid or the dishes are not found in the database.
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("根据分类id查询菜品");
        //查询redis中是否有数据
        String redisKey = "user_dish_list_" + categoryId;
        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(redisKey);

        //如果有，直接返回redis中的数据
        if (dishVOList!= null &&!dishVOList.isEmpty()) {
            log.info("Get dishes from redis: {}", redisKey);
            return Result.success(dishVOList);
        }

        //如果没有，查询mysql中数据，并将数据存入redis
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品

        List<DishVO> list = dishService.listWithFlavor(dish);

        //将查询结果存入redis
        redisTemplate.opsForValue().set(redisKey, list); // 60分钟过期
        log.info("Get dishes from mysql and set to redis: {}", redisKey);

        return Result.success(list);
    }

}
