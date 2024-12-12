package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //用授权码获得用户的openid
        Map<String,String> map=new HashMap<>(); //请求参数
        map.put("appid",weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type","authentication_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);//基于HttpClient封装好的HttpClientUtil

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        //判断openid是否获取成功
        if (openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断当前用户对于后端是否是新用户 查询
        User user=userMapper.getByOpenid(openid);

        //如果是新用户 注册
        if (user==null){
            user=new User();
            user.setOpenid(openid);
            user.setCreateTime(LocalDateTime.now()); //和管理端不一样 不使用自动填充
            userMapper.insert(user);
        }

        //返回用户对象
        return user;
    }
}
