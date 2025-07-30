package com.proshine.system.dto;

import lombok.Data;

import java.util.List;

/**
 * 导入结果
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ImportResult {

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failCount;

    /**
     * 错误信息列表
     */
    private List<String> errors;

    /**
     * 构造方法
     */
    public ImportResult() {}

    /**
     * 构造方法
     * 
     * @param successCount 成功数量
     * @param failCount 失败数量
     * @param errors 错误信息列表
     */
    public ImportResult(Integer successCount, Integer failCount, List<String> errors) {
        this.successCount = successCount;
        this.failCount = failCount;
        this.errors = errors;
    }

    /**
     * 静态工厂方法
     * 
     * @param successCount 成功数量
     * @param failCount 失败数量
     * @param errors 错误信息列表
     * @return ImportResult
     */
    public static ImportResult of(Integer successCount, Integer failCount, List<String> errors) {
        return new ImportResult(successCount, failCount, errors);
    }
}