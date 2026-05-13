package com.online.mall.service.share;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.share.Friendship;
import com.online.mall.vo.share.UserSimpleVO;

import java.util.List;
import java.util.Map;

/**
 * 好友关系服务接口
 */
public interface FriendshipService extends IService<Friendship> {

    /**
     * 关注用户
     */
    Map<String, Object> followUser(Long userId, Long friendId);

    /**
     * 取消关注
     */
    void unfollowUser(Long userId, Long friendId);

    /**
     * 检查是否已关注
     */
    boolean isFollowing(Long userId, Long friendId);

    /**
     * 检查是否互关（好友）
     */
    boolean isFriend(Long userId, Long friendId);

    /**
     * 获取关注列表
     */
    IPage<UserSimpleVO> getFollowing(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取粉丝列表
     */
    IPage<UserSimpleVO> getFollowers(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取好友列表（互关）
     */
    IPage<UserSimpleVO> getFriends(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取好友ID列表
     */
    List<Long> getFriendIds(Long userId);
}
