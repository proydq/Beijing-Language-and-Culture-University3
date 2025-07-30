package com.proshine.system.dto;

import lombok.Data;

/**
 * 添加教室结果
 * 
 * @author system
 * @date 2024-01-01
 */
@Data
public class ClassroomAddResult {

    /**
     * 新生成的教室ID
     */
    private String id;

    /**
     * 构造方法
     */
    public ClassroomAddResult() {}

    /**
     * 构造方法
     * 
     * @param id 教室ID
     */
    public ClassroomAddResult(String id) {
        this.id = id;
    }

    /**
     * 静态工厂方法
     * 
     * @param id 教室ID
     * @return ClassroomAddResult
     */
    public static ClassroomAddResult of(String id) {
        return new ClassroomAddResult(id);
    }
}