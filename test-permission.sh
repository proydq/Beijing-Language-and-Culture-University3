#!/bin/bash

# 测试权限控制的脚本

# 1. 登录获取token
echo "=== 1. 登录获取token ==="
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:10086/authentication/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "password": "123456",
    "customerId": "demo"
  }')

echo "登录响应："
echo $LOGIN_RESPONSE | jq .

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.data.token')
echo "获取到的Token: ${TOKEN:0:50}..."

# 2. 测试获取当前用户权限信息
echo -e "\n=== 2. 测试获取当前用户权限信息 ==="
curl -s -X GET http://localhost:10086/test/permission/current \
  -H "Authorization: Bearer $TOKEN" | jq .

# 3. 测试访问需要USER_SEARCH权限的接口（应该成功）
echo -e "\n=== 3. 测试访问需要USER_SEARCH权限的接口 ==="
curl -s -X GET http://localhost:10086/test/permission/need-search \
  -H "Authorization: Bearer $TOKEN" | jq .

# 4. 测试访问需要USER_ADD权限的接口（应该失败）
echo -e "\n=== 4. 测试访问需要USER_ADD权限的接口 ==="
curl -s -X GET http://localhost:10086/test/permission/need-add \
  -H "Authorization: Bearer $TOKEN" | jq .

# 5. 测试访问用户查询接口（应该成功，因为有USER_SEARCH权限）
echo -e "\n=== 5. 测试访问用户查询接口 ==="
curl -s -X POST http://localhost:10086/system/user/search \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "pageNum": 1,
    "pageSize": 10
  }' | jq .

# 6. 测试访问用户权限信息接口
echo -e "\n=== 6. 测试获取用户权限信息 ==="
curl -s -X GET http://localhost:10086/api/user/current/auth-info \
  -H "Authorization: Bearer $TOKEN" | jq .