package com.online.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.online.mall.common.BusinessException;
import com.online.mall.dto.OrderCreateDTO;
import com.online.mall.dto.OrderQueryDTO;
import com.online.mall.entity.*;
import com.online.mall.mapper.OrderMapper;
import com.online.mall.mapper.UserMapper;
import com.online.mall.service.*;
import com.online.mall.vo.CartVO;
import com.online.mall.vo.OrderItemVO;
import com.online.mall.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public OrderVO createOrder(Long userId, OrderCreateDTO createDTO) {
        log.info("创建订单: userId={}, cartIds={}", userId, createDTO.getCartIds());

        // 1. 获取选中的购物车商品
        List<CartVO> cartItems = cartService.getSelectedCartItems(userId);
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BusinessException("order.cart.empty");
        }

        // 验证购物车ID
        List<Long> cartIds = createDTO.getCartIds();
        List<CartVO> orderItems = new ArrayList<>();
        for (CartVO item : cartItems) {
            if (cartIds.contains(item.getId())) {
                orderItems.add(item);
            }
        }

        if (orderItems.isEmpty()) {
            throw new BusinessException("order.cart.empty");
        }

        // 2. 验证商品库存
        for (CartVO item : orderItems) {
            if (item.getProductStatus() != 1) {
                throw new BusinessException("商品【" + item.getProductName() + "】已下架");
            }
            if (item.getQuantity() > item.getProductStock()) {
                throw new BusinessException("商品【" + item.getProductName() + "】库存不足");
            }
        }

        // 3. 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartVO item : orderItems) {
            if (item.getSubtotal() != null) {
                totalAmount = totalAmount.add(item.getSubtotal());
            }
        }

        // 4. 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount); // 暂无优惠
        order.setFreightAmount(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setPayType(createDTO.getPayType());
        order.setStatus(Order.STATUS_PENDING_PAYMENT);
        order.setReceiverName(createDTO.getReceiverName());
        order.setReceiverPhone(createDTO.getReceiverPhone());
        order.setReceiverAddress(createDTO.getReceiverAddress());
        order.setRemark(createDTO.getRemark());

        save(order);

        // 5. 创建订单项
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartVO item : orderItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getProductName());
            orderItem.setProductImage(item.getProductImage());
            orderItem.setProductPrice(item.getProductPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSubtotal(item.getSubtotal());
            orderItemList.add(orderItem);

            // 扣减库存、增加销量
            Product product = productService.getById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() - item.getQuantity());
                product.setSales(product.getSales() + item.getQuantity());
                productService.updateById(product);
            }
        }
        orderItemService.saveBatch(orderItemList);

        // 6. 删除已下单的购物车商品
        cartService.batchRemoveCartItems(userId, cartIds);

        log.info("订单创建成功: orderNo={}", order.getOrderNo());

        return convertToVO(order, orderItemList);
    }

    @Override
    public OrderVO getOrderById(Long userId, Long orderId) {
        log.info("获取订单详情: userId={}, orderId={}", userId, orderId);

        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("order.not.found");
        }

        List<OrderItem> orderItems = orderItemService.list(new QueryWrapper<OrderItem>()
                .eq("order_id", orderId));

        return convertToVO(order, orderItems);
    }

    @Override
    public OrderVO getOrderByOrderNo(Long userId, String orderNo) {
        log.info("根据订单编号获取订单: userId={}, orderNo={}", userId, orderNo);

        Order order = getOne(new QueryWrapper<Order>().eq("order_no", orderNo));
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("order.not.found");
        }

        List<OrderItem> orderItems = orderItemService.list(new QueryWrapper<OrderItem>()
                .eq("order_id", order.getId()));

        return convertToVO(order, orderItems);
    }

    @Override
    public Page<OrderVO> getUserOrderList(Long userId, OrderQueryDTO queryDTO) {
        log.info("获取用户订单列表: userId={}, status={}", userId, queryDTO.getStatus());

        Page<Order> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        QueryWrapper<Order> wrapper = new QueryWrapper<Order>()
                .eq("user_id", userId)
                .orderByDesc("create_time");

        if (queryDTO.getStatus() != null) {
            wrapper.eq("status", queryDTO.getStatus());
        }
        if (queryDTO.getOrderNo() != null && !queryDTO.getOrderNo().isEmpty()) {
            wrapper.like("order_no", queryDTO.getOrderNo());
        }

        Page<Order> orderPage = page(page, wrapper);

        Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderVO> voList = new ArrayList<>();

        for (Order order : orderPage.getRecords()) {
            List<OrderItem> orderItems = orderItemService.list(new QueryWrapper<OrderItem>()
                    .eq("order_id", order.getId()));
            voList.add(convertToVO(order, orderItems));
        }

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, Long orderId, String reason) {
        log.info("取消订单: userId={}, orderId={}, reason={}", userId, orderId, reason);

        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("order.not.found");
        }

        if (order.getStatus() != Order.STATUS_PENDING_PAYMENT) {
            throw new BusinessException("order.cannot.cancel");
        }

        // 恢复库存
        List<OrderItem> orderItems = orderItemService.list(new QueryWrapper<OrderItem>()
                .eq("order_id", orderId));

        for (OrderItem item : orderItems) {
            Product product = productService.getById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(product.getSales() - item.getQuantity());
                productService.updateById(product);
            }
        }

        // 更新订单状态
        order.setStatus(Order.STATUS_CANCELLED);
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void confirmReceive(Long userId, Long orderId) {
        log.info("确认收货: userId={}, orderId={}", userId, orderId);

        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("order.not.found");
        }

        if (order.getStatus() != Order.STATUS_SHIPPED) {
            throw new BusinessException("order.status.error");
        }

        order.setStatus(Order.STATUS_COMPLETED);
        order.setReceiveTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Long userId, Long orderId) {
        log.info("删除订单: userId={}, orderId={}", userId, orderId);

        Order order = getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("order.not.found");
        }

        // 只能删除已完成或已取消的订单
        if (order.getStatus() != Order.STATUS_COMPLETED && order.getStatus() != Order.STATUS_CANCELLED) {
            throw new BusinessException("order.status.error");
        }

        removeById(orderId);
    }

    @Override
    public OrderStatisticsVO getOrderStatistics(Long userId) {
        log.info("获取订单统计: userId={}", userId);

        OrderStatisticsVO vo = new OrderStatisticsVO();

        vo.setTotalOrders(Math.toIntExact(count(new QueryWrapper<Order>().eq("user_id", userId))));
        vo.setPendingPayment(Math.toIntExact(count(new QueryWrapper<Order>()
                .eq("user_id", userId).eq("status", Order.STATUS_PENDING_PAYMENT))));
        vo.setPendingShipment(Math.toIntExact(count(new QueryWrapper<Order>()
                .eq("user_id", userId).eq("status", Order.STATUS_PENDING_SHIPMENT))));
        vo.setShipped(Math.toIntExact(count(new QueryWrapper<Order>()
                .eq("user_id", userId).eq("status", Order.STATUS_SHIPPED))));
        vo.setCompleted(Math.toIntExact(count(new QueryWrapper<Order>()
                .eq("user_id", userId).eq("status", Order.STATUS_COMPLETED))));
        vo.setCancelled(Math.toIntExact(count(new QueryWrapper<Order>()
                .eq("user_id", userId).eq("status", Order.STATUS_CANCELLED))));

        return vo;
    }

    // ============== 管理员接口实现 ==============

    @Override
    public Page<OrderVO> getAdminOrderList(OrderQueryDTO queryDTO) {
        log.info("管理员获取订单列表: status={}", queryDTO.getStatus());

        Page<Order> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        QueryWrapper<Order> wrapper = new QueryWrapper<Order>()
                .orderByDesc("create_time");

        if (queryDTO.getStatus() != null) {
            wrapper.eq("status", queryDTO.getStatus());
        }
        if (queryDTO.getOrderNo() != null && !queryDTO.getOrderNo().isEmpty()) {
            wrapper.like("order_no", queryDTO.getOrderNo());
        }

        Page<Order> orderPage = page(page, wrapper);

        Page<OrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderVO> voList = new ArrayList<>();

        for (Order order : orderPage.getRecords()) {
            List<OrderItem> orderItems = orderItemService.list(new QueryWrapper<OrderItem>()
                    .eq("order_id", order.getId()));
            voList.add(convertToVO(order, orderItems));
        }

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public OrderVO getAdminOrderById(Long orderId) {
        log.info("管理员获取订单详情: orderId={}", orderId);

        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("order.not.found");
        }

        List<OrderItem> orderItems = orderItemService.list(new QueryWrapper<OrderItem>()
                .eq("order_id", orderId));

        return convertToVO(order, orderItems);
    }

    @Override
    @Transactional
    public void shipOrder(Long orderId, String logisticsCompany, String logisticsNo) {
        log.info("发货: orderId={}, logisticsCompany={}, logisticsNo={}", orderId, logisticsCompany, logisticsNo);

        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("order.not.found");
        }

        if (order.getStatus() != Order.STATUS_PENDING_SHIPMENT) {
            throw new BusinessException("订单状态不允许发货");
        }

        order.setStatus(Order.STATUS_SHIPPED);
        order.setDeliveryTime(LocalDateTime.now());
        // 可以添加物流信息字段
        updateById(order);
    }

    @Override
    @Transactional
    public void adminCancelOrder(Long orderId, String reason) {
        log.info("管理员取消订单: orderId={}, reason={}", orderId, reason);

        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("order.not.found");
        }

        // 已发货或已完成的订单不能取消
        if (order.getStatus() == Order.STATUS_SHIPPED || order.getStatus() == Order.STATUS_COMPLETED) {
            throw new BusinessException("订单状态不允许取消");
        }

        // 如果是待付款或待发货状态，需要恢复库存
        if (order.getStatus() == Order.STATUS_PENDING_PAYMENT || order.getStatus() == Order.STATUS_PENDING_SHIPMENT) {
            List<OrderItem> orderItems = orderItemService.list(new QueryWrapper<OrderItem>()
                    .eq("order_id", orderId));

            for (OrderItem item : orderItems) {
                Product product = productService.getById(item.getProductId());
                if (product != null) {
                    product.setStock(product.getStock() + item.getQuantity());
                    product.setSales(product.getSales() - item.getQuantity());
                    productService.updateById(product);
                }
            }
        }

        order.setStatus(Order.STATUS_CANCELLED);
        order.setCancelReason(reason);
        order.setCancelTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional
    public void adminDeleteOrder(Long orderId) {
        log.info("管理员删除订单: orderId={}", orderId);

        Order order = getById(orderId);
        if (order == null) {
            throw new BusinessException("order.not.found");
        }

        // 删除订单项
        orderItemService.remove(new QueryWrapper<OrderItem>().eq("order_id", orderId));
        // 删除订单
        removeById(orderId);
    }

    @Override
    public OrderStatisticsVO getAdminOrderStatistics() {
        log.info("管理员获取订单统计");

        OrderStatisticsVO vo = new OrderStatisticsVO();

        vo.setTotalOrders(Math.toIntExact(count()));
        vo.setPendingPayment(Math.toIntExact(count(new QueryWrapper<Order>()
                .eq("status", Order.STATUS_PENDING_PAYMENT))));
        vo.setPendingShipment(Math.toIntExact(count(new QueryWrapper<Order>()
                .eq("status", Order.STATUS_PENDING_SHIPMENT))));
        vo.setShipped(Math.toIntExact(count(new QueryWrapper<Order>()
                .eq("status", Order.STATUS_SHIPPED))));
        vo.setCompleted(Math.toIntExact(count(new QueryWrapper<Order>()
                .eq("status", Order.STATUS_COMPLETED))));
        vo.setCancelled(Math.toIntExact(count(new QueryWrapper<Order>()
                .eq("status", Order.STATUS_CANCELLED))));

        return vo;
    }

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return timestamp + random;
    }

    /**
     * 转换为VO
     */
    private OrderVO convertToVO(Order order, List<OrderItem> orderItems) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);

        // 状态名称
        vo.setStatusName(getStatusName(order.getStatus()));

        // 支付方式名称
        if (order.getPayType() != null) {
            vo.setPayTypeName(order.getPayType() == 1 ? "支付宝" : "微信");
        }

        // 查询用户名
        if (order.getUserId() != null) {
            User user = userMapper.selectById(order.getUserId());
            if (user != null) {
                vo.setUsername(user.getUsername());
            }
        }

        // 订单项
        List<OrderItemVO> itemVOList = new ArrayList<>();
        for (OrderItem item : orderItems) {
            OrderItemVO itemVO = new OrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            itemVOList.add(itemVO);
        }
        vo.setItems(itemVOList);

        return vo;
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已取消";
            case 5: return "已退款";
            default: return "未知";
        }
    }
}
