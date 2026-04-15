package com.online.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.common.Result;
import com.online.mall.service.LotteryService;
import com.online.mall.vo.LotteryPrizeVO;
import com.online.mall.vo.LotteryRecordVO;
import com.online.mall.vo.LotteryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 抽奖活动控制器
 */
@Slf4j
@RestController
@RequestMapping("/lottery")
@Tag(name = "抽奖活动", description = "五一劳动节大转盘抽奖")
public class LotteryController {

    @Autowired
    private LotteryService lotteryService;

    @Operation(summary = "获取奖品列表")
    @GetMapping("/prizes")
    public Result<List<LotteryPrizeVO>> getPrizeList() {
        List<LotteryPrizeVO> prizes = lotteryService.getPrizeList();
        return Result.success(prizes);
    }

    @Operation(summary = "获取剩余抽奖次数")
    @GetMapping("/remaining-times")
    public Result<Integer> getRemainingTimes(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Integer times = lotteryService.getRemainingTimes(userId);
        return Result.success(times);
    }

    @Operation(summary = "执行抽奖")
    @PostMapping("/draw")
    public Result<LotteryVO> doLottery(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        LotteryVO result = lotteryService.doLottery(userId, username);
        return Result.success(result);
    }

    @Operation(summary = "获取我的中奖记录")
    @GetMapping("/my-records")
    public Result<List<LotteryRecordVO>> getMyRecords(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<LotteryRecordVO> records = lotteryService.getUserRecords(userId);
        return Result.success(records);
    }

    @Operation(summary = "领取奖品")
    @PostMapping("/receive/{recordId}")
    public Result<Void> receivePrize(
            HttpServletRequest request,
            @Parameter(description = "记录ID") @PathVariable Long recordId) {
        Long userId = (Long) request.getAttribute("userId");
        lotteryService.receivePrize(userId, recordId);
        return Result.success(null, "领取成功，请联系客服发货");
    }
}
