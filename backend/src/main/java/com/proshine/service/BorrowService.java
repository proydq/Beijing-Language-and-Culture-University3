package com.proshine.service;

import com.proshine.entity.Borrow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BorrowService {
    Page<Borrow> findAll(Pageable pageable);
    Borrow save(Borrow borrow);
}
