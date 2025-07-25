package com.proshine.system.security;

import com.proshine.system.entity.SysUser;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户主体类，实现Spring Security的UserDetails接口
 */
@Data
public class UserPrincipal implements UserDetails {
    
    private String userId;
    private String username;
    private String password;
    private String realName;
    private String customerId;
    private SysUser.Status status;
    private List<String> authorities;
    
    public UserPrincipal(SysUser user, List<String> authorities) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.realName = user.getRealName();
        this.customerId = user.getCustomerId();
        this.status = user.getStatus();
        this.authorities = authorities;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return status == SysUser.Status.NORMAL;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return status == SysUser.Status.NORMAL;
    }
}