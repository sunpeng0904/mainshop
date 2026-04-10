package com.online.mall;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.online.mall.dto.UserRegisterDTO;
import com.online.mall.entity.User;
import com.online.mall.mapper.UserMapper;
import com.online.mall.service.UserService;
import com.online.mall.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 应用启动后执行
 */
@Slf4j
@Component
public class ApplicationStartupRunner implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("==========================================");
        log.info("在线商城系统启动成功！");
        log.info("后端API地址: http://localhost:8082/api");
        log.info("Swagger文档: http://localhost:8082/api/swagger-ui.html");
        log.info("数据库: MySQL (online_mall)");
        log.info("前端地址: http://localhost:8080");
        log.info("==========================================");

        // 初始化测试用户数据
        initTestUsers();

        log.info("==========================================");
    }

    private void initTestUsers() {
        try {
            // 检查管理员用户是否存在
            if (userMapper.selectCount(new QueryWrapper<User>().eq("username", "admin")) == 0) {
                // 创建管理员用户
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setNickname("系统管理员");
                admin.setEmail("admin@onlinemall.com");
                admin.setPhone("13800138000");
                admin.setGender(1);
                admin.setStatus(1);
                admin.setAvatar("https://via.placeholder.com/150");
                admin.setLastLoginTime(LocalDateTime.now());
                userMapper.insert(admin);
                log.info("创建管理员用户: admin / 123456");
            }

            // 检查普通用户1是否存在
            if (userMapper.selectCount(new QueryWrapper<User>().eq("username", "user1")) == 0) {
                // 创建普通用户1
                User user1 = new User();
                user1.setUsername("user1");
                user1.setPassword(passwordEncoder.encode("123456"));
                user1.setNickname("张三");
                user1.setEmail("user1@onlinemall.com");
                user1.setPhone("13800138001");
                user1.setGender(1);
                user1.setStatus(1);
                user1.setAvatar("https://via.placeholder.com/150");
                user1.setLastLoginTime(LocalDateTime.now());
                userMapper.insert(user1);
                log.info("创建普通用户: user1 / 123456");
            }

            // 检查普通用户2是否存在
            if (userMapper.selectCount(new QueryWrapper<User>().eq("username", "user2")) == 0) {
                // 创建普通用户2
                User user2 = new User();
                user2.setUsername("user2");
                user2.setPassword(passwordEncoder.encode("123456"));
                user2.setNickname("李四");
                user2.setEmail("user2@onlinemall.com");
                user2.setPhone("13800138002");
                user2.setGender(2);
                user2.setStatus(1);
                user2.setAvatar("https://via.placeholder.com/150");
                user2.setLastLoginTime(LocalDateTime.now());
                userMapper.insert(user2);
                log.info("创建普通用户: user2 / 123456");
            }

            log.info("测试账号初始化完成");
        } catch (Exception e) {
            log.error("初始化测试用户失败", e);
        }
    }
}