package com.proshine.system.dto;

import com.proshine.common.dto.SearchBaseCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * DTO for searching positions.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SearchPositionDTO extends SearchBaseCondition {
    /** Position name. */
    private String name;
}
