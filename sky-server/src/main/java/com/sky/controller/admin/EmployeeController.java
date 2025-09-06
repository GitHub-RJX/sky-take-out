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
@Api(tags = "员工管理")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO 员工登录请求数据
     * @return 员工登录响应数据
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
//        log.info("员工登录：{}", employeeLoginDTO);
        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();
        return Result.success(employeeLoginVO);
    }

    /**
     * 新增员工
     *
     * @param employeeDTO 员工新增请求数据
     * @return 新增请求响应结果
     */
    @PostMapping
    @ApiOperation(value = "新增员工")
    public Result<Void> save(@RequestBody EmployeeDTO employeeDTO) {
//        log.info("新增员工{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 分页查询员工数据
     *
     * @param employeePageQueryDTO 员工分页查询请求数据
     * @return 员工分页查询响应数据
     */
    @GetMapping("/page")
    @ApiOperation(value = "员工分页查询")
    public Result<PageResult<Employee>> page(EmployeePageQueryDTO employeePageQueryDTO) {
//        log.info("分页查询{}", employeePageQueryDTO);
        PageResult<Employee> result = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(result);
    }

    /**
     * 启用/禁用员工状态
     *
     * @param status 员工状态
     * @param id     员工ID
     * @return 响应结果
     */
    @GetMapping("/status/{status}")
    @ApiOperation(value = "员工状态禁用启用")
    public Result<Void> startOrStop(@PathVariable("status") Integer status, Long id) {
//        log.info("启用禁用员工状态{}", status);
        employeeService.startOrStop(status, id);
        return Result.success();
    }

    /**
     * 根据ID查询员工信息
     *
     * @param id 员工ID
     * @return 员工实体数据
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID查询员工信息")
    public Result<Employee> getById(@PathVariable("id") Long id) {
//        log.info("根据ID查询员工信息{}", id);
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 修改员工
     *
     * @param employeeDTO 员工修改请求数据
     * @return 员工修改响应结果
     */
    @PutMapping
    @ApiOperation(value = "修改员工信息")
    public Result<Void> update(@RequestBody EmployeeDTO employeeDTO) {
//        log.info("修改员工信息{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }

    /**
     * 员工退出登录
     *
     * @return 员工退出登录响应结果
     */
    @PostMapping("/logout")
    @ApiOperation(value = "员工退出")
    public Result<String> logout() {
        return Result.success();
    }
}
