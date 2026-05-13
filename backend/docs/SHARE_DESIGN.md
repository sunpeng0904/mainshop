# 分享功能详细设计文档

## 1. 系统架构设计

### 1.1 模块架构

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              分享功能模块                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │
│  │   Share     │  │   Like      │  │  Comment    │  │  Forward    │       │
│  │   Module    │  │   Module    │  │  Module     │  │  Module     │       │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘       │
│         │                │                │                │               │
│         └────────────────┴────────────────┴────────────────┘               │
│                                    │                                       │
│                                    ▼                                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐       │
│  │  Collect    │  │  Friend     │  │ Notification│  │   Content   │       │
│  │  Module     │  │  Module     │  │  Module     │  │  Moderation │       │
│  └─────────────┘  └─────────────┘  └─────────────┘  └─────────────┘       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

### 1.2 分层架构

```
┌─────────────────────────────────────────────────────────────┐
│                      Controller 层                           │
│  ShareController / LikeController / CommentController ...    │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Service 层                             │
│  ShareService / LikeService / CommentService ...             │
│  (业务逻辑、事务管理、缓存处理)                                │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Mapper 层                              │
│  ShareMapper / ShareLikeMapper / ShareCommentMapper ...      │
│  (MyBatis-Plus 数据访问)                                     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       Entity 层                              │
│  Share / ShareImage / ShareLike / ShareComment ...           │
│  (数据库实体映射)                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. 类设计

### 2.1 实体类设计

#### 2.1.1 Share 实体

```java
@Data
@TableName("share")
public class Share {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String content;
    
    private String location;
    
    /**
     * 可见范围: 1公开 2好友 3部分可见 4不给谁看 5仅自己
     */
    private Integer visibility;
    
    private Integer likeCount;
    
    private Integer commentCount;
    
    private Integer forwardCount;
    
    private Integer collectCount;
    
    /**
     * 状态: 0删除 1正常 2审核中
     */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
}
```

#### 2.1.2 ShareImage 实体

```java
@Data
@TableName("share_image")
public class ShareImage {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long shareId;
    
    private String imageUrl;
    
    private String thumbUrl;
    
    private Integer sortOrder;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

#### 2.1.3 ShareLike 实体

```java
@Data
@TableName("share_like")
public class ShareLike {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long shareId;
    
    private Long userId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

#### 2.1.4 ShareComment 实体

```java
@Data
@TableName("share_comment")
public class ShareComment {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long shareId;
    
    private Long userId;
    
    /**
     * 父评论ID，0表示一级评论
     */
    private Long parentId;
    
    /**
     * 回复的用户ID
     */
    private Long replyUserId;
    
    private String content;
    
    private Integer likeCount;
    
    /**
     * 状态: 0删除 1正常
     */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableLogic
    private Integer deleted;
}
```

#### 2.1.5 ShareCollect 实体

```java
@Data
@TableName("share_collect")
public class ShareCollect {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long shareId;
    
    private Long userId;
    
    /**
     * 收藏夹ID，0表示默认收藏夹
     */
    private Long folderId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

#### 2.1.6 ShareForward 实体

```java
@Data
@TableName("share_forward")
public class ShareForward {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 原分享ID
     */
    private Long shareId;
    
    private Long userId;
    
    /**
     * 转发评论
     */
    private String content;
    
    /**
     * 转发后生成的新分享ID
     */
    private Long newShareId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

#### 2.1.7 Friendship 实体

```java
@Data
@TableName("friendship")
public class Friendship {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long friendId;
    
    /**
     * 状态: 0已删除 1已关注 2已互关
     */
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

#### 2.1.8 Notification 实体

```java
@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    /**
     * 通知类型: LIKE, COMMENT, FORWARD, FOLLOW, MENTION
     */
    private String type;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 关联内容ID
     */
    private Long relatedId;
    
    private String content;
    
    /**
     * 是否已读: 0未读 1已读
     */
    private Integer isRead;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

---

### 2.2 DTO 设计

#### 2.2.1 ShareCreateDTO

```java
@Data
public class ShareCreateDTO {
    @NotBlank(message = "内容不能为空")
    @Size(max = 1000, message = "内容最多1000字")
    private String content;
    
    /**
     * 图片URL列表，最多9张
     */
    @Size(max = 9, message = "最多上传9张图片")
    private List<String> imageUrls;
    
    private String location;
    
    /**
     * 可见范围，默认1（公开）
     */
    private Integer visibility = 1;
    
    /**
     * @的用户ID列表
     */
    private List<Long> mentionUserIds;
}
```

#### 2.2.2 ShareQueryDTO

```java
@Data
public class ShareQueryDTO {
    private Long userId;
    
    /**
     * 查询类型: my(我的), friend(好友圈), hot(热门)
     */
    private String type;
    
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;
    
    @Min(value = 1)
    @Max(value = 50)
    private Integer pageSize = 10;
}
```

#### 2.2.3 CommentCreateDTO

```java
@Data
public class CommentCreateDTO {
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论最多500字")
    private String content;
    
    /**
     * 父评论ID，0表示一级评论
     */
    private Long parentId = 0L;
    
