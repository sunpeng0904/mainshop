package com.online.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.User;
import com.online.mall.dto.UserLoginDTO;
import com.online.mall.dto.UserRegisterDTO;
import com.online.mall.dto.UserUpdateDTO;
import com.online.mall.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    UserVO register(UserRegisterDTO registerDTO);
    
    /**
     * 用户登录
     */
    String login(UserLoginDTO loginDTO);
    
    /**
     * 获取用户信息
     */
    UserVO getUserInfo(Long userId);
    
    /**
     * 更新用户信息
     */
    UserVO updateUserInfo(Long userId, UserUpdateDTO updateDTO);
    
    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     */
    void resetPassword(String email, String newPassword);
    
    /**
     * 检查用户名是否已存在
     */
    boolean checkUsernameExists(String username);
    
    /**
     * 检查邮箱是否已存在
     */
    boolean checkEmailExists(String email);
    
    /**
     * 检查手机号是否已存在
     */
    boolean checkPhoneExists(String phone);
    
    /**
     * 用户登出
     */
    void logout(Long userId);
}