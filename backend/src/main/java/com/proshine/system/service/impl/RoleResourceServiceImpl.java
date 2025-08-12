package com.proshine.system.service.impl;

import com.proshine.system.entity.SysPermission;
import com.proshine.system.entity.SysRole;
import com.proshine.system.entity.SysRolePermission;
import com.proshine.system.repository.SysPermissionApiRepository;
import com.proshine.system.repository.SysPermissionRepository;
import com.proshine.system.repository.SysRolePermissionRepository;
import com.proshine.system.repository.SysRoleRepository;
import com.proshine.system.repository.SysWebPageInterfaceRelRepository;
import com.proshine.system.service.RoleResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoleResourceServiceImpl implements RoleResourceService {

  @Autowired private SysRolePermissionRepository rolePermRepo;
  @Autowired private SysPermissionRepository permRepo;
  @Autowired private SysWebPageInterfaceRelRepository relRepo;
  @Autowired private SysPermissionApiRepository apiRepo;
  @Autowired private SysRoleRepository roleRepo;

  @Transactional
  @Override
  public void bindRoleAndResources(String customerId, String roleId, List<String> resourceIds, String category) {
    rolePermRepo.deleteByRoleId(roleId);

    List<SysRolePermission> batch = new ArrayList<>();
    for (String rid : resourceIds) {
      SysRolePermission rp = new SysRolePermission();
      rp.setRoleId(roleId); rp.setPermissionId(rid);
      batch.add(rp);

      List<String> interfaces = relRepo.findInterfaceIdsByWebResourceId(rid);
      for (String iid : interfaces) {
        SysRolePermission rpIf = new SysRolePermission();
        rpIf.setRoleId(roleId); rpIf.setPermissionId(iid);
        batch.add(rpIf);
      }
    }
    if (!batch.isEmpty()) rolePermRepo.saveAll(batch);
  }

  @Override
  public Map<String, Set<String>> loadUrlRoleMap(String customerId) {
    Map<String, Set<String>> map = new HashMap<>();
    List<SysPermission> apis = permRepo.findByTypeAndCustomerIdAndDeletedFalse(SysPermission.Type.API, customerId);
    for (SysPermission api : apis) {
      String pattern = apiRepo.findAntPatternByPermCode(api.getCode());
      if (pattern == null) continue;

      Set<String> roleCodes = new HashSet<>();
      roleCodes.add("ROLE_ADMIN");

      List<SysRolePermission> rps = rolePermRepo.findByPermissionId(api.getId());
      if (!rps.isEmpty()) {
        List<String> roleIds = new ArrayList<>();
        for (SysRolePermission rp : rps) roleIds.add(rp.getRoleId());
        List<SysRole> roles = roleRepo.findAllById(roleIds);
        for (SysRole r : roles) if (r.getCode() != null) roleCodes.add(r.getCode());
      }
      map.put(pattern, roleCodes);
    }
    return map;
  }
}
