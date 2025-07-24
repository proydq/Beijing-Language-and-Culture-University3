package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.SearchAccountCondition;
import com.proshine.system.entity.Account;
import com.proshine.system.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/account")
@Slf4j
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/search")
    public ResponseEntity<ResponsePageDataEntity<Account>> searchAccount(@RequestBody SearchAccountCondition condition) {
        try {
            ResponsePageDataEntity<Account> data = accountService.search(condition);
            return ResponseEntity.success(data);
        } catch (Exception e) {
            log.error("查询系统账号失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Account> save(@RequestBody Account account) {
        try {
            Account saved = accountService.save(account);
            return ResponseEntity.success(saved);
        } catch (Exception e) {
            log.error("保存账号失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    @GetMapping("/findById")
    public ResponseEntity<Account> findById(@RequestParam String id) {
        try {
            Account account = accountService.findById(id);
            return ResponseEntity.success(account);
        } catch (Exception e) {
            log.error("查询账号失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@RequestParam String id) {
        try {
            accountService.delete(id);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("删除账号失败：", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }
}
