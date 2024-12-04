package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login") //访问路径
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO); //打印日志，需要引入lombok @Slf4j注解

        //调用service层的login方法 会查询数据库 如果查询到了就返回employee对象 如果没有查询到就会抛出异常
        //异常会被全局异常处理器捕获 参见GlobalExceptionHandler
        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        //使用了jwt工具类JwtUtil生成jwt令牌，需要引入jwt依赖jwtProperties
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId()); //向jwt令牌中添加员工id键值对
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(), //jwt秘钥
                jwtProperties.getAdminTtl(),    //jwt过期时间
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder() //返回给前端的数据，使用了lombok的@Builder注解构建对象
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO); //返回给前端的数据
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

}
