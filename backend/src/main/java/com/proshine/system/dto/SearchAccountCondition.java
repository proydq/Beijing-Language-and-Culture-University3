package com.proshine.system.dto;

import com.proshine.common.dto.SearchBaseCondition;
import com.proshine.system.entity.Account;
import lombok.Data;

@Data
public class SearchAccountCondition extends SearchBaseCondition {
    private String name;
    private String email;
    private Account.Status status = Account.Status.NORMAL;
}
