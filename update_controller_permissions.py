#!/usr/bin/env python3
import os
import re

# Permission code mappings from old to new format
permission_mappings = {
    # User management
    'USER_MANAGE': 'system:user:manage',
    'USER_VIEW': 'system:user:view',
    'USER_CREATE': 'system:user:create',
    'USER_UPDATE': 'system:user:update',
    'USER_DELETE': 'system:user:delete',
    'USER_EXPORT': 'system:user:export',
    'USER_IMPORT': 'system:user:import',
    'USER_RESET_PASSWORD': 'system:user:reset-password',
    
    # Role management
    'ROLE_MANAGE': 'system:role:manage',
    'ROLE_VIEW': 'system:role:view',
    'ROLE_CREATE': 'system:role:create',
    'ROLE_UPDATE': 'system:role:update',
    'ROLE_DELETE': 'system:role:delete',
    'ROLE_ASSIGN_PERMISSION': 'system:role:assign-permission',
    
    # Permission management
    'PERMISSION_MANAGE': 'system:permission:manage',
    'PERMISSION_VIEW': 'system:permission:view',
    'PERMISSION_CREATE': 'system:permission:create',
    'PERMISSION_UPDATE': 'system:permission:update',
    'PERMISSION_DELETE': 'system:permission:delete',
    
    # Room management
    'ROOM_MANAGE': 'building:room:manage',
    'ROOM_SEARCH': 'building:room:search',
    'ROOM_VIEW': 'building:room:view',
    'ROOM_CREATE': 'building:room:create',
    'ROOM_UPDATE': 'building:room:update',
    'ROOM_DELETE': 'building:room:delete',
    'ROOM_BOOK': 'building:room:book',
    'ROOM_SCHEDULE_VIEW': 'building:room:schedule-view',
    
    # Booking management
    'BOOKING_MANAGE': 'booking:booking:manage',
    'BOOKING_SEARCH': 'booking:booking:search',
    'BOOKING_VIEW': 'booking:booking:view',
    'BOOKING_CREATE': 'booking:booking:create',
    'BOOKING_UPDATE': 'booking:booking:update',
    'BOOKING_CANCEL': 'booking:booking:cancel',
    'BOOKING_APPROVE': 'booking:booking:approve',
    'BOOKING_REJECT': 'booking:booking:reject',
    'BOOKING_RELEASE': 'booking:booking:release',
    
    # Approval management
    'APPROVAL_MANAGE': 'approval:approval:manage',
    'APPROVAL_SEARCH': 'approval:approval:search',
    'APPROVAL_VIEW': 'approval:approval:view',
    'APPROVAL_PROCESS': 'approval:approval:process',
    'APPROVAL_CONFIG_MANAGE': 'approval:config:manage',
    'BOOKING_APPROVAL_VIEW': 'approval:approval:view',
    'BOOKING_APPROVAL_MANAGE': 'approval:approval:manage',
    'BOOKING_APPROVAL_APPROVE': 'approval:approval:process',
    
    # Area management
    'AREA_MANAGE': 'building:area:manage',
    'AREA_VIEW': 'building:area:view',
    'AREA_CREATE': 'building:area:create',
    'AREA_UPDATE': 'building:area:update',
    'AREA_DELETE': 'building:area:delete',
    
    # Organization management
    'ORG_MANAGE': 'system:org:manage',
    'ORG_VIEW': 'system:org:view',
    'ORG_ADD': 'system:org:create',
    'ORG_EDIT': 'system:org:update',
    'ORG_DELETE': 'system:org:delete',
    
    # Title management
    'TITLE_MANAGE': 'system:title:manage',
    'TITLE_VIEW': 'system:title:view',
    'TITLE_ADD': 'system:title:create',
    'TITLE_EDIT': 'system:title:update',
    'TITLE_DELETE': 'system:title:delete',
    
    # Scheme management
    'SCHEME_MANAGE': 'scheme:scheme:manage',
    'SCHEME_VIEW': 'scheme:scheme:view',
    'SCHEME_CREATE': 'scheme:scheme:create',
    'SCHEME_UPDATE': 'scheme:scheme:update',
    'SCHEME_DELETE': 'scheme:scheme:delete',
    'SCHEME_ACTIVATE': 'scheme:scheme:activate',
    
    # Blacklist management
    'BLACKLIST_MANAGE': 'system:blacklist:manage',
    'BLACKLIST_VIEW': 'system:blacklist:view',
    'BLACKLIST_CREATE': 'system:blacklist:create',
    'BLACKLIST_UPDATE': 'system:blacklist:update',
    'BLACKLIST_DELETE': 'system:blacklist:delete',
    
    # Violation setting
    'VIOLATION_MANAGE': 'system:violation:manage',
    'VIOLATION_VIEW': 'system:violation:view',
    'VIOLATION_UPDATE': 'system:violation:update',
    
    # Booking rules
    'BOOKING_RULE_MANAGE': 'booking:rule:manage',
    'BOOKING_RULE_VIEW': 'booking:rule:view',
    'BOOKING_RULE_UPDATE': 'booking:rule:update',
    
    # Time rules
    'BOOKING_TIME_RULE_VIEW': 'booking:time-rule:view',
    'BOOKING_TIME_RULE_ADD': 'booking:time-rule:create',
    'BOOKING_TIME_RULE_EDIT': 'booking:time-rule:update',
    'BOOKING_TIME_RULE_DELETE': 'booking:time-rule:delete',
    'TIME_RULE_MANAGE': 'booking:time-rule:manage',
    'TIME_RULE_VIEW': 'booking:time-rule:view',
    'TIME_RULE_CREATE': 'booking:time-rule:create',
    'TIME_RULE_UPDATE': 'booking:time-rule:update',
    'TIME_RULE_DELETE': 'booking:time-rule:delete',
    
    # User booking limit
    'USER_BOOKING_LIMIT_MANAGE': 'booking:user-limit:manage',
    'USER_BOOKING_LIMIT_VIEW': 'booking:user-limit:view',
    'USER_BOOKING_LIMIT_CREATE': 'booking:user-limit:create',
    'USER_BOOKING_LIMIT_UPDATE': 'booking:user-limit:update',
    'USER_BOOKING_LIMIT_DELETE': 'booking:user-limit:delete',
    
    # Booking personnel permission
    'BOOKING_PERSONNEL_MANAGE': 'booking:personnel:manage',
    'BOOKING_PERSONNEL_VIEW': 'booking:personnel:view',
    'BOOKING_PERSONNEL_ADD': 'booking:personnel:create',
    'BOOKING_PERSONNEL_EDIT': 'booking:personnel:update',
    'BOOKING_PERSONNEL_DELETE': 'booking:personnel:delete',
    'BOOKING_PERSONNEL_CREATE': 'booking:personnel:create',
    'BOOKING_PERSONNEL_UPDATE': 'booking:personnel:update',
    
    # Recycle bin
    'RECYCLE_BIN_MANAGE': 'system:recycle-bin:manage',
    'RECYCLE_BIN_VIEW': 'system:recycle-bin:view',
    'RECYCLE_BIN_RESTORE': 'system:recycle-bin:restore',
    'RECYCLE_BIN_DELETE': 'system:recycle-bin:delete',
    
    # Continuous booking setting
    'CONTINUOUS_BOOKING_MANAGE': 'booking:continuous:manage',
    'CONTINUOUS_BOOKING_VIEW': 'booking:continuous:view',
    'CONTINUOUS_BOOKING_EDIT': 'booking:continuous:update',
    'CONTINUOUS_BOOKING_UPDATE': 'booking:continuous:update',
    'CONTINUOUS_BOOKING_DELETE': 'booking:continuous:delete',
    
    # File management
    'FILE_MANAGE': 'system:file:manage',
    'FILE_UPLOAD': 'system:file:upload',
    'FILE_VIEW': 'system:file:view',
}

