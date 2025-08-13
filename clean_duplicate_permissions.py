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

def clean_duplicate_permissions():
    conn = None
    cursor = None
    try:
        # 连接数据库
        conn = pymysql.connect(**DB_CONFIG)
        cursor = conn.cursor()
        
        # 查找重复的角色权限分配
        cursor.execute("""
            SELECT role_id, permission_id, MIN(id) as keep_id, COUNT(*) as count
            FROM sys_role_permission 
            GROUP BY role_id, permission_id 
            HAVING COUNT(*) > 1
        """)
        
        duplicates = cursor.fetchall()
        print(f"找到 {len(duplicates)} 个重复的权限分配")
        
        total_deleted = 0
        
        for role_id, permission_id, keep_id, count in duplicates:
            # 删除除了最早的记录之外的所有重复记录
            cursor.execute("""
                DELETE FROM sys_role_permission 
                WHERE role_id = %s AND permission_id = %s AND id != %s
            """, (role_id, permission_id, keep_id))
            
            deleted_count = cursor.rowcount
            total_deleted += deleted_count
            print(f"清理权限 {permission_id} (角色: {role_id}): 删除 {deleted_count} 个重复记录")
        
        # 提交事务
        conn.commit()
        
        print(f"\n清理完成！总共删除了 {total_deleted} 个重复记录")
        
        # 验证结果
        cursor.execute("SELECT COUNT(*) FROM sys_role_permission WHERE role_id = 'role001'")
        admin_permission_count = cursor.fetchone()[0]
        print(f"超级管理员角色现在有 {admin_permission_count} 个权限")
        
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
    clean_duplicate_permissions()