    /**
     * 回复的用户ID
     */
    private Long replyUserId = 0L;
}
```

#### 2.2.4 ForwardCreateDTO

```java
@Data
public class ForwardCreateDTO {
    @Size(max = 500, message = "转发评论最多500字")
    private String content;
    
    /**
     * 转发时是否同时点赞
     */
    private Boolean alsoLike = false;
}
```

---

### 2.3 VO 设计

#### 2.3.1 ShareVO

```java
@Data
public class ShareVO {
    private Long shareId;
    
    private UserSimpleVO user;
    
    private String content;
    
    private List<String> imageUrls;
    
    private String location;
    
    private Integer likeCount;
    
    private Integer commentCount;
    
    private Integer forwardCount;
    
    private Integer collectCount;
    
    /**
     * 当前用户是否已点赞
     */
    private Boolean isLiked;
    
    /**
     * 当前用户是否已收藏
     */
    private Boolean isCollected;
    
    private LocalDateTime createTime;
}
```

#### 2.3.2 CommentVO

```java
@Data
public class CommentVO {
    private Long commentId;
    
    private UserSimpleVO user;
    
    private String content;
    
    private Integer likeCount;
    
    private Boolean isLiked;
    
    /**
     * 回复的用户信息
     */
    private UserSimpleVO replyUser;
    
    /**
     * 子评论列表（最多显示3条）
     */
    private List<CommentVO> replyList;
    
    /**
     * 子评论总数
     */
    private Integer replyCount;
    
    private LocalDateTime createTime;
}
```

#### 2.3.3 UserSimpleVO

```java
@Data
public class UserSimpleVO {
    private Long userId;
    
    private String nickname;
    
    private String avatar;
}
```

#### 2.3.4 NotificationVO

```java
@Data
public class NotificationVO {
    private Long notificationId;
    
    private String type;
    
    private String content;
    
    private UserSimpleVO sender;
    
    /**
     * 关联的分享内容摘要
     */
    private String shareSummary;
    
    private Boolean isRead;
    
    private LocalDateTime createTime;
}
```

---

### 2.4 Service 接口设计

#### 2.4.1 ShareService

```java
public interface ShareService extends IService<Share> {
    
    /**
     * 发布分享
     */
    ShareVO createShare(Long userId, ShareCreateDTO dto);
    
    /**
     * 获取分享详情
     */
    ShareVO getShareDetail(Long shareId, Long currentUserId);
    
