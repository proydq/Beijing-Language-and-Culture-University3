# 前端API接口权限分析

## 已分析的API模块

### 1. 认证模块 (AuthenticationController)
| 接口 | 方法 | 权限要求 |
|------|------|----------|
| /authentication/login | POST | 无需权限 |
| /authentication/logout | POST | 需要登录 |
| /authentication/token/refresh | POST | 需要登录 |
| /authentication/forgot-password | POST | 无需权限 |

### 2. 用户管理模块 (SysUserController) ✅已添加权限
| 接口 | 方法 | 权限要求 |
|------|------|----------|
| /system/user/search | POST | USER_SEARCH or USER_MANAGE |
| /system/user/findById | GET | USER_VIEW or USER_MANAGE |
| /system/user/save | POST | USER_ADD or USER_MANAGE |
| /system/user/update | POST | USER_EDIT or USER_MANAGE |
| /system/user/delete | POST | USER_DELETE or USER_MANAGE |
| /system/user/moveToRecycleBin | POST | USER_DELETE or USER_MANAGE |
| /system/user/changeStatus | GET | USER_EDIT or USER_MANAGE |
| /system/user/resetPassword | POST | USER_RESET_PWD or USER_MANAGE |
| /system/user/assignRoles | POST | USER_ASSIGN_ROLE or USER_MANAGE |
| /system/user/checkUsername | GET | 需要登录 |
| /system/user/checkJobNumber | GET | 需要登录 |

### 3. 教室管理模块 (RoomController) ✅已添加权限
| 接口 | 方法 | 权限要求 |
|------|------|----------|
| /api/room/search | POST | ROOM_SEARCH or ROOM_MANAGE |
| /api/room/{id} | GET | ROOM_VIEW or ROOM_MANAGE |
| /api/room | POST | ROOM_ADD or ROOM_MANAGE |
| /api/room/{id} | PUT | ROOM_EDIT or ROOM_MANAGE |
| /api/room/{id} | DELETE | ROOM_DELETE or ROOM_MANAGE |
| /api/room/batch | DELETE | ROOM_DELETE or ROOM_MANAGE |
| /api/room/types | GET | ROOM_SEARCH or ROOM_MANAGE |
| /api/room/check-uniqueness | GET | ROOM_ADD or ROOM_EDIT or ROOM_MANAGE |
| /api/room/import | POST | ROOM_ADD or ROOM_MANAGE |
| /api/room/export | POST | ROOM_SEARCH or ROOM_MANAGE |
| /api/room/available | GET | ROOM_BOOK or BOOKING_CREATE or ROOM_MANAGE |

### 4. 需要添加权限的控制器

#### OrganizationController (组织机构管理)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /api/organization/all | GET | ORG_VIEW or ORG_MANAGE |
| /api/organization/tree | GET | ORG_VIEW or ORG_MANAGE |
| /api/organization/{id} | GET | ORG_VIEW or ORG_MANAGE |
| /api/organization | POST | ORG_ADD or ORG_MANAGE |
| /api/organization/{id} | PUT | ORG_EDIT or ORG_MANAGE |
| /api/organization/{id} | DELETE | ORG_DELETE or ORG_MANAGE |

#### PositionController (职位管理)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /api/position/all | GET | POSITION_VIEW or POSITION_MANAGE |
| /api/position/search | POST | POSITION_VIEW or POSITION_MANAGE |
| /api/position/{id} | GET | POSITION_VIEW or POSITION_MANAGE |
| /api/position | POST | POSITION_ADD or POSITION_MANAGE |
| /api/position/{id} | PUT | POSITION_EDIT or POSITION_MANAGE |
| /api/position/{id} | DELETE | POSITION_DELETE or POSITION_MANAGE |

#### TitleController (职称管理)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /api/title/all | GET | TITLE_VIEW or TITLE_MANAGE |
| /api/title/search | POST | TITLE_VIEW or TITLE_MANAGE |
| /api/title/{id} | GET | TITLE_VIEW or TITLE_MANAGE |
| /api/title | POST | TITLE_ADD or TITLE_MANAGE |
| /api/title/{id} | PUT | TITLE_EDIT or TITLE_MANAGE |
| /api/title/{id} | DELETE | TITLE_DELETE or TITLE_MANAGE |

#### BlacklistController (黑名单管理)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /blacklist/search | POST | BLACKLIST_VIEW or BLACKLIST_MANAGE |
| /blacklist/{id} | GET | BLACKLIST_VIEW or BLACKLIST_MANAGE |
| /blacklist | POST | BLACKLIST_ADD or BLACKLIST_MANAGE |
| /blacklist/{id} | DELETE | BLACKLIST_DELETE or BLACKLIST_MANAGE |
| /blacklist/batch | DELETE | BLACKLIST_DELETE or BLACKLIST_MANAGE |
| /blacklist/check-status | GET | 需要登录 |

#### ViolationSettingController (违规设置)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /violation-settings/search | POST | VIOLATION_MANAGE or SYSTEM_CONFIG |
| /violation-settings/{id} | GET | VIOLATION_MANAGE or SYSTEM_CONFIG |
| /violation-settings/{id} | PUT | VIOLATION_MANAGE or SYSTEM_CONFIG |
| /violation-settings/batch | PUT | VIOLATION_MANAGE or SYSTEM_CONFIG |

