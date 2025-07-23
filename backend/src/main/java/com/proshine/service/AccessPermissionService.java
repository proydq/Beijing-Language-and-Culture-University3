package com.proshine.service;

import com.proshine.entity.AccessPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccessPermissionService {
    Page<AccessPermission> findAll(Pageable pageable);
    AccessPermission save(AccessPermission permission);
}