    /**
     * 获取我的分享列表
     */
    IPage<ShareVO> getMyShares(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取好友圈动态
     */
    IPage<ShareVO> getFriendShares(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取热门分享
     */
    IPage<ShareVO> getHotShares(Integer pageNum, Integer pageSize);
    
    /**
     * 获取用户圈子主页
     */
    IPage<ShareVO> getUserShares(Long userId, Long currentUserId, Integer pageNum, Integer pageSize);
    
    /**
     * 删除分享
     */
    void deleteShare(Long shareId, Long userId);
    
    /**
     * 检查分享可见性
     */
    boolean checkVisibility(Long shareId, Long viewerId);
}
```

#### 2.4.2 LikeService

```java
public interface LikeService extends IService<ShareLike> {
    
    /**
     * 点赞/取消点赞
     */
    Map<String, Object> toggleLike(Long shareId, Long userId);
    
    /**
     * 检查是否已点赞
     */
    boolean isLiked(Long shareId, Long userId);
    
    /**
     * 批量检查是否已点赞
     */
    Map<Long, Boolean> batchCheckLiked(List<Long> shareIds, Long userId);
    
    /**
     * 获取点赞列表
     */
    IPage<UserSimpleVO> getLikeUsers(Long shareId, Integer pageNum, Integer pageSize);
}
```

#### 2.4.3 CommentService

```java
public interface CommentService extends IService<ShareComment> {
    
    /**
     * 发表评论
     */
    CommentVO createComment(Long shareId, Long userId, CommentCreateDTO dto);
    
    /**
     * 获取评论列表（一级评论）
     */
    IPage<CommentVO> getComments(Long shareId, Long currentUserId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取评论的回复列表
     */
    List<CommentVO> getReplies(Long commentId, Long currentUserId, Integer limit);
    
    /**
     * 删除评论
     */
    void deleteComment(Long commentId, Long userId);
    
    /**
     * 点赞/取消点赞评论
     */
    Map<String, Object> toggleCommentLike(Long commentId, Long userId);
}
```

#### 2.4.4 CollectService

```java
public interface CollectService extends IService<ShareCollect> {
    
    /**
     * 收藏/取消收藏
     */
    Map<String, Object> toggleCollect(Long shareId, Long userId);
    
    /**
     * 检查是否已收藏
     */
    boolean isCollected(Long shareId, Long userId);
    
    /**
     * 批量检查是否已收藏
     */
    Map<Long, Boolean> batchCheckCollected(List<Long> shareIds, Long userId);
    
    /**
     * 获取收藏列表
     */
    IPage<ShareVO> getCollects(Long userId, Integer pageNum, Integer pageSize);
}
```

#### 2.4.5 ForwardService

```java
public interface ForwardService extends IService<ShareForward> {
    
    /**
     * 转发分享
     */
    ShareVO forwardShare(Long shareId, Long userId, ForwardCreateDTO dto);
    
    /**
     * 获取转发列表
     */
    IPage<ShareVO> getForwards(Long shareId, Integer pageNum, Integer pageSize);
}
```

#### 2.4.6 FriendshipService

```java
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
```

#### 2.4.7 NotificationService

```java
public interface NotificationService extends IService<Notification> {
    
    /**
     * 发送通知
     */
    void sendNotification(Long userId, String type, Long senderId, Long relatedId, String content);
    
    /**
     * 获取通知列表
     */
    IPage<NotificationVO> getNotifications(Long userId, String type, Integer pageNum, Integer pageSize);
    
    /**
     * 获取未读通知数量
     */
    Map<String, Integer> getUnreadCount(Long userId);
    
    /**
     * 标记通知已读
     */
    void markAsRead(Long notificationId, Long userId);
    
    /**
     * 标记所有通知已读
     */
    void markAllAsRead(Long userId, String type);
}
```

---

### 2.5 Controller 设计

#### 2.5.1 ShareController

```java
@RestController
@RequestMapping("/share")
@Tag(name = "分享接口")
public class ShareController {
    
    @Autowired
    private ShareService shareService;
    
    @Autowired
    private LikeService likeService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private CollectService collectService;
    
    @Autowired
    private ForwardService forwardService;
    
    @PostMapping
    @Operation(summary = "发布分享")
    public Result<ShareVO> createShare(@RequestBody @Valid ShareCreateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(shareService.createShare(userId, dto));
    }
    
    @GetMapping("/{shareId}")
    @Operation(summary = "获取分享详情")
    public Result<ShareVO> getShareDetail(@PathVariable Long shareId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(shareService.getShareDetail(shareId, userId));
    }
    
    @GetMapping("/my")
    @Operation(summary = "获取我的分享")
    public Result<IPage<ShareVO>> getMyShares(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(shareService.getMyShares(userId, pageNum, pageSize));
    }
    
    @GetMapping("/friends")
    @Operation(summary = "获取好友圈动态")
    public Result<IPage<ShareVO>> getFriendShares(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(shareService.getFriendShares(userId, pageNum, pageSize));
    }
    
    @GetMapping("/hot")
    @Operation(summary = "获取热门分享")
    public Result<IPage<ShareVO>> getHotShares(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(shareService.getHotShares(pageNum, pageSize));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户分享列表")
    public Result<IPage<ShareVO>> getUserShares(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return Result.success(shareService.getUserShares(userId, currentUserId, pageNum, pageSize));
    }
    
    @DeleteMapping("/{shareId}")
    @Operation(summary = "删除分享")
    public Result<Void> deleteShare(@PathVariable Long shareId) {
        Long userId = SecurityUtils.getCurrentUserId();
        shareService.deleteShare(shareId, userId);
        return Result.success();
    }
    
    @PostMapping("/{shareId}/like")
    @Operation(summary = "点赞/取消点赞")
    public Result<Map<String, Object>> toggleLike(@PathVariable Long shareId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(likeService.toggleLike(shareId, userId));
    }
    
    @GetMapping("/{shareId}/likes")
    @Operation(summary = "获取点赞列表")
    public Result<IPage<UserSimpleVO>> getLikeUsers(
            @PathVariable Long shareId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(likeService.getLikeUsers(shareId, pageNum, pageSize));
    }
    
    @PostMapping("/{shareId}/comment")
    @Operation(summary = "发表评论")
    public Result<CommentVO> createComment(
            @PathVariable Long shareId,
            @RequestBody @Valid CommentCreateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(commentService.createComment(shareId, userId, dto));
    }
    
    @GetMapping("/{shareId}/comments")
    @Operation(summary = "获取评论列表")
    public Result<IPage<CommentVO>> getComments(
            @PathVariable Long shareId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(commentService.getComments(shareId, userId, pageNum, pageSize));
    }
    
    @DeleteMapping("/{shareId}/comment/{commentId}")
    @Operation(summary = "删除评论")
    public Result<Void> deleteComment(
            @PathVariable Long shareId,
            @PathVariable Long commentId) {
        Long userId = SecurityUtils.getCurrentUserId();
        commentService.deleteComment(commentId, userId);
        return Result.success();
    }
    
    @PostMapping("/{shareId}/collect")
    @Operation(summary = "收藏/取消收藏")
    public Result<Map<String, Object>> toggleCollect(@PathVariable Long shareId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(collectService.toggleCollect(shareId, userId));
    }
    
    @PostMapping("/{shareId}/forward")
    @Operation(summary = "转发分享")
    public Result<ShareVO> forwardShare(
            @PathVariable Long shareId,
            @RequestBody @Valid ForwardCreateDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(forwardService.forwardShare(shareId, userId, dto));
    }
}
```

#### 2.5.2 FriendController

```java
@RestController
@RequestMapping("/friend")
@Tag(name = "好友接口")
public class FriendController {
    
    @Autowired
    private FriendshipService friendshipService;
    
    @PostMapping("/follow/{userId}")
    @Operation(summary = "关注用户")
    public Result<Map<String, Object>> followUser(@PathVariable Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return Result.success(friendshipService.followUser(currentUserId, userId));
    }
    
    @DeleteMapping("/follow/{userId}")
    @Operation(summary = "取消关注")
    public Result<Void> unfollowUser(@PathVariable Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        friendshipService.unfollowUser(currentUserId, userId);
        return Result.success();
    }
    
    @GetMapping("/following")
    @Operation(summary = "获取关注列表")
    public Result<IPage<UserSimpleVO>> getFollowing(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(friendshipService.getFollowing(userId, pageNum, pageSize));
    }
    
    @GetMapping("/followers")
    @Operation(summary = "获取粉丝列表")
    public Result<IPage<UserSimpleVO>> getFollowers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(friendshipService.getFollowers(userId, pageNum, pageSize));
    }
    
    @GetMapping("/friends")
    @Operation(summary = "获取好友列表")
    public Result<IPage<UserSimpleVO>> getFriends(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(friendshipService.getFriends(userId, pageNum, pageSize));
    }
}
```

#### 2.5.3 NotificationController

```java
@RestController
@RequestMapping("/notification")
@Tag(name = "通知接口")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/list")
    @Operation(summary = "获取通知列表")
    public Result<IPage<NotificationVO>> getNotifications(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(notificationService.getNotifications(userId, type, pageNum, pageSize));
    }
    
    @GetMapping("/unread-count")
    @Operation(summary = "获取未读通知数量")
    public Result<Map<String, Integer>> getUnreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(notificationService.getUnreadCount(userId));
    }
    
    @PutMapping("/{notificationId}/read")
    @Operation(summary = "标记通知已读")
    public Result<Void> markAsRead(@PathVariable Long notificationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        notificationService.markAsRead(notificationId, userId);
        return Result.success();
    }
    
    @PutMapping("/read-all")
    @Operation(summary = "标记所有通知已读")
    public Result<Void> markAllAsRead(@RequestParam(required = false) String type) {
        Long userId = SecurityUtils.getCurrentUserId();
        notificationService.markAllAsRead(userId, type);
        return Result.success();
    }
}
```

---

## 3. 核心流程时序图

### 3.1 发布分享流程

```
┌──────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────┐
│ 用户 │      │ShareController│    │ShareService  │      │ShareMapper  │      │  MySQL  │
└──┬───┘      └──────┬──────┘      └──────┬──────┘      └──────┬──────┘      └────┬────┘
   │                 │                    │                    │                  │
   │  POST /share    │                    │                    │                  │
   │────────────────>│                    │                    │                  │
   │                 │                    │                    │                  │
   │                 │  createShare()     │                    │                  │
   │                 │──────────────────>│                    │                  │
   │                 │                    │                    │                  │
   │                 │                    │  内容审核           │                  │
   │                 │                    │───────────┐        │                  │
   │                 │                    │           │        │                  │
   │                 │                    │<──────────┘        │                  │
   │                 │                    │                    │                  │
   │                 │                    │  INSERT share      │                  │
   │                 │                    │──────────────────>│                  │
   │                 │                    │                    │  INSERT          │
   │                 │                    │                    │────────────────>│
   │                 │                    │                    │                  │
   │                 │                    │  INSERT share_image│                  │
   │                 │                    │──────────────────>│                  │
   │                 │                    │                    │  INSERT          │
   │                 │                    │                    │────────────────>│
   │                 │                    │                    │                  │
   │                 │                    │  处理@提及          │                  │
   │                 │                    │───────────┐        │                  │
   │                 │                    │           │        │                  │
   │                 │                    │<──────────┘        │                  │
   │                 │                    │                    │                  │
   │                 │  返回 ShareVO      │                    │                  │
   │                 │<──────────────────│                    │                  │
   │                 │                    │                    │                  │
   │  返回结果        │                    │                    │                  │
   │<────────────────│                    │                    │                  │
   │                 │                    │                    │                  │
```

### 3.2 点赞流程

```
┌──────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────┐
│ 用户 │      │ShareController│    │LikeService   │      │ShareMapper  │      │  MySQL  │
└──┬───┘      └──────┬──────┘      └──────┬──────┘      └──────┬──────┘      └────┬────┘
   │                 │                    │                    │                  │
   │ POST /like      │                    │                    │                  │
   │────────────────>│                    │                    │                  │
   │                 │                    │                    │                  │
   │                 │  toggleLike()      │                    │                  │
   │                 │──────────────────>│                    │                  │
   │                 │                    │                    │                  │
   │                 │                    │  查询是否已点赞      │                  │
   │                 │                    │──────────────────>│                  │
   │                 │                    │                    │  SELECT          │
   │                 │                    │                    │────────────────>│
   │                 │                    │                    │                  │
   │                 │                    │  [未点赞] INSERT    │                  │
   │                 │                    │  [已点赞] DELETE    │                  │
   │                 │                    │──────────────────>│                  │
   │                 │                    │                    │  INSERT/DELETE   │
   │                 │                    │                    │────────────────>│
   │                 │                    │                    │                  │
   │                 │                    │  UPDATE share      │                  │
   │                 │                    │  SET like_count    │                  │
   │                 │                    │──────────────────>│                  │
   │                 │                    │                    │  UPDATE          │
   │                 │                    │                    │────────────────>│
   │                 │                    │                    │                  │
   │                 │                    │  发送通知           │                  │
   │                 │                    │───────────┐        │                  │
   │                 │                    │           │        │                  │
   │                 │                    │<──────────┘        │                  │
   │                 │                    │                    │                  │
   │                 │  返回结果           │                    │                  │
   │                 │<──────────────────│                    │                  │
   │                 │                    │                    │                  │
   │  返回结果        │                    │                    │                  │
   │<────────────────│                    │                    │                  │
   │                 │                    │                    │                  │
```

### 3.3 好友圈动态加载流程

```
┌──────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────────┐      ┌─────────┐
│ 用户 │      │ShareController│    │ShareService  │      │FriendService│      │  MySQL  │
└──┬───┘      └──────┬──────┘      └──────┬──────┘      └──────┬──────┘      └────┬────┘
   │                 │                    │                    │                  │
   │ GET /friends    │                    │                    │                  │
   │────────────────>│                    │                    │                  │
   │                 │                    │                    │                  │
   │                 │ getFriendShares()  │                    │                  │
   │                 │──────────────────>│                    │                  │
   │                 │                    │                    │                  │
   │                 │                    │  getFriendIds()    │                  │
   │                 │                    │──────────────────>│                  │
   │                 │                    │                    │  SELECT          │
   │                 │                    │                    │────────────────>│
   │                 │                    │                    │                  │
   │                 │                    │  返回好友ID列表      │                  │
   │                 │                    │<──────────────────│                  │
   │                 │                    │                    │                  │
   │                 │                    │  查询好友分享        │                  │
   │                 │                    │──────────────────>│                  │
   │                 │                    │                    │  SELECT          │
   │                 │                    │                    │  WHERE user_id   │
   │                 │                    │                    │  IN (好友IDs)     │
   │                 │                    │                    │  AND visibility  │
   │                 │                    │                    │  IN (1,2)        │
   │                 │                    │                    │────────────────>│
   │                 │                    │                    │                  │
   │                 │                    │  批量查询互动状态    │                  │
   │                 │                    │───────────┐        │                  │
   │                 │                    │           │        │                  │
   │                 │                    │<──────────┘        │                  │
   │                 │                    │                    │                  │
   │                 │  返回 ShareVO 分页  │                    │                  │
   │                 │<──────────────────│                    │                  │
   │                 │                    │                    │                  │
   │  返回结果        │                    │                    │                  │
   │<────────────────│                    │                    │                  │
   │                 │                    │                    │                  │
```

---

## 4. 缓存设计

### 4.1 缓存策略

| 缓存Key | 说明 | 过期时间 | 更新策略 |
|---------|------|----------|----------|
| `share:detail:{shareId}` | 分享详情 | 10分钟 | 写时删除 |
| `share:like:{shareId}` | 点赞用户集合 | 30分钟 | 增量更新 |
| `share:like:count:{shareId}` | 点赞数量 | 30分钟 | 增量更新 |
| `user:following:{userId}` | 关注列表 | 1小时 | 写时删除 |
| `user:friends:{userId}` | 好友列表 | 1小时 | 写时删除 |
| `notification:unread:{userId}` | 未读通知数 | 5分钟 | 增量更新 |

### 4.2 缓存实现

```java
@Service
public class ShareCacheService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String SHARE_DETAIL_KEY = "share:detail:";
    private static final String SHARE_LIKE_KEY = "share:like:";
    private static final String SHARE_LIKE_COUNT_KEY = "share:like:count:";
    private static final Duration DETAIL_EXPIRE = Duration.ofMinutes(10);
    private static final Duration LIKE_EXPIRE = Duration.ofMinutes(30);
    
