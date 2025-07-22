package com.proshine.service;

import com.proshine.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoleService {
    Page<Role> findAll(Pageable pageable);
    Role save(Role role);
}
