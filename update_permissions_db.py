#!/usr/bin/env python3
import pymysql
import sys

# 数据库连接配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 12100,
    'user': 'bunny',
    'password': 'bunny',
    'database': 'testdb',
    'charset': 'utf8mb4'
}

# 权限映射表
PERMISSION_MAPPINGS = {
    # 系统管理
    'SYSTEM_MANAGE': 'system:system:manage',
    
    # 用户管理
    'USER_MANAGE': 'system:user:manage',
    'USER_SEARCH': 'system:user:view',
    'USER_VIEW': 'system:user:view',
    'USER_ADD': 'system:user:create',
    'USER_CREATE': 'system:user:create',
    'USER_EDIT': 'system:user:update',
    'USER_UPDATE': 'system:user:update',
    'USER_DELETE': 'system:user:delete',
    'USER_RESET_PWD': 'system:user:reset-password',
    'USER_RESET_PASSWORD': 'system:user:reset-password',
    'USER_ASSIGN_ROLE': 'system:user:assign-roles',
    'USER_EXPORT': 'system:user:export',
    'USER_IMPORT': 'system:user:import',
    
    # 角色管理
    'ROLE_MANAGE': 'system:role:manage',
    'ROLE_SEARCH': 'system:role:view',
    'ROLE_VIEW': 'system:role:view',
    'ROLE_ADD': 'system:role:create',
    'ROLE_CREATE': 'system:role:create',
    'ROLE_EDIT': 'system:role:update',
    'ROLE_UPDATE': 'system:role:update',
    'ROLE_DELETE': 'system:role:delete',
    'ROLE_ASSIGN_PERM': 'system:role:assign-permission',
    'ROLE_ASSIGN_PERMISSION': 'system:role:assign-permission',
    
    # 权限管理
    'PERMISSION_MANAGE': 'system:permission:manage',
    'PERMISSION_SEARCH': 'system:permission:view',
    'PERMISSION_VIEW': 'system:permission:view',
    'PERMISSION_ADD': 'system:permission:create',
    'PERMISSION_CREATE': 'system:permission:create',
    'PERMISSION_EDIT': 'system:permission:update',
    'PERMISSION_UPDATE': 'system:permission:update',
    'PERMISSION_DELETE': 'system:permission:delete',
    
    # 组织管理
    'ORG_MANAGE': 'system:org:manage',
    'ORG_VIEW': 'system:org:view',
    'ORG_ADD': 'system:org:create',
    'ORG_EDIT': 'system:org:update',
    'ORG_DELETE': 'system:org:delete',
    
    # 职位管理
    'POSITION_MANAGE': 'system:position:manage',
    'POSITION_VIEW': 'system:position:view',
    'POSITION_ADD': 'system:position:create',
    'POSITION_EDIT': 'system:position:update',
    'POSITION_DELETE': 'system:position:delete',
    
    # 职称管理
    'TITLE_MANAGE': 'system:title:manage',
    'TITLE_VIEW': 'system:title:view',
    'TITLE_ADD': 'system:title:create',
    'TITLE_EDIT': 'system:title:update',
    'TITLE_DELETE': 'system:title:delete',
    
    # 房间管理
    'ROOM_MANAGE': 'building:room:manage',
    'ROOM_SEARCH': 'building:room:search',
    'ROOM_VIEW': 'building:room:view',
    'ROOM_ADD': 'building:room:create',
    'ROOM_CREATE': 'building:room:create',
    'ROOM_EDIT': 'building:room:update',
    'ROOM_UPDATE': 'building:room:update',
    'ROOM_DELETE': 'building:room:delete',
    'ROOM_BOOK': 'building:room:book',
    'ROOM_SCHEDULE_VIEW': 'building:room:schedule-view',
    'ROOM_CENTER': 'building:room:center',
    
    # 区域管理 - 已手动更新，跳过
    # 'AREA_MANAGE': 'building:area:manage',
    # 'AREA_VIEW': 'building:area:view',
    # 'AREA_ADD': 'building:area:create',
    # 'AREA_CREATE': 'building:area:create',
    # 'AREA_EDIT': 'building:area:update',
    # 'AREA_UPDATE': 'building:area:update',
    # 'AREA_DELETE': 'building:area:delete',
    
    # 预订管理
    'BOOKING_MANAGE': 'booking:booking:manage',
    'BOOKING_SEARCH': 'booking:booking:search',
    'BOOKING_VIEW': 'booking:booking:view',
    'BOOKING_ADD': 'booking:booking:create',
    'BOOKING_CREATE': 'booking:booking:create',
    'BOOKING_EDIT': 'booking:booking:update',
    'BOOKING_UPDATE': 'booking:booking:update',
    'BOOKING_CANCEL': 'booking:booking:cancel',
    'BOOKING_APPROVE': 'booking:booking:approve',
    'BOOKING_REJECT': 'booking:booking:reject',
    'BOOKING_RELEASE': 'booking:booking:release',
    'BOOKING_CONFIG': 'booking:config:manage',
    'BOOKING_RECORDS_VIEW': 'booking:records:view',
    
    # 预订审批
    'BOOKING_APPROVAL_VIEW': 'approval:approval:view',
    'BOOKING_APPROVAL_MANAGE': 'approval:approval:manage',
    'BOOKING_APPROVAL_APPROVE': 'approval:approval:process',
    'APPROVAL_MANAGE': 'approval:approval:manage',
    'APPROVAL_SEARCH': 'approval:approval:search',
    'APPROVAL_VIEW': 'approval:approval:view',
    'APPROVAL_PROCESS': 'approval:approval:process',
    'APPROVAL_CONFIG_MANAGE': 'approval:config:manage',
    
    # 方案管理
    'SCHEME_MANAGE': 'scheme:scheme:manage',
    'SCHEME_VIEW': 'scheme:scheme:view',
    'SCHEME_CREATE': 'scheme:scheme:create',
    'SCHEME_UPDATE': 'scheme:scheme:update',
    'SCHEME_DELETE': 'scheme:scheme:delete',
    'SCHEME_ACTIVATE': 'scheme:scheme:activate',
    
    # 黑名单管理
    'BLACKLIST_MANAGE': 'system:blacklist:manage',
    'BLACKLIST_VIEW': 'system:blacklist:view',
    'BLACKLIST_ADD': 'system:blacklist:create',
    'BLACKLIST_CREATE': 'system:blacklist:create',
    'BLACKLIST_UPDATE': 'system:blacklist:update',
    'BLACKLIST_DELETE': 'system:blacklist:delete',
    
    # 违约设置
    'VIOLATION_MANAGE': 'system:violation:manage',
    'VIOLATION_VIEW': 'system:violation:view',
    'VIOLATION_UPDATE': 'system:violation:update',
    
    # 预订规则
    'BOOKING_RULE_MANAGE': 'booking:rule:manage',
    'BOOKING_RULE_VIEW': 'booking:rule:view',
    'BOOKING_RULE_UPDATE': 'booking:rule:update',
    
    # 时间规则
    'BOOKING_TIME_RULE_VIEW': 'booking:time-rule:view',
    'BOOKING_TIME_RULE_ADD': 'booking:time-rule:create',
    'BOOKING_TIME_RULE_EDIT': 'booking:time-rule:update',
    'BOOKING_TIME_RULE_DELETE': 'booking:time-rule:delete',
    'BOOKING_TIME_RULE_MANAGE': 'booking:time-rule:manage',
    'TIME_RULE_MANAGE': 'booking:time-rule:manage',
    'TIME_RULE_VIEW': 'booking:time-rule:view',
    'TIME_RULE_CREATE': 'booking:time-rule:create',
    'TIME_RULE_UPDATE': 'booking:time-rule:update',
    'TIME_RULE_DELETE': 'booking:time-rule:delete',
    
    # 用户预订限制
    'USER_BOOKING_LIMIT_MANAGE': 'booking:user-limit:manage',
    'USER_BOOKING_LIMIT_VIEW': 'booking:user-limit:view',
    'USER_BOOKING_LIMIT_CREATE': 'booking:user-limit:create',
    'USER_BOOKING_LIMIT_UPDATE': 'booking:user-limit:update',
    'USER_BOOKING_LIMIT_DELETE': 'booking:user-limit:delete',
    
    # 预订人员权限
    'BOOKING_PERSONNEL_VIEW': 'booking:personnel:view',
    'BOOKING_PERSONNEL_ADD': 'booking:personnel:create',
    'BOOKING_PERSONNEL_EDIT': 'booking:personnel:update',
    'BOOKING_PERSONNEL_DELETE': 'booking:personnel:delete',
    'BOOKING_PERSONNEL_MANAGE': 'booking:personnel:manage',
    'BOOKING_PERSONNEL_CREATE': 'booking:personnel:create',
    'BOOKING_PERSONNEL_UPDATE': 'booking:personnel:update',
    'BOOKING_PERMISSION_MANAGE': 'booking:personnel:manage',
    
    # 回收站
    'RECYCLE_BIN_MANAGE': 'system:recycle-bin:manage',
    'RECYCLE_BIN_VIEW': 'system:recycle-bin:view',
    'RECYCLE_BIN_RESTORE': 'system:recycle-bin:restore',
    'RECYCLE_BIN_DELETE': 'system:recycle-bin:delete',
    
    # 连续预订设置
    'CONTINUOUS_BOOKING_MANAGE': 'booking:continuous:manage',
    'CONTINUOUS_BOOKING_VIEW': 'booking:continuous:view',
    'CONTINUOUS_BOOKING_EDIT': 'booking:continuous:update',
    'CONTINUOUS_BOOKING_UPDATE': 'booking:continuous:update',
    'CONTINUOUS_BOOKING_DELETE': 'booking:continuous:delete',
    
    # 文件管理
    'FILE_MANAGE': 'system:file:manage',
    'FILE_UPLOAD': 'system:file:upload',
    'FILE_VIEW': 'system:file:view',
    'FILE_DELETE': 'system:file:delete',
}

