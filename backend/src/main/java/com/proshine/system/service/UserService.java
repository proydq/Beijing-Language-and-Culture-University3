package com.proshine.system.service;

import com.proshine.system.entity.SysUser;

import java.util.Optional;
import java.util.List;

public interface UserService {
    Optional<SysUser> findByUsername(String username);

    SysUser save(SysUser user);

    List<SysUser> getAll();
}
