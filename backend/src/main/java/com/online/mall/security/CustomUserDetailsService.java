package com.online.mall.security;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.online.mall.entity.Role;
import com.online.mall.entity.User;
import com.online.mall.service.RoleService;
import com.online.mall.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自定义用户详情服务，实现Spring Security UserDetailsService
 */
@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userService.getOne(queryWrapper);

        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在");
        }

        // 检查用户是否被删除
        if (user.getDeleted() != null && user.getDeleted() == 1) {
            log.error("用户已被删除: {}", username);
            throw new UsernameNotFoundException("用户已被删除");
        }

        // 从数据库加载用户角色
        List<Role> roles = roleService.getRolesByUserId(user.getId());

        log.debug("加载用户成功: {}, 角色: {}", username, roles != null ? roles.size() : 0);
        return new CustomUserDetails(user, roles);
    }
}
