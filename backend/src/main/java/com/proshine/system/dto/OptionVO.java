package com.proshine.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple label-value option VO.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionVO {
    private String label;
    private String value;
}
