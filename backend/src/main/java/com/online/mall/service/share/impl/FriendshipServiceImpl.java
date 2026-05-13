package com.online.mall.service.share.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.entity.User;
import com.online.mall.entity.share.Friendship;
import com.online.mall.mapper.FriendshipMapper;
import com.online.mall.mapper.UserMapper;
import com.online.mall.service.share.FriendshipService;
import com.online.mall.service.share.NotificationService;
import com.online.mall.vo.share.UserSimpleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 好友关系服务实现类
 */
@Slf4j
@Service
public class FriendshipServiceImpl extends ServiceImpl<FriendshipMapper, Friendship> implements FriendshipService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> followUser(Long userId, Long friendId) {
        Map<String, Object> result = new HashMap<>();

        if (userId.equals(friendId)) {
            throw new RuntimeException("不能关注自己");
        }

        // 检查是否已关注
        LambdaQueryWrapper<Friendship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friendship::getUserId, userId)
                .eq(Friendship::getFriendId, friendId);
        Friendship existing = this.getOne(wrapper);

        if (existing != null) {
            if (existing.getStatus() == 1 || existing.getStatus() == 2) {
                throw new RuntimeException("已关注该用户");
            }
            // 重新关注
            existing.setStatus(1);
            this.updateById(existing);
        } else {
            // 新建关注
            Friendship friendship = new Friendship();
            friendship.setUserId(userId);
            friendship.setFriendId(friendId);
            friendship.setStatus(1);
            this.save(friendship);
        }

        // 检查是否互关
        LambdaQueryWrapper<Friendship> reverseWrapper = new LambdaQueryWrapper<>();
        reverseWrapper.eq(Friendship::getUserId, friendId)
                .eq(Friendship::getFriendId, userId);
        Friendship reverse = this.getOne(reverseWrapper);

        boolean isMutual = false;
        if (reverse != null && reverse.getStatus() == 1) {
            // 互关
            existing.setStatus(2);
            this.updateById(existing);
            reverse.setStatus(2);
            this.updateById(reverse);
            isMutual = true;
        }

        result.put("isMutual", isMutual);

        // 发送通知
        notificationService.sendNotification(friendId, "FOLLOW", userId, null, "关注了你");

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfollowUser(Long userId, Long friendId) {
        LambdaQueryWrapper<Friendship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friendship::getUserId, userId)
                .eq(Friendship::getFriendId, friendId);
        Friendship friendship = this.getOne(wrapper);

        if (friendship == null) {
            throw new RuntimeException("未关注该用户");
        }

        // 如果是互关，需要更新对方状态
        if (friendship.getStatus() == 2) {
            LambdaQueryWrapper<Friendship> reverseWrapper = new LambdaQueryWrapper<>();
            reverseWrapper.eq(Friendship::getUserId, friendId)
                    .eq(Friendship::getFriendId, userId);
            Friendship reverse = this.getOne(reverseWrapper);
            if (reverse != null) {
                reverse.setStatus(1); // 变为单向关注
                this.updateById(reverse);
            }
        }

        this.removeById(friendship.getId());
    }

    @Override
    public boolean isFollowing(Long userId, Long friendId) {
        LambdaQueryWrapper<Friendship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friendship::getUserId, userId)
                .eq(Friendship::getFriendId, friendId)
                .in(Friendship::getStatus, 1, 2);
        return this.count(wrapper) > 0;
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        LambdaQueryWrapper<Friendship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friendship::getUserId, userId)
                .eq(Friendship::getFriendId, friendId)
                .eq(Friendship::getStatus, 2);
        return this.count(wrapper) > 0;
    }

    @Override
    public IPage<UserSimpleVO> getFollowing(Long userId, Integer pageNum, Integer pageSize) {
        Page<Friendship> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Friendship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friendship::getUserId, userId)
                .in(Friendship::getStatus, 1, 2)
                .orderByDesc(Friendship::getCreateTime);
        Page<Friendship> friendshipPage = this.page(page);

        return convertToUserSimpleVOPage(friendshipPage, true);
    }

    @Override
    public IPage<UserSimpleVO> getFollowers(Long userId, Integer pageNum, Integer pageSize) {
        Page<Friendship> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Friendship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friendship::getFriendId, userId)
                .in(Friendship::getStatus, 1, 2)
                .orderByDesc(Friendship::getCreateTime);
        Page<Friendship> friendshipPage = this.page(page);

        return convertToUserSimpleVOPage(friendshipPage, false);
    }

    @Override
    public IPage<UserSimpleVO> getFriends(Long userId, Integer pageNum, Integer pageSize) {
        Page<Friendship> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Friendship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friendship::getUserId, userId)
                .eq(Friendship::getStatus, 2)
                .orderByDesc(Friendship::getCreateTime);
        Page<Friendship> friendshipPage = this.page(page);

        return convertToUserSimpleVOPage(friendshipPage, true);
    }

    @Override
    public List<Long> getFriendIds(Long userId) {
        LambdaQueryWrapper<Friendship> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Friendship::getUserId, userId)
                .eq(Friendship::getStatus, 2);
        List<Friendship> friendships = this.list(wrapper);
        return friendships.stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toList());
    }

    /**
     * 转换为UserSimpleVO分页
     */
    private IPage<UserSimpleVO> convertToUserSimpleVOPage(Page<Friendship> friendshipPage, boolean isGetFriend) {
        Page<UserSimpleVO> voPage = new Page<>(friendshipPage.getCurrent(), friendshipPage.getSize(), friendshipPage.getTotal());

        List<Long> userIds = friendshipPage.getRecords().stream()
                .map(isGetFriend ? Friendship::getFriendId : Friendship::getUserId)
                .collect(Collectors.toList());

        if (userIds.isEmpty()) {
            voPage.setRecords(new ArrayList<>());
            return voPage;
        }

        List<User> users = userMapper.selectBatchIds(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        List<UserSimpleVO> voList = friendshipPage.getRecords().stream()
                .map(friendship -> {
                    Long uid = isGetFriend ? friendship.getFriendId() : friendship.getUserId();
                    User user = userMap.get(uid);
                    if (user != null) {
                        UserSimpleVO vo = new UserSimpleVO();
                        vo.setUserId(user.getId());
                        vo.setNickname(user.getUsername());
                        vo.setAvatar(user.getAvatar());
                        return vo;
                    }
                    return null;
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }
}
