package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.BaseException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO 登录信息
     * @return Employee 员工信息
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes()); //对密码进行md5加密
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus().equals(StatusConstant.DISABLE)) { //判断账号是否启用
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {
        //解析DTO对象
        ////判断用户名是否存在
        // Employee employee = employeeMapper.getByUsername(employeeDTO.getUsername());
        //if (employee != null) {
        //    //用户名已存在
        //    throw new BaseException("用户名已存在");
        //}
        //改为了在全局异常处理器中处理

        //判断手机号是11位合法号码
        if (employeeDTO.getPhone().length() != 11) {
            throw new BaseException("手机号不合法");
        }
        //判断身份证号是18位合法号码
        if (employeeDTO.getIdNumber().length() != 18) {
            throw new BaseException("身份证号不合法");
        }

        //新建要记录的员工对象
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee); //将DTO对象的属性拷贝到实体对象中 前提是属性名相同
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes())); //默认密码123456 MD5加密
        employee.setStatus(StatusConstant.ENABLE); //默认启用
        //添加额外操作信息
        employee.setCreateTime(LocalDateTime.now()); //创建时间
        employee.setUpdateTime(LocalDateTime.now()); //更新时间
        //创建人 从ThreadLocal中获取
        employee.setCreateUser(BaseContext.getCurrentId());
        //更新人 从ThreadLocal中获取
        employee.setUpdateUser(BaseContext.getCurrentId());

        //调用DAO层方法插入数据
        employeeMapper.insert(employee);

    }

    /**
     * 分页查询员工
     * @param employeePageQueryDTO 请求参数
     * @return PageResult 分页结果
     */
    @Override
    public PageResult pageEmployee(EmployeePageQueryDTO employeePageQueryDTO) {
        //使用PageHelper插件进行分页查询
        //设置分页参数
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        //调用DAO层方法查询数据
        List<Employee> employeeList = employeeMapper.pageEmployee(employeePageQueryDTO);

        //把密码都置空,不返回密码,保护用户隐私,这样在前端调试界面也看不到密码
        for (Employee employee : employeeList) {
            employee.setPassword("********");
        }


        Page<Employee> page = (Page<Employee>) employeeList; //强转为Page类型
        //将查询结果封装到PageResult对象中
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());

        return pageResult;
    }

    /**
     * 启用或停用员工
     * @param status 启用或停用
     * @param id    员工ID
     */
    @Override
    public void startOrStopEmployee(Integer status, Long id) {
        //调用DAO层方法更新数据 编写一个通用update方法 方便以后扩展
        Employee employee = new Employee();
        employee.setId(id);
        employee.setStatus(status);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);
    }

    @Override
    public Employee getByUserId(Long id) {

        Employee employee = employeeMapper.getByUserId(id);
        //把密码都置空,不返回密码,保护用户隐私,这样在前端调试界面也看不到密码
        employee.setPassword("********");

        return employee;
    }

    @Override
    public void updateEmployee(EmployeeDTO employeeDTO) {
        //判断手机号是11位合法号码
        if (employeeDTO.getPhone().length() != 11) {
            throw new BaseException("手机号不合法");
        }
        //判断身份证号是18位合法号码
        if (employeeDTO.getIdNumber().length() != 18) {
            throw new BaseException("身份证号不合法");
        }

        //新建要记录的员工对象
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee); //将DTO对象的属性拷贝到实体对象中 前提是属性名相同
        //添加额外操作信息
        employee.setUpdateTime(LocalDateTime.now()); //更新时间
        //更新人 从ThreadLocal中获取
        employee.setUpdateUser(BaseContext.getCurrentId());

        //调用DAO层方法插入数据
        employeeMapper.update(employee);
    }


}
