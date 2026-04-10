package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.common.BusinessException;
import com.online.mall.entity.Role;
import com.online.mall.entity.UserRole;
import com.online.mall.mapper.RoleMapper;
import com.online.mall.mapper.UserRoleMapper;
import com.online.mall.service.RoleService;
import com.online.mall.vo.RoleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色服务实现
 */
@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Override
    public List<RoleVO> getAllRoles() {
        List<Role> roles = list(new QueryWrapper<Role>()
                .eq("deleted", 0)
                .orderByAsc("sort"));

        List<RoleVO> voList = new ArrayList<>();
        for (Role role : roles) {
            voList.add(convertToVO(role));
        }
        return voList;
    }

    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public RoleVO createRole(String roleCode, String roleName, String description, Integer sort) {
        // 检查角色编码是否已存在
        if (checkRoleCodeExists(roleCode)) {
            throw new BusinessException("角色编码已存在");
        }

        Role role = new Role();
        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        role.setDescription(description);
        role.setSort(sort != null ? sort : 0);
        role.setStatus(1);

        save(role);
        log.info("创建角色成功: {}", roleCode);

        return convertToVO(role);
    }

    @Override
    @Transactional
    public RoleVO updateRole(Long roleId, String roleName, String description, Integer sort, Integer status) {
        Role role = getById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        if (roleName != null) {
            role.setRoleName(roleName);
        }
        if (description != null) {
            role.setDescription(description);
        }
        if (sort != null) {
            role.setSort(sort);
        }
        if (status != null) {
            role.setStatus(status);
        }

        updateById(role);
        log.info("更新角色成功: {}", roleId);

        return convertToVO(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        Role role = getById(roleId);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        // 删除角色
        removeById(roleId);

        // 删除用户角色关联
        userRoleMapper.delete(new QueryWrapper<UserRole>().eq("role_id", roleId));

        log.info("删除角色成功: {}", roleId);
    }

    @Override
    @Transactional
    public void assignRoleToUser(Long userId, Long roleId) {
        // 检查是否已分配
        Long count = userRoleMapper.selectCount(new QueryWrapper<UserRole>()
                .eq("user_id", userId)
                .eq("role_id", roleId));

        if (count > 0) {
            return; // 已分配，跳过
        }

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        userRoleMapper.insert(userRole);

        log.info("给用户分配角色成功: userId={}, roleId={}", userId, roleId);
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, Long roleId) {
        userRoleMapper.deleteByUserIdAndRoleId(userId, roleId);
        log.info("移除用户角色成功: userId={}, roleId={}", userId, roleId);
    }

    @Override
    @Transactional
    public void setUserRoles(Long userId, List<Long> roleIds) {
        // 删除用户原有角色
        userRoleMapper.deleteByUserId(userId);

        // 分配新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }

        log.info("设置用户角色成功: userId={}, roleIds={}", userId, roleIds);
    }

    @Override
    public boolean checkRoleCodeExists(String roleCode) {
        return count(new QueryWrapper<Role>().eq("role_code", roleCode)) > 0;
    }

    /**
     * 转换为VO
     */
    private RoleVO convertToVO(Role role) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(role, vo);
        return vo;
    }
}
