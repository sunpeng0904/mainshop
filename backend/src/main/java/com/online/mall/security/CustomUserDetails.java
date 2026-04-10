package com.online.mall.security;

import com.online.mall.entity.Role;
import com.online.mall.entity.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 自定义用户详情，实现Spring Security UserDetails接口
 */
@Data
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final List<Role> roles;

    public CustomUserDetails(User user, List<Role> roles) {
        this.user = user;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getStatus() != null && user.getStatus() == 0) {
            // 禁用用户无权限
            return new ArrayList<>();
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 从数据库加载的角色
        if (roles != null && !roles.isEmpty()) {
            for (Role role : roles) {
                authorities.add(new SimpleGrantedAuthority(role.getRoleCode()));
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 用户状态为1表示正常
        return user.getStatus() != null && user.getStatus() == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() != null && user.getStatus() == 1;
    }

    public Long getId() {
        return user.getId();
    }

    /**
     * 获取角色编码列表
     */
    public List<String> getRoleCodes() {
        List<String> roleCodes = new ArrayList<>();
        if (roles != null) {
            for (Role role : roles) {
                roleCodes.add(role.getRoleCode());
            }
        }
        return roleCodes;
    }

    /**
     * 判断是否为管理员
     */
    public boolean isAdmin() {
        if (roles != null) {
            for (Role role : roles) {
                if ("ROLE_ADMIN".equals(role.getRoleCode())) {
                    return true;
                }
            }
        }
        return false;
    }
}
