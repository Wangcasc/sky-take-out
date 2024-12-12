package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Api(tags = "C端用户相关接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信用户登录{}", userLoginDTO.getCode());
        //微信登录
        User user =userService.wxLogin(userLoginDTO);
        //为用户生成jwt令牌
        //登录成功后，生成jwt令牌
        //使用了jwt工具类JwtUtil生成jwt令牌，需要引入jwt依赖jwtProperties
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId()); //向jwt令牌中添加用户id键值对 唯一标识
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(), //jwt秘钥
                jwtProperties.getUserTtl(),    //jwt过期时间
                claims);

        UserLoginVO userLoginVO=new UserLoginVO();
        //BeanUtils.copyProperties(user,userLoginVO);
        userLoginVO.setId(user.getId());
        userLoginVO.setOpenid(user.getOpenid());
        userLoginVO.setToken(token);

        return Result.success(userLoginVO);
    }
}
