package com.online.mall.security;

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

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getStatus() != null && user.getStatus() == 0) {
            // 禁用用户
            return new ArrayList<>();
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        // 所有正常用户都有 USER 角色
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // admin 用户额外拥有 ADMIN 角色
        if ("admin".equals(user.getUsername())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
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
}