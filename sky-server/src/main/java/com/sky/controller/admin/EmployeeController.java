package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口") //swagger接口文档的分组 用于说明接口
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
    @ApiOperation("员工登录") //swagger接口文档的说明
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
    @ApiOperation("员工退出") //swagger接口文档的说明
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 添加员工
     *
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation("添加员工") //swagger接口文档的说明
    public Result addEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("添加员工：{}", employeeDTO);

        //调用service层的addEmployee方法 会插入数据库
        employeeService.addEmployee(employeeDTO);

        return Result.success();
    }

    /**
     * 分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询员工") //swagger接口文档的说明
    public Result<PageResult> pageEmployee( EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("分页查询员工：{}", employeePageQueryDTO);

        //调用service层的pageEmployee方法 会查询数据库
        PageResult pageResult = employeeService.pageEmployee(employeePageQueryDTO);

        return Result.success(pageResult);

    }

    /**
     * 启用或停用员工
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用或停用员工") //swagger接口文档的说明
    public Result startOrStopEmployee(@PathVariable Integer status,  Long id) {
        log.info("启用或停用员工：status={}, id={}", status, id);

        //调用service层的startOrStopEmployee方法 会更新数据库
        employeeService.startOrStopEmployee(status, id);

        return Result.success();
    }

}
