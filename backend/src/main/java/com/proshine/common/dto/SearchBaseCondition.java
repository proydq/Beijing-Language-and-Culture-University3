package com.proshine.common.dto;

import lombok.Data;

@Data
public class SearchBaseCondition {
    private String customerId;
    private Integer pageNumber = 1;
    private Integer pageSize = 10;
}
