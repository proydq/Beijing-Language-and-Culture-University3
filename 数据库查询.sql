-- 检查表结构
DESCRIBE sys_permission;

[
  {
    "Field": "id",
    "Type": "varchar(32)",
    "Null": "NO",
    "Key": "PRI",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "code",
    "Type": "varchar(100)",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "create_time",
    "Type": "datetime",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "customer_id",
    "Type": "varchar(32)",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "deleted",
    "Type": "tinyint(1)",
    "Null": "YES",
    "Key": "",
    "Default": "0",
    "Extra": ""
  },
  {
    "Field": "name",
    "Type": "varchar(50)",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "parent_id",
    "Type": "varchar(32)",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "sort",
    "Type": "int(11)",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "type",
    "Type": "varchar(20)",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "update_time",
    "Type": "datetime",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "url",
    "Type": "varchar(255)",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "path",
    "Type": "varchar(200)",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "component",
    "Type": "varchar(200)",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "icon",
    "Type": "varchar(50)",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  },
  {
    "Field": "visible",
    "Type": "tinyint(4)",
    "Null": "YES",
    "Key": "",
    "Default": "1",
    "Extra": ""
  },
  {
    "Field": "keep_alive",
    "Type": "tinyint(4)",
    "Null": "YES",
    "Key": "",
    "Default": "0",
    "Extra": ""
  },
  {
    "Field": "redirect",
    "Type": "varchar(200)",
    "Null": "YES",
    "Key": "",
    "Default": null,
    "Extra": ""
  }
]



-- 检查关键权限的数据
SELECT 
    id, code, name, type, path, component, icon, visible, parent_id, sort
FROM sys_permission 
WHERE code IN ('USER_MANAGE', 'ROLE_MANAGE', 'ROOM_MANAGE', 'BOOKING_MANAGE')
ORDER BY code;

[
  {
    "id": "perm031",
    "code": "BOOKING_MANAGE",
    "name": "预订管理",
    "type": "MENU",
    "path": "/room-booking",
    "component": "RoomBooking",
    "icon": "calendar",
    "visible": 1,
    "parent_id": "perm_room_center",
    "sort": 5
  },
  {
    "id": "perm009",
    "code": "ROLE_MANAGE",
    "name": "角色管理",
    "type": "MENU",
    "path": "/role-management",
    "component": "RoleManagement",
    "icon": "user-filled",
    "visible": 1,
    "parent_id": "perm_system",
    "sort": 2
  },
  {
    "id": "perm022",
    "code": "ROOM_MANAGE",
    "name": "教室管理",
    "type": "MENU",
    "path": "/house-management",
    "component": "HouseManagement",
    "icon": "office-building",
    "visible": 1,
    "parent_id": "perm_room_center",
    "sort": 4
  },
  {
    "id": "perm002",
    "code": "USER_MANAGE",
    "name": "用户管理",
    "type": "MENU",
    "path": "/user-management",
    "component": "UserManagement",
    "icon": "user",
    "visible": 1,
    "parent_id": "perm_user_center",
    "sort": 1
  }
]



-- 检查所有MENU类型的权限
SELECT 
    id, code, name, type, path, component, icon, visible, parent_id, sort
FROM sys_permission 
WHERE type = 'MENU' 
ORDER BY sort, code;

