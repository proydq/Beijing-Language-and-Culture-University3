package com.proshine.controller;

import com.proshine.entity.Borrow;
import com.proshine.response.ResponseEntity;
import com.proshine.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    private final BorrowService borrowService;

    @Autowired
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping
    public ResponseEntity<Page<Borrow>> list(Pageable pageable) {
        return ResponseEntity.success(borrowService.findAll(pageable));
    }

    @PostMapping
    public ResponseEntity<Borrow> create(@RequestBody Borrow borrow) {
        return ResponseEntity.success(borrowService.save(borrow));
    }
}
