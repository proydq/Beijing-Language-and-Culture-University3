package com.proshine.system.repository;

import com.proshine.system.entity.SysWebPageInterfaceRel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SysWebPageInterfaceRelRepository extends JpaRepository<SysWebPageInterfaceRel, String> {
  @Query("select r.interfaceResourceId from SysWebPageInterfaceRel r where r.webPageResourceId=?1")
  List<String> findInterfaceIdsByWebResourceId(String webResId);
}
