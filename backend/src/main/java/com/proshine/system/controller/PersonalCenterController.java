package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.UserProfileDto;
import com.proshine.system.entity.User;
import com.proshine.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/personal-center")
@RequiredArgsConstructor
public class PersonalCenterController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> profile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<String> codes = userService.getPermissionCodes(user.getId());
        UserProfileDto dto = new UserProfileDto();
        dto.setUser(user);
        dto.setPermissionCodes(codes);
        return ResponseEntity.success(dto);
    }
}
