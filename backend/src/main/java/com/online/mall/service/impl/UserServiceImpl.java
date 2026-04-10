package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.common.BusinessException;
import com.online.mall.dto.UserLoginDTO;
import com.online.mall.dto.UserRegisterDTO;
import com.online.mall.dto.UserUpdateDTO;
import com.online.mall.entity.User;
import com.online.mall.mapper.UserMapper;
import com.online.mall.service.UserService;
import com.online.mall.utils.JwtTokenUtil;
import com.online.mall.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;
    
    @Override
    @Transactional
    public UserVO register(UserRegisterDTO registerDTO) {
        // 验证密码
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("user.password.mismatch");
        }

        // 检查用户名是否已存在
        if (checkUsernameExists(registerDTO.getUsername())) {
            throw new BusinessException("user.username.exists");
        }

        // 检查邮箱是否已存在
        if (registerDTO.getEmail() != null && checkEmailExists(registerDTO.getEmail())) {
            throw new BusinessException("user.email.exists");
        }

        // 检查手机号是否已存在
        if (registerDTO.getPhone() != null && checkPhoneExists(registerDTO.getPhone())) {
            throw new BusinessException("user.phone.exists");
        }
        
        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(registerDTO, user);
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        
        // 设置默认值
        user.setStatus(1);
        user.setAvatar("https://via.placeholder.com/150");
        user.setNickname(registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername());
        
        // 保存用户
        save(user);
        
        log.info("用户注册成功: {}", user.getUsername());
        
        // 返回用户信息
        return convertToVO(user);
    }
    
    @Override
    public String login(UserLoginDTO loginDTO) {
        try {
            // 使用Spring Security进行认证
            Authentication authentication = authenticationConfiguration.getAuthenticationManager().authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 获取用户信息
            User user = getOne(new QueryWrapper<User>().eq("username", loginDTO.getUsername()));
            
            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            updateById(user);
            
            // 生成token
            String token = jwtTokenUtil.generateToken(user.getId(), user.getUsername());
            
            log.info("用户登录成功: {}", loginDTO.getUsername());
            return token;
            
        } catch (Exception e) {
            log.error("用户登录失败: {}", loginDTO.getUsername(), e);
            throw new BusinessException("user.login.failed");
        }
    }
    
    @Override
    public UserVO getUserInfo(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("user.not.found");
        }
        return convertToVO(user);
    }
    
    @Override
    @Transactional
    public UserVO updateUserInfo(Long userId, UserUpdateDTO updateDTO) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("user.not.found");
        }

        // 更新用户名
        if (updateDTO.getUsername() != null && !updateDTO.getUsername().equals(user.getUsername())) {
            if (checkUsernameExists(updateDTO.getUsername())) {
                throw new BusinessException("user.username.exists");
            }
            user.setUsername(updateDTO.getUsername());
        }

        // 更新密码
        if (updateDTO.getPassword() != null) {
            if (!updateDTO.getPassword().equals(updateDTO.getConfirmPassword())) {
                throw new BusinessException("user.password.mismatch");
            }
            user.setPassword(passwordEncoder.encode(updateDTO.getPassword()));
        }

        // 更新昵称
        if (updateDTO.getNickname() != null) {
            user.setNickname(updateDTO.getNickname());
        }

        // 更新邮箱
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (checkEmailExists(updateDTO.getEmail())) {
                throw new BusinessException("user.email.exists");
            }
            user.setEmail(updateDTO.getEmail());
        }

        // 更新手机号
        if (updateDTO.getPhone() != null && !updateDTO.getPhone().equals(user.getPhone())) {
            if (checkPhoneExists(updateDTO.getPhone())) {
                throw new BusinessException("user.phone.exists");
            }
            user.setPhone(updateDTO.getPhone());
        }

        // 更新性别
        if (updateDTO.getGender() != null) {
            user.setGender(updateDTO.getGender());
        }

        updateById(user);
        log.info("更新用户信息成功: {}", userId);

        return convertToVO(user);
    }
    
    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("user.not.found");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("user.password.old.error");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
        
        log.info("修改密码成功: {}", userId);
    }
    
    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = getOne(new QueryWrapper<User>().eq("email", email));
        if (user == null) {
            throw new BusinessException("user.email.not.found");
        }
        
        // 重置密码
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
        
        log.info("重置密码成功: {}", email);
    }
    
    @Override
    public boolean checkUsernameExists(String username) {
        return count(new QueryWrapper<User>().eq("username", username)) > 0;
    }
    
    @Override
    public boolean checkEmailExists(String email) {
        return count(new QueryWrapper<User>().eq("email", email)) > 0;
    }
    
    @Override
    public boolean checkPhoneExists(String phone) {
        return count(new QueryWrapper<User>().eq("phone", phone)) > 0;
    }
    
    @Override
    public void logout(Long userId) {
        // 这里可以添加token黑名单等逻辑
        log.info("用户登出: {}", userId);
    }
    
    /**
     * 将User转换为UserVO
     */
    private UserVO convertToVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 设置角色：admin用户为管理员角色
        if ("admin".equals(user.getUsername())) {
            userVO.setRoles(Arrays.asList("admin", "user"));
        } else {
            userVO.setRoles(Arrays.asList("user"));
        }
        return userVO;
    }
}