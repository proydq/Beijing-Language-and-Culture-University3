package com.proshine.system.dto;

import com.proshine.common.dto.SearchBaseCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Condition for searching titles.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchTitleDTO extends SearchBaseCondition {
    private String name;
}
