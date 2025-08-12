package com.proshine.common.security;

import com.proshine.system.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.config.annotation.ObjectPostProcessor;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
                                         FilterInvocationSecurityMetadataSource metadataSource,
                                         AccessDecisionManager adm,
                                         JwtAuthenticationFilter jwt) throws Exception {
    http.csrf().disable();
    http.addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);
    http.authorizeRequests(reg -> reg.anyRequest().authenticated())
        .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
          @Override public <O extends FilterSecurityInterceptor> O postProcess(O fsi) {
            fsi.setSecurityMetadataSource(metadataSource);
            fsi.setAccessDecisionManager(adm);
            return fsi;
          }
        });
    return http.build();
  }
}
