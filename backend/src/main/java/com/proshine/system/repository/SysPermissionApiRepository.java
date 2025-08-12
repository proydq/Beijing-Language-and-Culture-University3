package com.proshine.system.repository;

import com.proshine.system.entity.SysPermissionApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SysPermissionApiRepository extends JpaRepository<SysPermissionApi, String> {
  @Query("select s.antPattern from SysPermissionApi s where s.permCode=?1")
  String findAntPatternByPermCode(String permCode);
}
