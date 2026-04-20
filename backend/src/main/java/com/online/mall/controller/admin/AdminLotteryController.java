package com.online.mall.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.online.mall.common.Result;
import com.online.mall.entity.LotteryPrize;
import com.online.mall.entity.LotteryRecord;
import com.online.mall.mapper.LotteryRecordMapper;
import com.online.mall.service.LotteryService;
import com.online.mall.vo.LotteryRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;

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

    @Autowired
    private LotteryRecordMapper recordMapper;

    @Operation(summary = "获取奖品列表")
    @GetMapping("/prizes")
    public Result<List<LotteryPrize>> getPrizeList() {
        List<LotteryPrize> prizes = lotteryService.list(
            new LambdaQueryWrapper<LotteryPrize>().orderByAsc(LotteryPrize::getSort)
        );
        return Result.success(prizes);
    }

    @Operation(summary = "新增奖品")
    @PostMapping("/prizes")
    public Result<Void> createPrize(@RequestBody LotteryPrize prize) {
        lotteryService.save(prize);
        return Result.success(null, "创建成功");
    }

    @Operation(summary = "更新奖品")
    @PutMapping("/prizes/{id}")
    public Result<Void> updatePrize(@PathVariable Long id, @RequestBody LotteryPrize prize) {
        prize.setId(id);
        lotteryService.updateById(prize);
        return Result.success(null, "更新成功");
    }

    @Operation(summary = "删除奖品")
    @DeleteMapping("/prizes/{id}")
    public Result<Void> deletePrize(@PathVariable Long id) {
        lotteryService.removeById(id);
        return Result.success(null, "删除成功");
    }

    @Operation(summary = "初始化奖品数据")
    @PostMapping("/init")
    public Result<Void> initPrizes() {
        lotteryService.initDefaultPrizes();
        return Result.success(null, "初始化成功");
    }

    @Operation(summary = "获取中奖记录列表")
    @GetMapping("/records")
    public Result<Page<LotteryRecord>> getRecords(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "奖品ID") @RequestParam(required = false) Long prizeId,
            @Parameter(description = "奖品等级") @RequestParam(required = false) Integer prizeLevel,
            @Parameter(description = "领取状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime) {

        Page<LotteryRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LotteryRecord> wrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.isEmpty()) {
            wrapper.like(LotteryRecord::getUsername, username);
        }
        if (prizeId != null) {
            wrapper.eq(LotteryRecord::getPrizeId, prizeId);
        }
        if (prizeLevel != null) {
            wrapper.eq(LotteryRecord::getPrizeLevel, prizeLevel);
        }
        if (status != null) {
            wrapper.eq(LotteryRecord::getReceiveStatus, status);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(LotteryRecord::getLotteryTime, startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(LotteryRecord::getLotteryTime, endTime);
        }

        wrapper.orderByDesc(LotteryRecord::getLotteryTime);
        Page<LotteryRecord> result = recordMapper.selectPage(page, wrapper);
        return Result.success(result);
    }

    @Operation(summary = "获取抽奖统计数据")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // 总抽奖次数
        long totalDraws = recordMapper.selectCount(
            new LambdaQueryWrapper<LotteryRecord>()
        );
        stats.put("totalDraws", totalDraws);

        // 中奖人数（非谢谢参与）
        long winCount = recordMapper.selectCount(
            new LambdaQueryWrapper<LotteryRecord>().lt(LotteryRecord::getPrizeLevel, 4)
        );
        stats.put("winCount", winCount);

        // 待领取
        long pendingCount = recordMapper.selectCount(
            new LambdaQueryWrapper<LotteryRecord>()
                .eq(LotteryRecord::getReceiveStatus, 0)
                .lt(LotteryRecord::getPrizeLevel, 4)
        );
        stats.put("pendingCount", pendingCount);

        // 已领取
        long receivedCount = recordMapper.selectCount(
            new LambdaQueryWrapper<LotteryRecord>()
                .eq(LotteryRecord::getReceiveStatus, 1)
                .lt(LotteryRecord::getPrizeLevel, 4)
        );
        stats.put("receivedCount", receivedCount);

        return Result.success(stats);
    }

    @Operation(summary = "导出中奖记录")
    @GetMapping("/records/export")
    public void exportRecords(
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "奖品ID") @RequestParam(required = false) Long prizeId,
            @Parameter(description = "奖品等级") @RequestParam(required = false) Integer prizeLevel,
            @Parameter(description = "领取状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            HttpServletResponse response) throws IOException {

        // 构建查询条件
        LambdaQueryWrapper<LotteryRecord> wrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.isEmpty()) {
            wrapper.like(LotteryRecord::getUsername, username);
        }
        if (prizeId != null) {
            wrapper.eq(LotteryRecord::getPrizeId, prizeId);
        }
        if (prizeLevel != null) {
            wrapper.eq(LotteryRecord::getPrizeLevel, prizeLevel);
        }
        if (status != null) {
            wrapper.eq(LotteryRecord::getReceiveStatus, status);
        }
        if (startTime != null && !startTime.isEmpty()) {
            wrapper.ge(LotteryRecord::getLotteryTime, startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            wrapper.le(LotteryRecord::getLotteryTime, endTime);
        }

        wrapper.orderByDesc(LotteryRecord::getLotteryTime);
        List<LotteryRecord> records = recordMapper.selectList(wrapper);

        // 创建 Excel 工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("中奖记录");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "用户名", "奖品ID", "奖品名称", "奖品等级", "抽奖时间", "领取状态", "领取时间", "备注"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            cell.setCellStyle(headerStyle);
        }

        // 填充数据
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int rowNum = 1;
        for (LotteryRecord record : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(record.getId());
            row.createCell(1).setCellValue(record.getUsername());
            row.createCell(2).setCellValue(record.getPrizeId());
            row.createCell(3).setCellValue(record.getPrizeName());
            row.createCell(4).setCellValue(getLevelName(record.getPrizeLevel()));
            row.createCell(5).setCellValue(record.getLotteryTime().format(formatter));
            row.createCell(6).setCellValue(record.getReceiveStatus() == 1 ? "已领取" : "待领取");
            row.createCell(7).setCellValue(record.getReceiveTime() != null ?
                record.getReceiveTime().format(formatter) : "-");
            row.createCell(8).setCellValue(record.getRemark() != null ? record.getRemark() : "-");
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 设置响应头
        String fileName = URLEncoder.encode("中奖记录.xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

        // 写入响应流
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    private String getLevelName(Integer level) {
        if (level == null) return "";
        switch (level) {
            case 1: return "一等奖";
            case 2: return "二等奖";
            case 3: return "三等奖";
            case 4: return "四等奖";
            case 5: return "五等奖";
            default: return level + "等奖";
        }
    }
}
