package com.proshine.service.impl;

import com.proshine.entity.AccessPermission;
import com.proshine.repository.AccessPermissionRepository;
import com.proshine.service.AccessPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AccessPermissionServiceImpl implements AccessPermissionService {

    private final AccessPermissionRepository repository;

    @Autowired
    public AccessPermissionServiceImpl(AccessPermissionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<AccessPermission> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public AccessPermission save(AccessPermission permission) {
        return repository.save(permission);
    }
}
