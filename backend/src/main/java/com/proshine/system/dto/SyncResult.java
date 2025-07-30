package com.proshine.system.dto;

import lombok.Data;

/**
 * 同步结果
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class SyncResult {

    /**
     * 同步数量
     */
    private Integer syncCount;

    /**
     * 构造方法
     */
    public SyncResult() {}

    /**
     * 构造方法
     * 
     * @param syncCount 同步数量
     */
    public SyncResult(Integer syncCount) {
        this.syncCount = syncCount;
    }

    /**
     * 静态工厂方法
     * 
     * @param syncCount 同步数量
     * @return SyncResult
     */
    public static SyncResult of(Integer syncCount) {
        return new SyncResult(syncCount);
    }
}