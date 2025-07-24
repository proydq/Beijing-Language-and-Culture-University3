package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.dto.LoginRequest;
import com.proshine.system.dto.LoginResponse;
import com.proshine.system.entity.User;
import com.proshine.system.security.JwtUtil;
import com.proshine.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.fail("invalid credentials");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        List<String> codes = userService.getPermissionCodes(user.getId());
        LoginResponse resp = new LoginResponse();
        resp.setToken(token);
        resp.setPermissionCodes(codes);
        return ResponseEntity.success(resp);
    }
}
