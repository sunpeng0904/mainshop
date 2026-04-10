package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.entity.OrderItem;
import com.online.mall.mapper.OrderItemMapper;
import com.online.mall.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单项服务实现
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Override
    public List<OrderItem> getByOrderId(Long orderId) {
        return list(new QueryWrapper<OrderItem>().eq("order_id", orderId));
    }
}
