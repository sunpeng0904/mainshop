package com.online.mall.controller.admin;

import com.online.mall.common.Result;
import com.online.mall.dto.RoleCreateDTO;
import com.online.mall.dto.UserRoleUpdateDTO;
import com.online.mall.service.RoleService;
import com.online.mall.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理员角色控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/role")
@Tag(name = "管理员-角色管理", description = "管理员角色相关接口")
public class AdminRoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "获取所有角色")
    @GetMapping("/list")
    public Result<List<RoleVO>> getAllRoles() {
        List<RoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    @Operation(summary = "创建角色")
    @PostMapping("/create")
    public Result<RoleVO> createRole(@Valid @RequestBody RoleCreateDTO dto) {
        RoleVO role = roleService.createRole(dto.getRoleCode(), dto.getRoleName(), dto.getDescription(), dto.getSort());
        return Result.success(role, "角色创建成功");
    }

    @Operation(summary = "更新角色")
    @PutMapping("/{roleId}")
    public Result<RoleVO> updateRole(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @RequestBody RoleCreateDTO dto) {
        RoleVO role = roleService.updateRole(roleId, dto.getRoleName(), dto.getDescription(), dto.getSort(), dto.getStatus());
        return Result.success(role, "角色更新成功");
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{roleId}")
    public Result<Void> deleteRole(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return Result.success(null, "角色删除成功");
    }

    @Operation(summary = "给用户分配角色")
    @PostMapping("/assign/{userId}/{roleId}")
    public Result<Void> assignRoleToUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        roleService.assignRoleToUser(userId, roleId);
        return Result.success(null, "角色分配成功");
    }

    @Operation(summary = "移除用户角色")
    @DeleteMapping("/remove/{userId}/{roleId}")
    public Result<Void> removeRoleFromUser(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        roleService.removeRoleFromUser(userId, roleId);
        return Result.success(null, "角色移除成功");
    }

    @Operation(summary = "设置用户角色")
    @PutMapping("/user/{userId}")
    public Result<Void> setUserRoles(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Valid @RequestBody UserRoleUpdateDTO dto) {
        roleService.setUserRoles(userId, dto.getRoleIds());
        return Result.success(null, "用户角色设置成功");
    }
}
