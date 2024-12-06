package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
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
     * @param employeeLoginDTO 登录信息
     * @return 员工登录信息
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
     * @return 退出成功
     */
    @PostMapping("/logout")
    @ApiOperation("员工退出") //swagger接口文档的说明
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 添加员工
     *
     * @param employeeDTO 新增员工信息
     * @return 添加成功
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
     * @param employeePageQueryDTO 查询条件
     * @return 员工分页信息
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
     * @param status 启用或停用
     * @param id 员工ID
     * @return 启用或停用成功
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用或停用员工") //swagger接口文档的说明
    public Result startOrStopEmployee(@PathVariable Integer status,  Long id) {
        log.info("启用或停用员工：status={}, id={}", status, id);

        //调用service层的startOrStopEmployee方法 会更新数据库
        employeeService.startOrStopEmployee(status, id);

        return Result.success();
    }

    /**
     * 根据员工id查询员工信息
     * @param id 员工id
     * @return 员工信息
     */
    @GetMapping("/{id}")
    @ApiOperation("根据员工id查询员工信息") //swagger接口文档的说明
    public Result<Employee> getByUserId(@PathVariable Long id) {
        log.info("根据员工id查询员工信息：id={}", id);

        //调用service层的getByUserId方法 会查询数据库
        Employee employee = employeeService.getByUserId(id);

        return Result.success(employee);
    }

    /**
     * 更新员工
     * @param employeeDTO 员工信息
     * @return 更新成功
     */
    @PutMapping
    @ApiOperation("更新员工") //swagger接口文档的说明
    public Result updateEmployee(@RequestBody EmployeeDTO employeeDTO) {
        log.info("更新员工：{}", employeeDTO);

        //调用service层的updateEmployee方法 会更新数据库
        employeeService.updateEmployee(employeeDTO);

        return Result.success();
    }

    /**
     * 修改密码
     * @param passwordEditDTO 密码信息
     * @return 修改成功
     */
    @PutMapping("/editPassword")
    public Result changePassword(@RequestBody PasswordEditDTO passwordEditDTO) {
        log.info("修改密码：{}", passwordEditDTO); //前端没有给传递员工id，所以需要从token中获得员工id 但这样只能修改自己的密码

        //调用service层的changePassword方法 会更新数据库
        employeeService.changePassword(passwordEditDTO);

        return Result.success();

    }

}