[
  {
    "id": "perm_dashboard",
    "code": "DASHBOARD",
    "name": "首页",
    "type": "MENU",
    "path": "/dashboard",
    "component": "Dashboard",
    "icon": "home-filled",
    "visible": 1,
    "parent_id": null,
    "sort": 1
  },
  {
    "id": "perm001",
    "code": "SYSTEM_MANAGE",
    "name": "系统管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 1
  },
  {
    "id": "perm002",
    "code": "USER_MANAGE",
    "name": "用户管理",
    "type": "MENU",
    "path": "/user-management",
    "component": "UserManagement",
    "icon": "user",
    "visible": 1,
    "parent_id": "perm_user_center",
    "sort": 1
  },
  {
    "id": "perm009",
    "code": "ROLE_MANAGE",
    "name": "角色管理",
    "type": "MENU",
    "path": "/role-management",
    "component": "RoleManagement",
    "icon": "user-filled",
    "visible": 1,
    "parent_id": "perm_system",
    "sort": 2
  },
  {
    "id": "perm015",
    "code": "PERMISSION_MANAGE",
    "name": "权限管理",
    "type": "MENU",
    "path": "/role-management",
    "component": "RoleManagement",
    "icon": "lock",
    "visible": 1,
    "parent_id": "perm_system",
    "sort": 3
  },
  {
    "id": "perm022",
    "code": "ROOM_MANAGE",
    "name": "教室管理",
    "type": "MENU",
    "path": "/house-management",
    "component": "HouseManagement",
    "icon": "office-building",
    "visible": 1,
    "parent_id": "perm_room_center",
    "sort": 4
  },
  {
    "id": "perm031",
    "code": "BOOKING_MANAGE",
    "name": "预订管理",
    "type": "MENU",
    "path": "/room-booking",
    "component": "RoomBooking",
    "icon": "calendar",
    "visible": 1,
    "parent_id": "perm_room_center",
    "sort": 5
  },
  {
    "id": "perm043",
    "code": "REPORT_VIEW",
    "name": "报表查看",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 7
  },
  {
    "id": "perm045",
    "code": "FILE_MANAGE",
    "name": "文件管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 8
  },
  {
    "id": "perm047",
    "code": "BLACKLIST_MANAGE",
    "name": "黑名单管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 9
  },
  {
    "id": "perm_user_center",
    "code": "USER_CENTER",
    "name": "用户中心",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": "user",
    "visible": 1,
    "parent_id": null,
    "sort": 10
  },
  {
    "id": "perm051",
    "code": "VIOLATION_MANAGE",
    "name": "违规管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 10
  },
  {
    "id": "perm052",
    "code": "SCHEME_MANAGE",
    "name": "方案管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 11
  },
  {
    "id": "perm057",
    "code": "RECYCLE_VIEW",
    "name": "回收站查看",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 12
  },
  {
    "id": "perm060",
    "code": "AREA_MANAGE",
    "name": "区域管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 13
  },
  {
    "id": "perm065",
    "code": "BOOKING_CONFIG",
    "name": "预订配置",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 14
  },
  {
    "id": "perm067",
    "code": "TITLE_MANAGE",
    "name": "职称管理",
    "type": "MENU",
    "path": "/level-management",
    "component": "LevelManagement",
    "icon": "medal",
    "visible": 1,
    "parent_id": "perm_user_center",
    "sort": 15
  },
  {
    "id": "perm072",
    "code": "POSITION_MANAGE",
    "name": "职位管理",
    "type": "MENU",
    "path": "/position-management",
    "component": "PositionManagement",
    "icon": "postcard",
    "visible": 1,
    "parent_id": "perm_user_center",
    "sort": 16
  },
  {
    "id": "perm077",
    "code": "ORG_MANAGE",
    "name": "组织机构管理",
    "type": "MENU",
    "path": "/organization-management",
    "component": "OrganizationManagement",
    "icon": "office-building",
    "visible": 1,
    "parent_id": "perm_user_center",
    "sort": 17
  },
  {
    "id": "perm082",
    "code": "BOOKING_PERSONNEL_MANAGE",
    "name": "预订人员权限管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 18
  },
  {
    "id": "perm087",
    "code": "CONTINUOUS_BOOKING_MANAGE",
    "name": "连续预订管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 19
  },
  {
    "id": "perm_room_center",
    "code": "ROOM_CENTER",
    "name": "教室中心",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": "office-building",
    "visible": 1,
    "parent_id": null,
    "sort": 20
  },
  {
    "id": "perm091",
    "code": "USER_BOOKING_LIMIT_MANAGE",
    "name": "用户预订限制管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 20
  },
  {
    "id": "perm095",
    "code": "BOOKING_TIME_RULE_MANAGE",
    "name": "预订时间规则管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 21
  },
  {
    "id": "perm100",
    "code": "RECYCLE_BIN_MANAGE",
    "name": "回收站管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 22
  },
  {
    "id": "perm105",
    "code": "ROOM_SCHEDULE_MANAGE",
    "name": "教室日程管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 23
  },
  {
    "id": "perm107",
    "code": "BOOKING_APPROVAL_MANAGE",
    "name": "预订审批管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": null,
    "visible": 1,
    "parent_id": null,
    "sort": 24
  },
  {
    "id": "perm_system",
    "code": "SYSTEM",
    "name": "系统管理",
    "type": "MENU",
    "path": null,
    "component": null,
    "icon": "setting",
    "visible": 1,
    "parent_id": null,
    "sort": 100
  },
  {
    "id": "perm_personal",
    "code": "PERSONAL_CENTER",
    "name": "个人中心",
    "type": "MENU",
    "path": "/personal-center",
    "component": "PersonalCenter",
    "icon": "user",
    "visible": 1,
    "parent_id": null,
    "sort": 200
  }
]

-- 检查路径字段是否存在数据
SELECT 
    COUNT(*) as total,
    COUNT(path) as has_path,
    COUNT(component) as has_component,
    COUNT(icon) as has_icon
FROM sys_permission 
WHERE type = 'MENU';



[
  {
    "total": 29,
    "has_path": 10,
    "has_component": 10,
    "has_icon": 13
  }
]