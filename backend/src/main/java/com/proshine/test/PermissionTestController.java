package com.proshine.test;

import com.proshine.common.response.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限测试控制器
 */
@RestController
@RequestMapping("/test/permission")
@Slf4j
public class PermissionTestController {
    
    /**
     * 测试当前用户的权限信息
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentPermissions() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> result = new HashMap<>();
        result.put("principal", auth.getPrincipal().toString());
        result.put("authorities", auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toList()));
        result.put("isAuthenticated", auth.isAuthenticated());
        
        log.info("当前用户权限信息：{}", result);
        
        return ResponseEntity.success(result);
    }
    
    /**
     * 需要system:user:view权限的测试接口
     */
    @GetMapping("/need-search")
    @PreAuthorize("hasAuthority('system:user:view')")
    public ResponseEntity<String> needSearchPermission() {
        return ResponseEntity.success("您有system:user:view权限");
    }
    
    /**
     * 需要system:user:create权限的测试接口
     */
    @GetMapping("/need-add")
    @PreAuthorize("hasAuthority('system:user:create')")
    public ResponseEntity<String> needAddPermission() {
        return ResponseEntity.success("您有system:user:create权限");
    }
}