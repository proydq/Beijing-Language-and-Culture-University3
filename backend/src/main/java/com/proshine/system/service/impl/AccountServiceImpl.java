package com.proshine.system.service.impl;

import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.SearchAccountCondition;
import com.proshine.system.entity.Account;
import com.proshine.system.repository.AccountRepository;
import com.proshine.system.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public ResponsePageDataEntity<Account> search(SearchAccountCondition condition) {
        Specification<Account> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (condition.getName() != null) {
                predicates.add(cb.like(root.get("name"), "%" + condition.getName() + "%"));
            }
            if (condition.getEmail() != null) {
                predicates.add(cb.like(root.get("email"), "%" + condition.getEmail() + "%"));
            }
            if (condition.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), condition.getStatus()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        PageRequest page = PageRequest.of(condition.getPageNumber() - 1, condition.getPageSize());
        Page<Account> result = accountRepository.findAll(spec, page);
        ResponsePageDataEntity<Account> pageData = new ResponsePageDataEntity<>();
        pageData.setTotal(result.getTotalElements());
        pageData.setRows(result.getContent());
        return pageData;
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }

    @Override
    public Account findById(String id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(String id) {
        accountRepository.deleteById(id);
    }
}
