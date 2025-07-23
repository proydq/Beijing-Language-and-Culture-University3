package com.proshine.service.impl;

import com.proshine.entity.Borrow;
import com.proshine.repository.BorrowRepository;
import com.proshine.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository;

    @Autowired
    public BorrowServiceImpl(BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }

    @Override
    public Page<Borrow> findAll(Pageable pageable) {
        return borrowRepository.findAll(pageable);
    }

    @Override
    public Borrow save(Borrow borrow) {
        return borrowRepository.save(borrow);
    }
}
