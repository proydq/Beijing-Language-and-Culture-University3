package com.proshine.common.security;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class CustomAccessDecisionManager implements AccessDecisionManager {
  @Override
  public void decide(Authentication auth, Object object, Collection<ConfigAttribute> attrs) {
    if (attrs == null || attrs.isEmpty()) return;
    for (ConfigAttribute need : attrs) {
      String n = need.getAttribute();
      if ("ROLE_NONE_EXISTING".equals(n)) throw new AccessDeniedException("权限不足");
      for (GrantedAuthority ga : auth.getAuthorities()) {
        if (n.equals(ga.getAuthority())) return;
      }
    }
    throw new AccessDeniedException("权限不足");
  }
  @Override public boolean supports(ConfigAttribute attribute) { return true; }
  @Override public boolean supports(Class<?> clazz) { return true; }
}
