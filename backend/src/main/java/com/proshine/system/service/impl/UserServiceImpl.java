package com.proshine.system.service.impl;

import com.proshine.system.entity.SysUser;
import com.proshine.system.repository.SysUserRepository;
import com.proshine.system.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private SysUserRepository userRepository;

    @Override
    public Optional<SysUser> findByUsername(String username) {
        return userRepository.findByUsernameAndDeletedFalse(username);
    }

    @Override
    public SysUser save(SysUser user) {
        return userRepository.save(user);
    }

    @Override
    public List<SysUser> getAll() {
        return userRepository.findAll();
    }
}
