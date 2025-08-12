package com.proshine.system.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleBindReq {
  private String customerId;
  private String roleId;
  private List<String> resourceIdList;
  private String categoryCode;
}
