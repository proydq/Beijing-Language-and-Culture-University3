package com.proshine.system.controller;

import com.proshine.common.response.ResponseEntity;
import com.proshine.system.entity.SysUser;
import com.proshine.system.security.JwtTokenUtil;
import com.proshine.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/authentication")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username, @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Map<String, Object> claims = new HashMap<>();
        String token = jwtTokenUtil.generateToken(username, claims);
        return ResponseEntity.success(Collections.singletonMap("token", token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.success(null);
    }

    @GetMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String username = jwtTokenUtil.getUsernameFromToken(token);
        Map<String, Object> claims = new HashMap<>();
        String newToken = jwtTokenUtil.generateToken(username, claims);
        return ResponseEntity.success(Collections.singletonMap("token", newToken));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestParam String username, @RequestParam String newPassword) {
        SysUser user = userService.findByUsername(username).orElse(null);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.save(user);
        }
        return ResponseEntity.success(null);
    }
}
