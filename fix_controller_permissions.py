#!/usr/bin/env python3
import os
import re

# 权限代码映射表
permission_mappings = {
    'AREA_ADD': 'building:area:create',
    'AREA_DELETE': 'building:area:delete',
    'AREA_EDIT': 'building:area:update',
    'AREA_MANAGE': 'building:area:manage',
    'AREA_VIEW': 'building:area:view',
    'BLACKLIST_ADD': 'system:blacklist:create',
    'BLACKLIST_DELETE': 'system:blacklist:delete',
    'BLACKLIST_MANAGE': 'system:blacklist:manage',
    'BLACKLIST_VIEW': 'system:blacklist:view',
    'BOOKING_APPROVAL_APPROVE': 'approval:approval:process',
    'BOOKING_APPROVAL_MANAGE': 'approval:approval:manage',
    'BOOKING_APPROVAL_VIEW': 'approval:approval:view',
    'BOOKING_APPROVE': 'booking:booking:approve',
    'BOOKING_CANCEL': 'booking:booking:cancel',
    'BOOKING_CREATE': 'booking:booking:create',
    'BOOKING_EDIT': 'booking:booking:update',
    'BOOKING_MANAGE': 'booking:booking:manage',
    'BOOKING_PERSONNEL_ADD': 'booking:personnel:create',
    'BOOKING_PERSONNEL_DELETE': 'booking:personnel:delete',
    'BOOKING_PERSONNEL_EDIT': 'booking:personnel:update',
    'BOOKING_PERSONNEL_MANAGE': 'booking:personnel:manage',
    'BOOKING_PERSONNEL_VIEW': 'booking:personnel:view',
    'BOOKING_TIME_RULE_ADD': 'booking:time-rule:create',
    'BOOKING_TIME_RULE_DELETE': 'booking:time-rule:delete',
    'BOOKING_TIME_RULE_EDIT': 'booking:time-rule:update',
    'BOOKING_TIME_RULE_VIEW': 'booking:time-rule:view',
    'BOOKING_VIEW': 'booking:booking:view',
    'CONTINUOUS_BOOKING_DELETE': 'booking:continuous:delete',
    'CONTINUOUS_BOOKING_EDIT': 'booking:continuous:update',
    'CONTINUOUS_BOOKING_VIEW': 'booking:continuous:view',
    'FILE_MANAGE': 'system:file:manage',
    'FILE_UPLOAD': 'system:file:upload',
    'ORG_ADD': 'system:org:create',
    'ORG_DELETE': 'system:org:delete',
    'ORG_EDIT': 'system:org:update',
    'ORG_MANAGE': 'system:org:manage',
    'ORG_VIEW': 'system:org:view',
    'POSITION_ADD': 'system:position:create',
    'POSITION_DELETE': 'system:position:delete',
    'POSITION_EDIT': 'system:position:update',
    'POSITION_MANAGE': 'system:position:manage',
    'POSITION_VIEW': 'system:position:view',
    'RECYCLE_BIN_DELETE': 'system:recycle-bin:delete',
    'RECYCLE_BIN_MANAGE': 'system:recycle-bin:manage',
    'RECYCLE_BIN_RESTORE': 'system:recycle-bin:restore',
    'RECYCLE_BIN_VIEW': 'system:recycle-bin:view',
    'ROLE_ADD': 'system:role:create',
    'ROLE_DELETE': 'system:role:delete',
    'ROLE_EDIT': 'system:role:update',
    'ROLE_MANAGE': 'system:role:manage',
    'ROLE_VIEW': 'system:role:view',
    'ROOM_ADD': 'building:room:create',
    'ROOM_BOOK': 'building:room:book',
    'ROOM_CREATE': 'building:room:create',
    'ROOM_DELETE': 'building:room:delete',
    'ROOM_EDIT': 'building:room:update',
    'ROOM_MANAGE': 'building:room:manage',
    'ROOM_SEARCH': 'building:room:search',
    'ROOM_UPDATE': 'building:room:update',
    'ROOM_VIEW': 'building:room:view',
    'SCHEME_ADD': 'scheme:scheme:create',
    'SCHEME_DELETE': 'scheme:scheme:delete',
    'SCHEME_EDIT': 'scheme:scheme:update',
    'SCHEME_MANAGE': 'scheme:scheme:manage',
    'SCHEME_VIEW': 'scheme:scheme:view',
    'SYSTEM_CONFIG': 'system:config:manage',
    'TITLE_ADD': 'system:title:create',
    'TITLE_DELETE': 'system:title:delete',
    'TITLE_EDIT': 'system:title:update',
    'TITLE_MANAGE': 'system:title:manage',
    'TITLE_VIEW': 'system:title:view',
    'USER_BOOKING_LIMIT_DELETE': 'booking:user-limit:delete',
    'USER_BOOKING_LIMIT_EDIT': 'booking:user-limit:update',
    'USER_BOOKING_LIMIT_MANAGE': 'booking:user-limit:manage',
    'USER_BOOKING_LIMIT_VIEW': 'booking:user-limit:view',
    'USER_DELETE': 'system:user:delete',
    'USER_EDIT': 'system:user:update',
    'USER_MANAGE': 'system:user:manage',
    'USER_PERMISSION_VIEW': 'system:user:permission:view',
    'USER_RESET_PWD': 'system:user:reset-password',
    'USER_VIEW': 'system:user:view',
    'VIOLATION_MANAGE': 'system:violation:manage'
}

def update_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_content = content
    updated_count = 0
    
    for old_code, new_code in permission_mappings.items():
        old_pattern = f"hasAuthority('{old_code}')"
        new_pattern = f"hasAuthority('{new_code}')"
        
        if old_pattern in content:
            content = content.replace(old_pattern, new_pattern)
            updated_count += content.count(new_pattern) - original_content.count(new_pattern)
            print(f"  {old_code} -> {new_code}")
    
    if content != original_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        return updated_count
    return 0

def main():
    controller_dir = r"D:\code\Beijing-Language-and-Culture-University3\backend\src\main\java\com\proshine\system\controller"
    
    total_updated = 0
    updated_files = []
    
    for filename in os.listdir(controller_dir):
        if filename.endswith('.java'):
            file_path = os.path.join(controller_dir, filename)
            count = update_file(file_path)
            if count > 0:
                print(f"{filename}: 更新了 {count} 个权限")
                updated_files.append(filename)
                total_updated += count
    
    print(f"\n总结: 更新了 {len(updated_files)} 个文件，共 {total_updated} 个权限代码")
    
if __name__ == "__main__":
    main()