package com.proshine.system.controller;

import com.proshine.system.dto.RoleBindReq;
import com.proshine.system.service.RoleResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/roleResource")
public class RoleBindController {

  @Autowired private RoleResourceService service;

  @PostMapping("/saveBindingRelationship")
  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  public ResponseEntity<?> save(@RequestBody RoleBindReq req){
    service.bindRoleAndResources(req.getCustomerId(), req.getRoleId(), req.getResourceIdList(), req.getCategoryCode());
    return ResponseEntity.ok().build();
  }
}
