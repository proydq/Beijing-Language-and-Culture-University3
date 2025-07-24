package com.proshine.common.response;

import lombok.Data;

import java.util.List;

@Data
public class ResponsePageDataEntity<T> {
    private Long total;
    private List<T> rows;
}