    /**
     * 获取分享详情缓存
     */
    public ShareVO getShareDetail(Long shareId) {
        String key = SHARE_DETAIL_KEY + shareId;
        return (ShareVO) redisTemplate.opsForValue().get(key);
    }
    
    /**
     * 设置分享详情缓存
     */
    public void setShareDetail(Long shareId, ShareVO shareVO) {
        String key = SHARE_DETAIL_KEY + shareId;
        redisTemplate.opsForValue().set(key, shareVO, DETAIL_EXPIRE);
    }
    
    /**
     * 删除分享详情缓存
     */
    public void deleteShareDetail(Long shareId) {
        String key = SHARE_DETAIL_KEY + shareId;
        redisTemplate.delete(key);
    }
    
    /**
     * 点赞操作（增量更新）
     */
    public void addLike(Long shareId, Long userId) {
        String likeKey = SHARE_LIKE_KEY + shareId;
        String countKey = SHARE_LIKE_COUNT_KEY + shareId;
        
        redisTemplate.opsForSet().add(likeKey, userId);
        redisTemplate.opsForValue().increment(countKey);
        
        redisTemplate.expire(likeKey, LIKE_EXPIRE);
        redisTemplate.expire(countKey, LIKE_EXPIRE);
    }
    
    /**
     * 取消点赞（增量更新）
     */
    public void removeLike(Long shareId, Long userId) {
        String likeKey = SHARE_LIKE_KEY + shareId;
        String countKey = SHARE_LIKE_COUNT_KEY + shareId;
        
        redisTemplate.opsForSet().remove(likeKey, userId);
        redisTemplate.opsForValue().decrement(countKey);
    }
    
