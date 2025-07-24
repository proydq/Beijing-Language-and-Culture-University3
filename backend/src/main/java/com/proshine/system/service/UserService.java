package com.proshine.system.service;

import com.proshine.system.entity.User;

import java.util.List;

public interface UserService {
    User findByUsername(String username);
    List<String> getPermissionCodes(String userId);
    void assignRole(String userId, List<String> roleIds);
    User save(User user);
}
