#!/usr/bin/env python3
import os
import re

# 权限代码映射表：旧权限码 -> 新权限码
permission_mappings = {
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
    'ROOM_APPROVE': 'booking:booking:approve',
    'ROOM_CANCEL_BOOK': 'booking:booking:cancel',
    'ROOM_SCHEDULE_MANAGE': 'building:room:schedule:manage',
    
    # 区域管理
    'AREA_MANAGE': 'building:area:manage',
    'AREA_VIEW': 'building:area:view',
    'AREA_ADD': 'building:area:create',
    'AREA_CREATE': 'building:area:create',
    'AREA_EDIT': 'building:area:update',
    'AREA_UPDATE': 'building:area:update',
    'AREA_DELETE': 'building:area:delete',
    
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
    'SCHEME_ADD': 'scheme:scheme:create',
    'SCHEME_EDIT': 'scheme:scheme:update',
    
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
    'USER_BOOKING_LIMIT_EDIT': 'booking:user-limit:update',
    
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
    'RECYCLE_DELETE': 'system:recycle-bin:delete',
    'RECYCLE_RESTORE': 'system:recycle-bin:restore',
    'RECYCLE_VIEW': 'system:recycle-bin:view',
    
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
    
    # 其他
    'DASHBOARD': 'system:dashboard:view',
    'PERSONAL_CENTER': 'system:user:center',
    'USER_CENTER': 'system:user:center',
    'REPORT_EXPORT': 'booking:report:export',
    'REPORT_VIEW': 'booking:report:view',
    'SYSTEM': 'system:system:manage',
}

def update_permissions_in_file(file_path):
    """
    Update permission codes in a single file.
    """
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_content = content
    changes_made = []
    
    # Pattern to match @PreAuthorize annotations with hasAuthority
    pattern = r'@PreAuthorize\s*\(\s*"([^"]+)"\s*\)'
    
    def replace_permissions(match):
        annotation_content = match.group(1)
        original_annotation = annotation_content
        
        # Replace permission codes in the annotation
        for old_code, new_code in permission_mappings.items():
            old_pattern = f"hasAuthority\('{old_code}'\)"
            new_pattern = f"hasAuthority('{new_code}')"
            if old_pattern in annotation_content:
                annotation_content = annotation_content.replace(old_pattern, new_pattern)
                changes_made.append(f"{old_code} -> {new_code}")
        
        return f'@PreAuthorize("{annotation_content}")'
    
    # Replace all @PreAuthorize annotations
    content = re.sub(pattern, replace_permissions, content)
    
    # If content changed, write it back
    if content != original_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        return changes_made
    return []

def main():
    controller_dir = r"D:\code\Beijing-Language-and-Culture-University3\backend\src\main\java\com\proshine\system\controller"
    
    updated_files = []
    total_changes = 0
    
    # Process all Java files in the controller directory
    for filename in os.listdir(controller_dir):
        if filename.endswith('.java'):
            file_path = os.path.join(controller_dir, filename)
            changes = update_permissions_in_file(file_path)
            if changes:
                updated_files.append(filename)
                total_changes += len(changes)
                print(f"Updated {filename}:")
                for change in changes:
                    print(f"  - {change}")
                print()
    
    print(f"\n总结:")
    print(f"更新的文件数: {len(updated_files)}")
    print(f"总权限更改数: {total_changes}")
    
    if updated_files:
        print("\n更新的文件列表:")
        for f in updated_files:
            print(f"  - {f}")

if __name__ == "__main__":
    main()