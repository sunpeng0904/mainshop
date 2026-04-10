package com.online.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.Role;
import com.online.mall.vo.RoleVO;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {

    /**
     * 获取所有启用的角色
     */
    List<RoleVO> getAllRoles();

    /**
     * 根据用户ID获取角色
     */
    List<Role> getRolesByUserId(Long userId);

    /**
     * 创建角色
     */
    RoleVO createRole(String roleCode, String roleName, String description, Integer sort);

    /**
     * 更新角色
     */
    RoleVO updateRole(Long roleId, String roleName, String description, Integer sort, Integer status);

    /**
     * 删除角色
     */
    void deleteRole(Long roleId);

    /**
     * 给用户分配角色
     */
    void assignRoleToUser(Long userId, Long roleId);

    /**
     * 移除用户角色
     */
    void removeRoleFromUser(Long userId, Long roleId);

    /**
     * 设置用户角色（替换原有角色）
     */
    void setUserRoles(Long userId, List<Long> roleIds);

    /**
     * 检查角色编码是否存在
     */
    boolean checkRoleCodeExists(String roleCode);
}
