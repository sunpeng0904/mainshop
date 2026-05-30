package com.online.mall.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.common.Result;
import com.online.mall.entity.InsiderInfo;
import com.online.mall.service.InsiderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 后台管理 - 内幕信息知情人登记Controller
 */
@RestController
@RequestMapping("/admin/insider")
@PreAuthorize("hasRole('ADMIN')")
public class AdminInsiderController {

    @Autowired
    private InsiderInfoService insiderInfoService;

    /**
     * 获取内幕信息知情人列表
     */
    @GetMapping("/list")
    public Result<Page<InsiderInfo>> getInsiderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String board,
            @RequestParam(required = false) String financingType,
            @RequestParam(required = false) String acceptTimeStart,
            @RequestParam(required = false) String acceptTimeEnd,
            @RequestParam(required = false) String knowledgeTimeStart,
            @RequestParam(required = false) String knowledgeTimeEnd,
            @RequestParam(required = false) String registerTimeStart,
            @RequestParam(required = false) String registerTimeEnd,
            @RequestParam(required = false) String insiderName,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String status) {

        Page<InsiderInfo> insiderPage = insiderInfoService.getInsiderList(page, pageSize, companyName, industry,
                board, financingType, acceptTimeStart, acceptTimeEnd, knowledgeTimeStart, knowledgeTimeEnd,
                registerTimeStart, registerTimeEnd, insiderName, reason, content, status);
        return Result.success(insiderPage);
    }

    /**
     * 获取内幕信息知情人详情
     */
    @GetMapping("/{id}")
    public Result<InsiderInfo> getInsiderDetail(@PathVariable Long id) {
        InsiderInfo insiderInfo = insiderInfoService.getById(id);
        if (insiderInfo == null) {
            return Result.error("记录不存在");
        }
        return Result.success(insiderInfo);
    }

    /**
     * 创建内幕信息知情人登记
     */
    @PostMapping
    public Result<Void> createInsider(@RequestBody InsiderInfo insiderInfo) {
        boolean success = insiderInfoService.createInsider(insiderInfo);
        return success ? Result.success() : Result.error("创建失败");
    }

    /**
     * 更新内幕信息知情人登记
     */
    @PutMapping("/{id}")
    public Result<Void> updateInsider(@PathVariable Long id, @RequestBody InsiderInfo insiderInfo) {
        insiderInfo.setId(id);
        boolean success = insiderInfoService.updateInsider(insiderInfo);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除内幕信息知情人登记
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteInsider(@PathVariable Long id) {
        boolean success = insiderInfoService.deleteInsider(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 更新状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        boolean success = insiderInfoService.updateStatus(id, status);
        return success ? Result.success() : Result.error("操作失败");
    }
}
