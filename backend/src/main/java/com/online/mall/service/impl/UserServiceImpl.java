package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.common.BusinessException;
import com.online.mall.dto.UserLoginDTO;
import com.online.mall.dto.UserRegisterDTO;
import com.online.mall.dto.UserUpdateDTO;
import com.online.mall.entity.Role;
import com.online.mall.entity.User;
import com.online.mall.mapper.UserMapper;
import com.online.mall.service.RoleService;
import com.online.mall.service.UserService;
import com.online.mall.utils.JwtTokenUtil;
import com.online.mall.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private RoleService roleService;
    
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

        // 从数据库加载用户角色
        List<Role> roles = roleService.getRolesByUserId(user.getId());
        if (roles != null && !roles.isEmpty()) {
            // 提取角色编码（去掉ROLE_前缀）
            List<String> roleCodes = roles.stream()
                    .map(role -> role.getRoleCode().replace("ROLE_", "").toLowerCase())
                    .collect(Collectors.toList());
            userVO.setRoles(roleCodes);
        } else {
            userVO.setRoles(new ArrayList<>());
        }

        return userVO;
    }

    // ============== 管理员接口实现 ==============

    @Override
    public Page<UserVO> getAdminUserList(String username, String phone, Integer status, Integer pageNum, Integer pageSize) {
        log.info("管理员获取用户列表: username={}, phone={}, status={}", username, phone, status);

        Page<User> page = new Page<>(pageNum, pageSize);

        QueryWrapper<User> wrapper = new QueryWrapper<User>()
                .orderByDesc("create_time");

        if (username != null && !username.isEmpty()) {
            wrapper.like("username", username);
        }
        if (phone != null && !phone.isEmpty()) {
            wrapper.like("phone", phone);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }

        Page<User> userPage = page(page, wrapper);

        Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> voList = new ArrayList<>();

        for (User user : userPage.getRecords()) {
            voList.add(convertToVO(user));
        }

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public UserVO getAdminUserById(Long userId) {
        log.info("管理员获取用户详情: userId={}", userId);

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("user.not.found");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional
    public void disableUser(Long userId) {
        log.info("禁用用户: userId={}", userId);

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("user.not.found");
        }

        // 检查用户是否拥有管理员角色
        List<Role> roles = roleService.getRolesByUserId(userId);
        boolean isAdmin = roles != null && roles.stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getRoleCode()));

        if (isAdmin) {
            throw new BusinessException("不能禁用管理员账户");
        }

        user.setStatus(0);
        updateById(user);
    }

    @Override
    @Transactional
    public void enableUser(Long userId) {
        log.info("启用用户: userId={}", userId);

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("user.not.found");
        }

        user.setStatus(1);
        updateById(user);
    }

    @Override
    @Transactional
    public void adminResetPassword(Long userId, String newPassword) {
        log.info("管理员重置用户密码: userId={}", userId);

        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("user.not.found");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
    }

    @Override
    public UserStatisticsVO getUserStatistics() {
        log.info("获取用户统计");

        UserStatisticsVO vo = new UserStatisticsVO();

        vo.setTotalUsers(Math.toIntExact(count()));
        vo.setActiveUsers(Math.toIntExact(count(new QueryWrapper<User>().eq("status", 1))));
        vo.setDisabledUsers(Math.toIntExact(count(new QueryWrapper<User>().eq("status", 0))));

        // 今日新增用户
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        vo.setTodayNewUsers(Math.toIntExact(count(new QueryWrapper<User>()
                .ge("create_time", todayStart))));

        return vo;
    }
}