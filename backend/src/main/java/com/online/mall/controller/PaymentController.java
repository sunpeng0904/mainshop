package com.online.mall.controller;

import com.online.mall.common.Result;
import com.online.mall.dto.PaymentDTO;
import com.online.mall.service.PaymentService;
import com.online.mall.service.WechatPayService;
import com.online.mall.service.impl.WechatPayServiceImpl;
import com.online.mall.vo.PaymentVO;
import com.online.mall.vo.WechatPayVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/payment")
@Validated
@Tag(name = "支付管理", description = "支付相关接口")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private WechatPayService wechatPayService;

    @Operation(summary = "创建支付")
    @PostMapping("/create")
    public Result<PaymentVO> createPayment(
            HttpServletRequest request,
            @Valid @RequestBody PaymentDTO paymentDTO) {
        Long userId = (Long) request.getAttribute("userId");
        PaymentVO paymentVO = paymentService.createPayment(userId, paymentDTO);
        return Result.success(paymentVO, "支付创建成功");
    }

    @Operation(summary = "创建微信Native支付（扫码支付）")
    @PostMapping("/wechat/native/{orderId}")
    public Result<WechatPayVO> createWechatNativePayment(
            HttpServletRequest request,
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        WechatPayVO vo = paymentService.createWechatNativePayment(userId, orderId);
        return Result.success(vo, "微信支付订单创建成功");
    }

    @Operation(summary = "查询微信支付状态")
    @GetMapping("/wechat/status/{orderNo}")
    public Result<String> queryWechatPayStatus(
            @Parameter(description = "订单编号") @PathVariable String orderNo) {
        String status = wechatPayService.queryPayStatus(orderNo);
        return Result.success(status);
    }

    @Operation(summary = "微信支付回调")
    @PostMapping("/wechat/callback")
    public String wechatPayCallback(@RequestBody String notifyData) {
        try {
            log.info("收到微信支付回调: {}", notifyData);
            boolean success = paymentService.handleWechatCallback(notifyData);
            if (success) {
                return "{\"code\":\"SUCCESS\",\"message\":\"成功\"}";
            } else {
                return "{\"code\":\"FAIL\",\"message\":\"失败\"}";
            }
        } catch (Exception e) {
            log.error("微信支付回调处理失败", e);
            return "{\"code\":\"FAIL\",\"message\":\"" + e.getMessage() + "\"}";
        }
    }

    @Operation(summary = "模拟微信支付成功（沙箱测试）")
    @PostMapping("/wechat/mock-success/{orderNo}")
    public Result<Void> mockWechatPaySuccess(
            @Parameter(description = "订单编号") @PathVariable String orderNo) {
        ((WechatPayServiceImpl) wechatPayService).mockPaySuccess(orderNo);
        paymentService.mockWechatPaySuccess(orderNo);
        return Result.success(null, "模拟支付成功");
    }

    @Operation(summary = "模拟支付成功（用于测试）")
    @PostMapping("/mock-success/{paymentNo}")
    public Result<PaymentVO> mockPaymentSuccess(
            HttpServletRequest request,
            @Parameter(description = "支付流水号") @PathVariable String paymentNo) {
        Long userId = (Long) request.getAttribute("userId");
        PaymentVO paymentVO = paymentService.mockPaymentSuccess(userId, paymentNo);
        return Result.success(paymentVO, "支付成功");
    }

    @Operation(summary = "申请退款")
    @PostMapping("/refund/{orderId}")
    public Result<Void> refund(
            HttpServletRequest request,
            @Parameter(description = "订单ID") @PathVariable Long orderId,
            @Parameter(description = "退款原因") @RequestParam(required = false) String reason) {
        Long userId = (Long) request.getAttribute("userId");
        paymentService.refund(userId, orderId, reason);
        return Result.success(null, "退款申请成功");
    }

    @Operation(summary = "根据订单ID获取支付记录")
    @GetMapping("/order/{orderId}")
    public Result<PaymentVO> getPaymentByOrderId(
            HttpServletRequest request,
            @Parameter(description = "订单ID") @PathVariable Long orderId) {
        Long userId = (Long) request.getAttribute("userId");
        PaymentVO paymentVO = paymentService.getPaymentByOrderId(userId, orderId);
        return Result.success(paymentVO);
    }

    @Operation(summary = "根据支付流水号获取支付记录")
    @GetMapping("/{paymentNo}")
    public Result<PaymentVO> getPaymentByPaymentNo(
            HttpServletRequest request,
            @Parameter(description = "支付流水号") @PathVariable String paymentNo) {
        Long userId = (Long) request.getAttribute("userId");
        PaymentVO paymentVO = paymentService.getPaymentByPaymentNo(userId, paymentNo);
        return Result.success(paymentVO);
    }

    @Operation(summary = "支付回调（第三方支付调用）")
    @PostMapping("/callback")
    public String paymentCallback(
            @Parameter(description = "支付流水号") @RequestParam String paymentNo,
            @Parameter(description = "第三方交易号") @RequestParam String tradeNo,
            @RequestBody(required = false) String notifyData) {
        try {
            paymentService.handlePaymentCallback(paymentNo, tradeNo, notifyData);
            return "success";
        } catch (Exception e) {
            log.error("支付回调处理失败", e);
            return "fail";
        }
    }
}