def update_permissions():
    conn = None
    cursor = None
    try:
        # 连接数据库
        conn = pymysql.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        # 获取当前权限状态
        cursor.execute("SELECT COUNT(*) FROM sys_permission WHERE code NOT LIKE '%:%' AND deleted = 0")
        old_count = cursor.fetchone()[0]
        print(f"需要更新的权限数量: {old_count}")
        
        # 更新权限
        updated_count = 0
        for old_code, new_code in PERMISSION_MAPPINGS.items():
            cursor.execute(
                "UPDATE sys_permission SET code = %s WHERE code = %s AND deleted = 0",
                (new_code, old_code)
            )
            if cursor.rowcount > 0:
                updated_count += cursor.rowcount
                print(f"更新: {old_code} -> {new_code}")
        
        # 提交事务
        conn.commit()
        
        # 验证结果
        cursor.execute("SELECT COUNT(*) FROM sys_permission WHERE code LIKE '%:%' AND deleted = 0")
        new_count = cursor.fetchone()[0]
        
        cursor.execute("SELECT COUNT(*) FROM sys_permission WHERE code NOT LIKE '%:%' AND deleted = 0")
        remaining_old = cursor.fetchone()[0]
        
        print(f"\n更新完成！")
        print(f"更新的权限数量: {updated_count}")
        print(f"新格式权限数量: {new_count}")
        print(f"剩余旧格式权限: {remaining_old}")
        
        if remaining_old > 0:
            print("\n剩余未更新的权限:")
            cursor.execute("SELECT code, name FROM sys_permission WHERE code NOT LIKE '%:%' AND deleted = 0 ORDER BY code")
            for row in cursor.fetchall():
                print(f"  {row[0]}: {row[1]}")
        
    except Exception as e:
        print(f"错误: {e}")
        if conn:
            conn.rollback()
        sys.exit(1)
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

if __name__ == "__main__":
    update_permissions()