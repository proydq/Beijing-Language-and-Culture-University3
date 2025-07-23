package com.proshine.service;

import com.proshine.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    Page<Admin> findAll(Pageable pageable);
    Admin save(Admin admin);
}
