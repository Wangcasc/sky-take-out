package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    private static final String SHOP_STATUS = "shop_status";

    @Autowired
    private RedisTemplate redisTemplate; //注入redisTemplate
    /**
     * 设置店铺状态
     * @param status 状态
     * @return 返回结果
     */
    @RequestMapping("/{status}")
    @ApiOperation("设置店铺状态")
    public Result setStatus(@PathVariable Integer status) {
        log.info("设置店铺状态, status: {}", status);
        //使用RedisTemplate操作redis来设置店铺状态
        redisTemplate.opsForValue().set(SHOP_STATUS, status);

        return Result.success();
    }

    /**
     * 获取店铺状态
     * @return 返回结果
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result getStatus() {
        log.info("获取店铺状态");
        //使用RedisTemplate操作redis来获取店铺状态
        Object status = redisTemplate.opsForValue().get(SHOP_STATUS);
        //转为Integer类型
        status = Integer.valueOf(status.toString());
        log.info("status: {}", status);

        return Result.success(status);
    }
}
