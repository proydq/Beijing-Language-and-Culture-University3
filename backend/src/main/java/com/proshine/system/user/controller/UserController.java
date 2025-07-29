package com.proshine.system.user.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.common.response.ResponsePageDataEntity;
import com.proshine.system.dto.SearchAvailableUserCondition;
import com.proshine.system.entity.SysUser;
import com.proshine.system.service.BookingPersonnelPermissionService;
import com.proshine.system.user.dto.SearchUserCondition;
import com.proshine.system.user.dto.UserSaveRequest;
import com.proshine.system.user.dto.UserVO;
import com.proshine.system.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private BookingPersonnelPermissionService permissionService;

    /**
     * Pagination search users
     */
    @PostMapping("/search")
    public ResponseEntity<ResponsePageDataEntity<UserVO>> search(@RequestBody SearchUserCondition condition) {
        try {
            ResponsePageDataEntity<UserVO> result = userService.search(condition);
            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * search by organization
     */
    @PostMapping("/search/by-organization")
    public ResponseEntity<ResponsePageDataEntity<UserVO>> searchByOrg(@RequestBody SearchUserCondition condition) {
        return search(condition);
    }

    /**
     * Get detail
     */
    @GetMapping("/{id}")
    public ResponseEntity<SysUser> detail(@PathVariable String id) {
        try {
            SysUser vo = userService.findById(id);
            return ResponseEntity.success(vo);
        } catch (Exception e) {
            log.error("查询用户详情失败", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * Create user
     */
    @PostMapping
    public ResponseEntity<UserVO> create(@RequestBody UserSaveRequest request) {
        try {
            UserVO vo = userService.create(request);
            return ResponseEntity.success(vo);
        } catch (Exception e) {
            log.error("新增用户失败", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * Update user
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserVO> update(@PathVariable String id, @RequestBody UserSaveRequest request) {
        try {
            UserVO vo = userService.update(id, request);
            return ResponseEntity.success(vo);
        } catch (Exception e) {
            log.error("编辑用户失败", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            userService.delete(id);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }

    /**
     * Export list
     */
    @GetMapping("/export")
    public void export(SearchUserCondition condition, HttpServletResponse response) {
        userService.exportExcel(condition, response);
    }

    /**
     * Import users
     */
    @PostMapping("/import")
    public ResponseEntity<Void> importData(@RequestParam("file") MultipartFile file) {
        try {
            userService.importExcel(file);
            return ResponseEntity.success(null);
        } catch (Exception e) {
            log.error("导入用户数据失败", e);
            return ResponseEntity.fail(e.getMessage());
        }
    }


    /**
     * 获取可选择的用户列表
     */
    @GetMapping("/available")
    public ResponseEntity<ResponsePageDataEntity<UserVO>> getAvailableUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String realNameAndJobNumber,
            @RequestParam(required = false) String departmentId) {

        try {
            SearchAvailableUserCondition condition = new SearchAvailableUserCondition();
            condition.setPageNum(pageNum);
            condition.setPageSize(pageSize);
            condition.setRealNameAndJobNumber(realNameAndJobNumber);
            condition.setDepartmentId(departmentId);

            ResponsePageDataEntity<UserVO> result = permissionService.getAvailableUsers(condition);

            return ResponseEntity.success(result);
        } catch (Exception e) {
            log.error("获取可选用户列表失败：", e);
            return ResponseEntity.fail("获取可选用户列表失败：" + e.getMessage());
        }
    }


}
