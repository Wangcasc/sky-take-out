package com.sky.controller.admin;

import com.github.pagehelper.Page;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate; //注入redisTemplate 用于保持缓存和后端的数据同步

    /**
     * 新增菜品
     * @param dishDTO 菜品信息
     * @return 返回成功
     */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result addDish(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.addDishWithFlavor(dishDTO); //调用新增菜品的方法
        return Result.success();
    }

    /**
     * 分页查询菜品
     * @param dishPageQueryDTO 分页查询条件
     * @return 返回分页查询结果
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> pageDish(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询菜品：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageDish(dishPageQueryDTO); //调用分页查询菜品的方法
        return Result.success(pageResult);
    }

    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result deleteDish(@RequestParam List<Long> ids) {
        log.info("删除菜品：{}", ids);
        dishService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getDishById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getDishByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("更新菜品")
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        log.info("更新菜品：{}", dishDTO);
        dishService.updateDish(dishDTO);
        cleanRedisCache("user_dish_list_*"); //清除redis中user_dish_list_* 相关的缓存
        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    @PutMapping("/status/{status}")
    @ApiOperation("修改菜品状态")
    public Result dishStatus(Long dishId, @PathVariable Integer status) {
        dishService.setDishStatus(dishId, status);
        return Result.success();
    }


    private void cleanRedisCache(String pattern) {
        redisTemplate.delete(redisTemplate.keys(pattern));
        log.info("清除redis缓存：{}", pattern);
    }

}
