package com.proshine.common.security;

import com.proshine.system.service.RoleResourceService;
import com.proshine.system.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathMatcher;
import org.springframework.stereotype.Component;
import org.springframework.security.web.FilterInvocation;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CustomFilterSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

  private final AntPathMatcher ant = new AntPathMatcher();
  @Value("${security.permit-paths:/login,/error,/actuator/**}")
  private String[] permitPaths;

  private final RoleResourceService roleResourceService;

  public CustomFilterSecurityMetadataSource(RoleResourceService roleResourceService) {
    this.roleResourceService = roleResourceService;
  }

  @Override
  public Collection<ConfigAttribute> getAttributes(Object object) {
    HttpServletRequest req = ((FilterInvocation) object).getHttpRequest();
    String uri = req.getRequestURI();

    for (String p : permitPaths) if (ant.match(p, uri)) return null;

    if (SecurityUtil.isSystemAdministrator()) return null;

    Map<String, Set<String>> url2Roles = roleResourceService.loadUrlRoleMap(SecurityUtil.getCustomerId());
    for (Map.Entry<String, Set<String>> e : url2Roles.entrySet()) {
      if (ant.match(e.getKey(), uri)) {
        Set<String> roles = e.getValue();
        if (roles.isEmpty()) return SecurityConfig.createList("ROLE_NONE_EXISTING");
        return roles.stream().map(SecurityConfig::new).collect(Collectors.toList());
      }
    }
    return SecurityConfig.createList("ROLE_NONE_EXISTING");
  }

  @Override public Collection<ConfigAttribute> getAllConfigAttributes() { return null; }
  @Override public boolean supports(Class<?> clazz) { return FilterInvocation.class.isAssignableFrom(clazz); }
}