    /**
     * 检查是否已点赞
     */
    public Boolean isLiked(Long shareId, Long userId) {
        String key = SHARE_LIKE_KEY + shareId;
        return redisTemplate.opsForSet().isMember(key, userId);
    }
    
    /**
     * 批量检查是否已点赞
     */
    public Map<Long, Boolean> batchCheckLiked(List<Long> shareIds, Long userId) {
        Map<Long, Boolean> result = new HashMap<>();
        for (Long shareId : shareIds) {
            result.put(shareId, isLiked(shareId, userId));
        }
        return result;
    }
}
```

---

## 5. 异步处理设计

### 5.1 异步事件定义

```java
/**
 * 分享事件类型
 */
public enum ShareEventType {
    LIKE,           // 点赞
    COMMENT,        // 评论
    FORWARD,        // 转发
    FOLLOW,         // 关注
    MENTION         // @提及
}

/**
 * 分享事件
 */
@Data
public class ShareEvent {
    private ShareEventType type;
    private Long senderId;
    private Long receiverId;
    private Long shareId;
    private String content;
    private LocalDateTime timestamp;
    
    public static ShareEvent of(ShareEventType type, Long senderId, Long receiverId, Long shareId) {
        ShareEvent event = new ShareEvent();
        event.setType(type);
        event.setSenderId(senderId);
        event.setReceiverId(receiverId);
        event.setShareId(shareId);
        event.setTimestamp(LocalDateTime.now());
        return event;
    }
}
```

### 5.2 事件发布

```java
@Service
public class ShareEventPublisher {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    /**
     * 发布点赞事件
     */
    public void publishLikeEvent(Long shareId, Long senderId, Long receiverId) {
        if (!senderId.equals(receiverId)) {
            ShareEvent event = ShareEvent.of(ShareEventType.LIKE, senderId, receiverId, shareId);
            eventPublisher.publishEvent(event);
        }
    }
    
