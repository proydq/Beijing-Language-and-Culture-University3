package com.proshine.system.service;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.SearchAccountCondition;
import com.proshine.system.entity.Account;

public interface AccountService {
    ResponsePageDataEntity<Account> search(SearchAccountCondition condition);

    Account save(Account account);

    Account findById(String id);

    void delete(String id);
}
