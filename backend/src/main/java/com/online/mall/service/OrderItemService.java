package com.online.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.entity.OrderItem;

import java.util.List;

/**
 * 订单项服务接口
 */
public interface OrderItemService extends IService<OrderItem> {

    /**
     * 根据订单ID获取订单项列表
     */
    List<OrderItem> getByOrderId(Long orderId);
}