def update_permissions_in_file(file_path):
    """
    Update permission codes in a single file.
    Remove old permission codes that are paired with new ones using 'or'.
    """
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_content = content
    
    # Pattern to match @PreAuthorize annotations with hasAuthority
    pattern = r'@PreAuthorize\s*\(\s*"([^"]+)"\s*\)'
    
    def replace_permissions(match):
        annotation_content = match.group(1)
        
        # Check if this annotation contains old permission codes
        has_old_codes = any(f"hasAuthority('{old}')" in annotation_content for old in permission_mappings.keys())
        
        if not has_old_codes:
            return match.group(0)  # No changes needed
        
        # Split by 'or' to handle multiple permissions
        parts = annotation_content.split(' or ')
        new_parts = []
        
        for part in parts:
            # Extract the permission code from hasAuthority('CODE')
            perm_match = re.search(r"hasAuthority\('([^']+)'\)", part)
            if perm_match:
                old_code = perm_match.group(1)
                if old_code in permission_mappings:
                    # Skip this part if the new code is already in the annotation
                    new_code = permission_mappings[old_code]
                    if f"hasAuthority('{new_code}')" in annotation_content:
                        continue  # Skip old code if new code already exists
                    else:
                        # Replace old code with new code
                        new_parts.append(f"hasAuthority('{new_code}')")
                else:
                    # Keep the part as is (it's already in new format)
                    new_parts.append(part.strip())
        
        # If we have new parts, reconstruct the annotation
        if new_parts:
            new_annotation = ' or '.join(new_parts)
            return f'@PreAuthorize("{new_annotation}")'
        else:
            return match.group(0)
    
    # Replace all @PreAuthorize annotations
    content = re.sub(pattern, replace_permissions, content)
    
    # If content changed, write it back
    if content != original_content:
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False

def main():
    controller_dir = r"D:\code\Beijing-Language-and-Culture-University3\backend\src\main\java\com\proshine\system\controller"
    
    updated_files = []
    
    # Process all Java files in the controller directory
    for filename in os.listdir(controller_dir):
        if filename.endswith('.java'):
            file_path = os.path.join(controller_dir, filename)
            if update_permissions_in_file(file_path):
                updated_files.append(filename)
                print(f"Updated: {filename}")
    
    print(f"\nTotal files updated: {len(updated_files)}")
    if updated_files:
        print("Updated files:")
        for f in updated_files:
            print(f"  - {f}")

if __name__ == "__main__":
    main()