#!/usr/bin/env python3
import requests
import json

# 测试配置
BASE_URL = "http://localhost:10086"
LOGIN_URL = f"{BASE_URL}/authentication/login"
USER_URL = f"{BASE_URL}/system/user/search"

def test_login_and_permissions():
    # 1. 登录获取token
    login_data = {
        "username": "admin",
        "password": "123456"
    }
    
    print("1. 测试登录...")
    response = requests.post(LOGIN_URL, json=login_data)
    if response.status_code != 200:
        print(f"登录失败: {response.status_code} - {response.text}")
        return
    
    login_result = response.json()
    if not login_result.get("success"):
        print(f"登录失败: {login_result}")
        return
        
    token = login_result["data"]["token"]
    permissions = login_result["data"]["permissions"]
    
    print(f"登录成功，获得token: {token[:50]}...")
    print(f"用户权限数量: {len(permissions)}")
    print(f"前5个权限: {permissions[:5]}")
    
    # 2. 测试API调用
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    
    search_data = {
        "pageNumber": 1,
        "pageSize": 10,
        "username": "",
        "realName": ""
    }
    
    print(f"\n2. 测试用户查询API...")
    try:
        response = requests.post(USER_URL, json=search_data, headers=headers, timeout=10)
        print(f"API响应状态码: {response.status_code}")
        
        if response.status_code == 200:
            print("✅ API调用成功！权限验证通过")
            result = response.json()
            if result.get("success"):
                print(f"查询结果: 找到 {len(result.get('data', {}).get('content', []))} 个用户")
            else:
                print(f"API返回错误: {result}")
        elif response.status_code == 403:
            print("❌ 权限不足 (403)")
            print(f"响应内容: {response.text}")
        else:
            print(f"❌ API调用失败: {response.status_code}")
            print(f"响应内容: {response.text}")
    except Exception as e:
        print(f"❌ 请求异常: {e}")

if __name__ == "__main__":
    test_login_and_permissions()