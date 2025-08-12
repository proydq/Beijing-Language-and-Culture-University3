package com.proshine.system.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "sys_web_page_interface_rel", uniqueConstraints = {
    @UniqueConstraint(name = "uk_bind", columnNames = {"web_page_resource_id", "interface_resource_id"})
})
@Data
public class SysWebPageInterfaceRel {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid")
  @Column(name = "id", columnDefinition = "VARCHAR(32)")
  private String id;

  @Column(name = "web_page_resource_id", columnDefinition = "VARCHAR(32)" , nullable = false)
  private String webPageResourceId;

  @Column(name = "interface_resource_id", columnDefinition = "VARCHAR(32)" , nullable = false)
  private String interfaceResourceId;
}
