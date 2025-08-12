package com.proshine.system.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RoleResourceService {
  void bindRoleAndResources(String customerId, String roleId, List<String> resourceIds, String category);
  Map<String, Set<String>> loadUrlRoleMap(String customerId);
}