    /**
     * 发布评论事件
     */
    public void publishCommentEvent(Long shareId, Long senderId, Long receiverId, String content) {
        if (!senderId.equals(receiverId)) {
            ShareEvent event = ShareEvent.of(ShareEventType.COMMENT, senderId, receiverId, shareId);
            event.setContent(content);
            eventPublisher.publishEvent(event);
        }
    }
    
    /**
     * 发布转发事件
     */
    public void publishForwardEvent(Long shareId, Long senderId, Long receiverId) {
        if (!senderId.equals(receiverId)) {
            ShareEvent event = ShareEvent.of(ShareEventType.FORWARD, senderId, receiverId, shareId);
            eventPublisher.publishEvent(event);
        }
    }
    
    /**
     * 发布关注事件
     */
    public void publishFollowEvent(Long senderId, Long receiverId) {
        ShareEvent event = ShareEvent.of(ShareEventType.FOLLOW, senderId, receiverId, null);
        eventPublisher.publishEvent(event);
    }
}
```

### 5.3 事件监听

```java
@Component
@Slf4j
public class ShareEventListener {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ShareCacheService shareCacheService;
    
    /**
     * 处理分享事件
     */
    @Async
    @EventListener
    public void handleShareEvent(ShareEvent event) {
        log.info("处理分享事件: {}", event);
        
        try {
            switch (event.getType()) {
                case LIKE:
                    handleLikeEvent(event);
                    break;
                case COMMENT:
                    handleCommentEvent(event);
                    break;
                case FORWARD:
                    handleForwardEvent(event);
                    break;
                case FOLLOW:
                    handleFollowEvent(event);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error("处理分享事件失败", e);
        }
    }
    
    private void handleLikeEvent(ShareEvent event) {
        String content = "赞了你的分享";
        notificationService.sendNotification(
            event.getReceiverId(),
            "LIKE",
            event.getSenderId(),
            event.getShareId(),
            content
        );
    }
    
    private void handleCommentEvent(ShareEvent event) {
        String content = "评论了你的分享: " + event.getContent();
        if (content.length() > 50) {
            content = content.substring(0, 50) + "...";
        }
        notificationService.sendNotification(
            event.getReceiverId(),
            "COMMENT",
            event.getSenderId(),
            event.getShareId(),
            content
        );
    }
    
    private void handleForwardEvent(ShareEvent event) {
        String content = "转发了你的分享";
        notificationService.sendNotification(
            event.getReceiverId(),
            "FORWARD",
            event.getSenderId(),
            event.getShareId(),
            content
        );
    }
    
    private void handleFollowEvent(ShareEvent event) {
        String content = "关注了你";
        notificationService.sendNotification(
            event.getReceiverId(),
            "FOLLOW",
            event.getSenderId(),
            null,
            content
        );
    }
}
```

---

## 6. 内容审核设计

### 6.1 敏感词过滤

```java
@Service
public class ContentModerationService {
    
