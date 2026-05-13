package com.online.mall.service.share.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.entity.share.ShareImage;
import com.online.mall.mapper.ShareImageMapper;
import com.online.mall.service.share.ShareImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 分享图片服务实现类
 */
@Slf4j
@Service
public class ShareImageServiceImpl extends ServiceImpl<ShareImageMapper, ShareImage> implements ShareImageService {
}
