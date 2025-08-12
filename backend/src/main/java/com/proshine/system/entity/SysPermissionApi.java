package com.proshine.system.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "sys_permission_api", uniqueConstraints = {
    @UniqueConstraint(name = "uk_method_pattern", columnNames = {"http_method", "ant_pattern"})
})
@Data
public class SysPermissionApi {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid")
  @Column(name = "id", columnDefinition = "VARCHAR(32)")
  private String id;

  @Column(name = "http_method", columnDefinition = "VARCHAR(10)")
  private String httpMethod;

  @Column(name = "ant_pattern", columnDefinition = "VARCHAR(200)")
  private String antPattern;

  @Column(name = "perm_code", columnDefinition = "VARCHAR(100)")
  private String permCode;

  @Column(name = "sort")
  private Integer sort;
}
