package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("userShopController") //取一个别名 避免和admin的ShopController冲突
@RequestMapping("/user/shop") //用户端就是user开头 项目约定
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    private static final String SHOP_STATUS = "shop_status";

    @Autowired
    private RedisTemplate redisTemplate; //注入redisTemplate

    /**
     * 获取店铺状态
     * @return 返回结果
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺状态")
    public Result getStatus() {
        log.info("获取店铺状态");
        //使用RedisTemplate操作redis来获取店铺状态
        Object status = redisTemplate.opsForValue().get(SHOP_STATUS); //获取key的value
        //转为Integer类型
        status = Integer.valueOf(status.toString()); //将Object类型转为Integer类型
        log.info("status: {}", status);

        return Result.success(status);
    }
}
