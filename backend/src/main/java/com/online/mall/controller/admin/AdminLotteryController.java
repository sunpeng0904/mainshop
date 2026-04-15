package com.online.mall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.common.Result;
import com.online.mall.service.LotteryService;
import com.online.mall.vo.LotteryRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员抽奖活动控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/lottery")
@Tag(name = "管理员-抽奖管理", description = "抽奖活动管理接口")
public class AdminLotteryController {

    @Autowired
    private LotteryService lotteryService;

    @Operation(summary = "初始化奖品数据")
    @PostMapping("/init")
    public Result<Void> initPrizes() {
        lotteryService.initDefaultPrizes();
        return Result.success(null, "初始化成功");
    }

    @Operation(summary = "获取中奖记录列表")
    @GetMapping("/records")
    public Result<Page<LotteryRecordVO>> getRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "奖品等级") @RequestParam(required = false) Integer prizeLevel) {
        Page<LotteryRecordVO> page = lotteryService.getRecordPage(pageNum, pageSize, prizeLevel);
        return Result.success(page);
    }
}