#### AreaController (区域管理)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /area/search | POST | AREA_VIEW or AREA_MANAGE |
| /area/tree | GET | AREA_VIEW or AREA_MANAGE |
| /area/{id} | GET | AREA_VIEW or AREA_MANAGE |
| /area | POST | AREA_ADD or AREA_MANAGE |
| /area/{id} | PUT | AREA_EDIT or AREA_MANAGE |
| /area/{id} | DELETE | AREA_DELETE or AREA_MANAGE |

#### BookingPersonnelPermissionController (预订人员权限)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /booking-personnel-permission/search | POST | BOOKING_PERMISSION_MANAGE |
| /booking-personnel-permission/{id} | GET | BOOKING_PERMISSION_MANAGE |
| /booking-personnel-permission | POST | BOOKING_PERMISSION_MANAGE |
| /booking-personnel-permission/{id} | PUT | BOOKING_PERMISSION_MANAGE |
| /booking-personnel-permission/{id} | DELETE | BOOKING_PERMISSION_MANAGE |
| /booking-personnel-permission/available-users | GET | BOOKING_PERMISSION_MANAGE |
| /booking-personnel-permission/available-rooms | GET | BOOKING_PERMISSION_MANAGE |

#### ContinuousBookingSettingController (连续预订设置)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /continuous-booking-settings/search | POST | BOOKING_CONFIG or SYSTEM_CONFIG |
| /continuous-booking-settings/{id} | GET | BOOKING_CONFIG or SYSTEM_CONFIG |
| /continuous-booking-settings | POST | BOOKING_CONFIG or SYSTEM_CONFIG |
| /continuous-booking-settings/{id} | PUT | BOOKING_CONFIG or SYSTEM_CONFIG |
| /continuous-booking-settings/{id} | DELETE | BOOKING_CONFIG or SYSTEM_CONFIG |
| /continuous-booking-settings/batch/update | PUT | BOOKING_CONFIG or SYSTEM_CONFIG |

#### UserBookingLimitController (用户预订限制)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /user-booking-limits/all | GET | BOOKING_CONFIG or SYSTEM_CONFIG |
| /user-booking-limits/{id} | GET | BOOKING_CONFIG or SYSTEM_CONFIG |
| /user-booking-limits | POST | BOOKING_CONFIG or SYSTEM_CONFIG |
| /user-booking-limits/{id} | PUT | BOOKING_CONFIG or SYSTEM_CONFIG |
| /user-booking-limits/{id} | DELETE | BOOKING_CONFIG or SYSTEM_CONFIG |

#### BookingTimeRuleController (预订时间规则)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /booking-time-rules/search | POST | BOOKING_CONFIG or SYSTEM_CONFIG |
| /booking-time-rules/{id} | GET | BOOKING_CONFIG or SYSTEM_CONFIG |
| /booking-time-rules | POST | BOOKING_CONFIG or SYSTEM_CONFIG |
| /booking-time-rules/{id} | PUT | BOOKING_CONFIG or SYSTEM_CONFIG |
| /booking-time-rules/{id} | DELETE | BOOKING_CONFIG or SYSTEM_CONFIG |

#### BookingTimeValidationController (预订时间验证)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /booking-validation/validate | POST | 需要登录 |

#### RecycleBinController (回收站)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /recycle-bin/users | GET | RECYCLE_VIEW |
| /recycle-bin/users/restore | POST | RECYCLE_RESTORE |
| /recycle-bin/users/delete | DELETE | RECYCLE_DELETE |

#### SchemeManagementController (方案管理)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /scheme-management/search | POST | SCHEME_VIEW or SCHEME_MANAGE |
| /scheme-management/{id} | GET | SCHEME_VIEW or SCHEME_MANAGE |
| /scheme-management | POST | SCHEME_ADD or SCHEME_MANAGE |
| /scheme-management/{id} | PUT | SCHEME_EDIT or SCHEME_MANAGE |
| /scheme-management/{id} | DELETE | SCHEME_DELETE or SCHEME_MANAGE |
| /scheme-management/{id}/activate | POST | SCHEME_EDIT or SCHEME_MANAGE |

#### FileUploadController (文件上传)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /api/upload | POST | FILE_UPLOAD |
| /api/upload/multiple | POST | FILE_UPLOAD |

#### FileUploadEnhancedController (增强文件上传)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /api/upload/enhanced | POST | FILE_UPLOAD or FILE_MANAGE |
| /api/upload/enhanced/check | POST | FILE_UPLOAD or FILE_MANAGE |
| /api/upload/enhanced/{fileId} | GET | FILE_UPLOAD or FILE_MANAGE |
| /api/upload/enhanced/{fileId} | DELETE | FILE_MANAGE |

#### RoomScheduleController (教室日程)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /api/room-schedule/weekly | POST | ROOM_VIEW or BOOKING_VIEW |
| /api/room-schedule/available-rooms | POST | ROOM_BOOK or BOOKING_CREATE |

#### RoomBookingApprovalController (预订审批)
| 接口 | 方法 | 建议权限 |
|------|------|----------|
| /api/room-booking-approval/pending | POST | BOOKING_APPROVE or BOOKING_MANAGE |
| /api/room-booking-approval/history | POST | BOOKING_VIEW or BOOKING_MANAGE |
| /api/room-booking-approval/approve | POST | BOOKING_APPROVE or BOOKING_MANAGE |
| /api/room-booking-approval/reject | POST | BOOKING_REJECT or BOOKING_MANAGE |
| /api/room-booking-approval/batch-approve | POST | BOOKING_APPROVE or BOOKING_MANAGE |
| /api/room-booking-approval/batch-reject | POST | BOOKING_REJECT or BOOKING_MANAGE |