package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据openid查询用户
     * @param openid openid授权码
     * @return 用户
     */
    @Select("select * from user where openid=#{openid}")
    User getByOpenid(String openid);

    /**
     * @param user 用户
     */
    //由于要获取用户唯一标识id，所以需要使用动态sql中的参数
    void insert(User user);

    @Select("select * from user where id =#{id}")
    User getById(Long userId);
}