    /**
     * 敏感词库
     */
    private Set<String> sensitiveWords = new HashSet<>();
    
    @PostConstruct
    public void init() {
        // 加载敏感词库
        loadSensitiveWords();
    }
    
    /**
     * 检查内容是否合规
     */
    public boolean checkContent(String content) {
        if (StringUtils.isBlank(content)) {
            return true;
        }
        
        String lowerContent = content.toLowerCase();
        for (String word : sensitiveWords) {
            if (lowerContent.contains(word)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 过滤敏感词
     */
    public String filterContent(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }
        
        String result = content;
        for (String word : sensitiveWords) {
            String replacement = "*".repeat(word.length());
            result = result.replaceAll("(?i)" + word, replacement);
        }
        return result;
    }
    
    /**
     * 内容审核（可对接第三方审核服务）
     */
    public ModerationResult moderate(String content, List<String> imageUrls) {
        ModerationResult result = new ModerationResult();
        
        // 文本审核
        if (!checkContent(content)) {
            result.setPass(false);
            result.setReason("包含敏感内容");
            return result;
        }
        
        // 图片审核（可扩展）
        // if (imageUrls != null) {
        //     for (String imageUrl : imageUrls) {
        //         if (!checkImage(imageUrl)) {
        //             result.setPass(false);
        //             result.setReason("图片包含违规内容");
        //             return result;
        //         }
        //     }
        // }
        
        result.setPass(true);
        return result;
    }
    
    @Data
    public static class ModerationResult {
        private boolean pass;
        private String reason;
    }
}
```

### 6.2 举报处理

```java
@Data
@TableName("share_report")
public class ShareReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long shareId;
    
    private Long reporterId;
    
    /**
     * 举报原因: SPAM, VIOLENCE, PORNOGRAPHY, OTHER
     */
    private String reason;
    
    private String description;
    
    /**
     * 处理状态: 0待处理 1已处理 2已驳回
     */
    private Integer status;
    
    private String handleRemark;
    
    private LocalDateTime handleTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
```

---

## 7. 频率限制设计

### 7.1 限流注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    /**
     * 限流key前缀
     */
    String key() default "";
    
    /**
     * 时间窗口（秒）
     */
    int timeWindow() default 3600;
    
    /**
     * 最大请求数
     */
    int maxCount() default 100;
    
    /**
     * 限流类型
     */
    LimitType limitType() default LimitType.USER;
    
    enum LimitType {
        USER,    // 按用户限流
        IP       // 按IP限流
    }
}
```

### 7.2 限流实现

```java
@Aspect
@Component
public class RateLimitAspect {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        String key = buildKey(point, rateLimit);
        long currentTime = System.currentTimeMillis();
        
        // 滑动窗口限流
        Long count = redisTemplate.opsForZSet().count(key, currentTime - rateLimit.timeWindow() * 1000L, currentTime);
        
        if (count >= rateLimit.maxCount()) {
            throw new BusinessException(429, "操作太频繁，请稍后再试");
        }
        
        redisTemplate.opsForZSet().add(key, currentTime, currentTime);
        redisTemplate.expire(key, rateLimit.timeWindow(), TimeUnit.SECONDS);
        
        return point.proceed();
    }
    
    private String buildKey(ProceedingJoinPoint point, RateLimit rateLimit) {
        StringBuilder key = new StringBuilder("rate_limit:");
        key.append(rateLimit.key());
        
        if (rateLimit.limitType() == RateLimit.LimitType.USER) {
            Long userId = SecurityUtils.getCurrentUserId();
            key.append(":").append(userId);
        } else {
            // 获取IP
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String ip = IpUtils.getIp(request);
            key.append(":").append(ip);
        }
        
        return key.toString();
    }
}
```

### 7.3 限流配置

```java
@RestController
@RequestMapping("/share")
public class ShareController {
    
    @PostMapping
    @RateLimit(key = "share:create", timeWindow = 3600, maxCount = 10)
    public Result<ShareVO> createShare(@RequestBody @Valid ShareCreateDTO dto) {
        // ...
    }
    
    @PostMapping("/{shareId}/like")
    @RateLimit(key = "share:like", timeWindow = 3600, maxCount = 100)
    public Result<Map<String, Object>> toggleLike(@PathVariable Long shareId) {
        // ...
    }
    
    @PostMapping("/{shareId}/comment")
    @RateLimit(key = "share:comment", timeWindow = 3600, maxCount = 50)
    public Result<CommentVO> createComment(@PathVariable Long shareId, @RequestBody @Valid CommentCreateDTO dto) {
        // ...
    }
    
    @PostMapping("/{shareId}/forward")
    @RateLimit(key = "share:forward", timeWindow = 3600, maxCount = 20)
    public Result<ShareVO> forwardShare(@PathVariable Long shareId, @RequestBody @Valid ForwardCreateDTO dto) {
        // ...
    }
}
```

---

## 8. 数据库索引设计

### 8.1 索引策略

| 表名 | 索引名 | 字段 | 说明 |
|------|--------|------|------|
| share | idx_user_id | user_id | 按用户查询分享 |
| share | idx_create_time | create_time | 按时间排序 |
| share | idx_user_time | user_id, create_time | 用户分享按时间排序 |
| share_like | uk_share_user | share_id, user_id | 唯一索引，防止重复点赞 |
| share_like | idx_user_id | user_id | 查询用户点赞记录 |
| share_comment | idx_share_id | share_id | 查询分享评论 |
| share_comment | idx_parent_id | parent_id | 查询子评论 |
| share_collect | uk_share_user | share_id, user_id | 唯一索引，防止重复收藏 |
| share_forward | idx_share_id | share_id | 查询转发记录 |
| friendship | uk_user_friend | user_id, friend_id | 唯一索引 |
| friendship | idx_friend_id | friend_id | 查询粉丝 |
| notification | idx_user_type | user_id, type, is_read | 查询用户通知 |

### 8.2 查询优化

```sql
-- 好友圈动态查询优化
SELECT s.*, u.nickname, u.avatar
FROM share s
LEFT JOIN user u ON s.user_id = u.id
WHERE s.user_id IN (
    SELECT friend_id FROM friendship 
    WHERE user_id = ? AND status = 2
)
AND s.visibility IN (1, 2)
AND s.status = 1
AND s.deleted = 0
ORDER BY s.create_time DESC
LIMIT ?, ?

-- 使用覆盖索引
CREATE INDEX idx_share_cover ON share(user_id, visibility, status, deleted, create_time);
```

---

## 9. 安全设计

### 9.1 数据权限控制

```java
@Service
public class SharePermissionService {
    
    @Autowired
    private FriendshipService friendshipService;
    
    /**
     * 检查分享查看权限
     */
    public boolean checkViewPermission(Long shareId, Long viewerId) {
        Share share = shareMapper.selectById(shareId);
        if (share == null) {
            return false;
        }
        
        // 自己的分享总是可见
        if (share.getUserId().equals(viewerId)) {
            return true;
        }
        
        // 根据可见范围判断
        switch (share.getVisibility()) {
            case 1: // 公开
                return true;
            case 2: // 好友可见
                return friendshipService.isFriend(share.getUserId(), viewerId);
            case 3: // 部分可见
                return isPartiallyVisible(shareId, viewerId);
            case 4: // 不给谁看
                return !isBlocked(shareId, viewerId);
            case 5: // 仅自己
                return false;
            default:
                return false;
        }
    }
    
    /**
     * 检查操作权限
     */
    public boolean checkOperationPermission(Long shareId, Long userId) {
        Share share = shareMapper.selectById(shareId);
        return share != null && share.getUserId().equals(userId);
    }
}
```

### 9.2 XSS 防护

```java
@Component
public class XssFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new XssHttpServletRequestWrapper((HttpServletRequest) request), response);
    }
}

public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    
    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }
    
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return value != null ? cleanXSS(value) : null;
    }
    
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values != null) {
            String[] cleanValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                cleanValues[i] = cleanXSS(values[i]);
            }
            return cleanValues;
        }
        return null;
    }
    
    private String cleanXSS(String value) {
        value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        value = value.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");
        value = value.replaceAll("'", "&#39;");
        value = value.replaceAll("eval\\((.*)\\)", "");
        value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
        return value;
    }
}
```

---

## 10. 性能优化方案

### 10.1 数据库优化

1. **分页查询优化**
   - 使用游标分页代替 offset 分页
   - 避免深度分页

2. **批量查询优化**
   - 使用 IN 查询代替循环查询
   - 控制 IN 查询数量（< 1000）

3. **索引优化**
   - 覆盖索引减少回表
   - 联合索引遵循最左前缀原则

### 10.2 缓存优化

1. **热点数据缓存**
   - 分享详情缓存
   - 用户信息缓存
   - 计数缓存（点赞、评论数）

2. **缓存穿透防护**
   - 布隆过滤器
   - 空值缓存

3. **缓存雪崩防护**
   - 过期时间随机化
   - 多级缓存

### 10.3 接口优化

1. **数据裁剪**
   - 列表接口只返回必要字段
   - 详情接口按需加载

2. **异步处理**
   - 通知发送异步化
   - 计数更新异步化

3. **批量处理**
   - 批量查询互动状态
   - 批量更新计数

---

## 11. 扩展性设计

### 11.1 视频分享扩展

```java
public class ShareVideo {
    private Long id;
    private Long shareId;
    private String videoUrl;
    private String coverUrl;
    private Integer duration;
    private Long fileSize;
}
```

### 11.2 话题功能扩展

```java
public class Topic {
    private Long id;
    private String name;
    private String description;
    private Integer shareCount;
    private Integer followCount;
}

public class ShareTopic {
    private Long id;
    private Long shareId;
    private Long topicId;
}
```

### 11.3 分享分组扩展

```java
public class ShareGroup {
    private Long id;
    private Long userId;
    private String name;
    private Integer sortOrder;
}

public class ShareGroupMember {
    private Long id;
    private Long groupId;
    private Long shareId;
}
```
