package com.online.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.online.mall.dto.OrderCreateDTO;
import com.online.mall.dto.OrderQueryDTO;
import com.online.mall.entity.Order;
import com.online.mall.vo.OrderVO;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService extends IService<Order> {

    /**
     * 创建订单
     */
    OrderVO createOrder(Long userId, OrderCreateDTO createDTO);

    /**
     * 根据订单ID获取订单详情
     */
    OrderVO getOrderById(Long userId, Long orderId);

    /**
     * 根据订单编号获取订单详情
     */
    OrderVO getOrderByOrderNo(Long userId, String orderNo);

    /**
     * 获取用户订单列表
     */
    Page<OrderVO> getUserOrderList(Long userId, OrderQueryDTO queryDTO);

    /**
     * 取消订单
     */
    void cancelOrder(Long userId, Long orderId, String reason);

    /**
     * 确认收货
     */
    void confirmReceive(Long userId, Long orderId);

    /**
     * 删除订单
     */
    void deleteOrder(Long userId, Long orderId);

    /**
     * 获取订单数量统计
     */
    OrderStatisticsVO getOrderStatistics(Long userId);

    // ============== 管理员接口 ==============

    /**
     * 管理员获取订单列表
     */
    Page<OrderVO> getAdminOrderList(OrderQueryDTO queryDTO);

    /**
     * 管理员获取订单详情
     */
    OrderVO getAdminOrderById(Long orderId);

    /**
     * 管理员发货
     */
    void shipOrder(Long orderId, String logisticsCompany, String logisticsNo);

    /**
     * 管理员取消订单
     */
    void adminCancelOrder(Long orderId, String reason);

    /**
     * 管理员删除订单
     */
    void adminDeleteOrder(Long orderId);

    /**
     * 管理员获取订单统计
     */
    OrderStatisticsVO getAdminOrderStatistics();

    /**
     * 订单统计VO
     */
    class OrderStatisticsVO {
        private Integer totalOrders;
        private Integer pendingPayment;
        private Integer pendingShipment;
        private Integer shipped;
        private Integer completed;
        private Integer cancelled;

        public Integer getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(Integer totalOrders) {
            this.totalOrders = totalOrders;
        }

        public Integer getPendingPayment() {
            return pendingPayment;
        }

        public void setPendingPayment(Integer pendingPayment) {
            this.pendingPayment = pendingPayment;
        }

        public Integer getPendingShipment() {
            return pendingShipment;
        }

        public void setPendingShipment(Integer pendingShipment) {
            this.pendingShipment = pendingShipment;
        }

        public Integer getShipped() {
            return shipped;
        }

        public void setShipped(Integer shipped) {
            this.shipped = shipped;
        }

        public Integer getCompleted() {
            return completed;
        }

        public void setCompleted(Integer completed) {
            this.completed = completed;
        }

        public Integer getCancelled() {
            return cancelled;
        }

        public void setCancelled(Integer cancelled) {
            this.cancelled = cancelled;
        }
    }
}
