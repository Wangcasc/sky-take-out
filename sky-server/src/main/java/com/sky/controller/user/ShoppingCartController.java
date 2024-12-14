package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api("购物车管理")
@RequestMapping("/user/shoppingCart")
//@ApiIgnore // 忽略swagger文档生成，只在controller上添加@ApiIgnore注解，不在父类上添加
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    @ApiOperation("添加到购物车" )
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加到购物车：{}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);

        return Result.success();
    }
}
