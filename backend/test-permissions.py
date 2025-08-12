#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
权限测试脚本
测试不同权限的接口访问
"""

import requests
import json
import sys

# 服务器地址
BASE_URL = "http://localhost:10086"

# 测试用户
TEST_USER = {
    "username": "test",
    "password": "123456",
    "customerId": "demo"
}

def login(username, password, customer_id="demo"):
    """登录获取token"""
    url = f"{BASE_URL}/authentication/login"
    data = {
        "username": username,
        "password": password,
        "customerId": customer_id
    }
    response = requests.post(url, json=data)
    if response.status_code == 200:
        result = response.json()
        if result.get("code") == 200:
            return result["data"]["token"], result["data"]
    return None, None

def test_api(token, method, endpoint, data=None, name=""):
    """测试API接口"""
    url = f"{BASE_URL}{endpoint}"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    try:
        if method == "GET":
            response = requests.get(url, headers=headers)
        elif method == "POST":
            response = requests.post(url, headers=headers, json=data or {})
        elif method == "PUT":
            response = requests.put(url, headers=headers, json=data or {})
        elif method == "DELETE":
            response = requests.delete(url, headers=headers)
        else:
            return False, "不支持的方法"
        
        status_code = response.status_code
        if status_code == 200:
            return True, "✓ 访问成功"
        elif status_code == 403:
            return False, "✗ 权限不足(403)"
        elif status_code == 401:
            return False, "✗ 未授权(401)"
        else:
            return False, f"✗ 其他错误({status_code})"
    except Exception as e:
        return False, f"✗ 请求异常: {str(e)}"

def main():
    print("=" * 60)
    print("权限控制测试脚本")
    print("=" * 60)
    
    # 1. 登录
    print("\n1. 登录测试用户...")
    token, login_data = login(TEST_USER["username"], TEST_USER["password"], TEST_USER["customerId"])
    if not token:
        print("✗ 登录失败")
        sys.exit(1)
    
    print("✓ 登录成功")
    print(f"   用户名: {login_data.get('username')}")
    print(f"   权限数: {len(login_data.get('permissions', []))}")
    print(f"   权限列表: {', '.join(login_data.get('permissions', []))}")
    
    # 2. 测试权限接口
    print("\n2. 测试权限接口...")
    
    test_cases = [
        # 用户管理接口
        ("POST", "/system/user/search", {"pageNum": 1, "pageSize": 10}, "用户查询(需要USER_SEARCH)"),
        ("GET", "/system/user/findById?id=test", None, "用户查看(需要USER_VIEW)"),
        ("POST", "/system/user/save", {"username": "test_new"}, "用户新增(需要USER_ADD)"),
        
        # 角色管理接口
        ("POST", "/role-management/search", {"pageNum": 1, "pageSize": 10}, "角色查询(需要ROLE_SEARCH)"),
        
        # 教室管理接口
        ("POST", "/api/room/search", {"pageNumber": 1, "pageSize": 10}, "教室查询(需要ROOM_SEARCH)"),
        ("GET", "/api/room/types", None, "教室类型查询(需要ROOM_SEARCH)"),
        
        # 预订管理接口
        ("POST", "/api/room-booking/my-bookings", {"pageNum": 1, "pageSize": 10}, "我的预约(需要登录)"),
        ("GET", "/api/room-booking/stats", None, "预订统计(需要BOOKING_VIEW)"),
        
        # 权限查询接口
        ("GET", "/api/user/current/auth-info", None, "当前用户权限信息(需要登录)"),
        
        # 测试权限接口
        ("GET", "/test/permission/current", None, "测试当前权限(需要登录)"),
        ("GET", "/test/permission/need-search", None, "测试USER_SEARCH权限"),
        ("GET", "/test/permission/need-add", None, "测试USER_ADD权限"),
    ]
    
    for method, endpoint, data, name in test_cases:
        success, message = test_api(token, method, endpoint, data, name)
        print(f"\n   {name}:")
        print(f"   {method} {endpoint}")
        print(f"   结果: {message}")
    
    # 3. 统计结果
    print("\n" + "=" * 60)
    print("测试完成！")
    print("\n说明:")
    print("- ✓ 表示有权限访问")
    print("- ✗ 表示无权限访问")
    print("- test用户当前角色为'普通用户'，拥有基础权限")
    print("- 如需测试其他权限，请分配相应角色")

if __name__ == "__main__":
    main()