package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO 登录信息
     * @return Employee
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 添加员工
     * @param employeeDTO 获得的员工信息
     */
    void addEmployee(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工
     * @param employeePageQueryDTO 查询条件
     * @return 员工分页信息
     */
    PageResult pageEmployee(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 启用或停用员工
     * @param status 状态
     * @param id 员工id
     */
    void startOrStopEmployee(Integer status, Long id);

    /**
     * 根据员工id查询员工信息
     * @param id 员工id
     * @return 员工信息
     */
    Employee getByUserId(Long id);

    /**
     * @param employeeDTO 员工信息
     */
    void updateEmployee(EmployeeDTO employeeDTO);

    /**
     * @param passwordEditDTO 密码修改信息
     */
    void changePassword(PasswordEditDTO passwordEditDTO);
}
