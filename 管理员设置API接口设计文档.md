# 管理员设置模块 API 接口设计文档

## 📋 目录

1. [项目背景与分析](#项目背景与分析)
2. [现有API设计模式](#现有api设计模式)
3. [功能需求分析](#功能需求分析)
4. [接口设计规范](#接口设计规范)
5. [管理员设置接口详细设计](#管理员设置接口详细设计)
6. [数据库设计建议](#数据库设计建议)
7. [实施建议](#实施建议)

---

## 📊 项目背景与分析

### 系统架构
- **前端**: Vue 3 + Element Plus
- **后端**: Spring Boot + JPA/Hibernate
- **数据库**: MySQL
- **认证**: Spring Security + JWT
- **权限**: 基于注解的方法级权限控制

### 现有权限体系
项目已实现完整的RBAC权限管理体系：
- `SysUser` - 系统用户实体
- `SysRole` - 系统角色实体  
- `SysPermission` - 系统权限实体
- `SysUserRole` - 用户角色关联
- 支持多租户（customerId）

---

## 🏗️ 现有API设计模式

### 1. 统一响应格式
```java
public class ResponseEntity<T> {
    private int code;           // 状态码 (200=成功)
    private String message;     // 响应消息
    private T data;            // 响应数据
}

// 分页响应
public class ResponsePageDataEntity<T> {
    private List<T> rows;      // 数据列表
    private Long total;        // 总记录数
    private Integer pageNumber; // 当前页码
    private Integer pageSize;   // 每页大小
}
```

### 2. 控制器规范
```java
@RestController
@RequestMapping("/路径")
@Slf4j
public class XxxController {
    
    @PostMapping("/search")
    @PreAuthorize("hasAuthority('system:xxx:view')")
    public ResponseEntity<ResponsePageDataEntity<XxxVo>> search(@RequestBody SearchCondition condition) {
        // 实现逻辑
    }
}
```

### 3. 权限控制模式
- 使用 `@PreAuthorize` 注解进行方法级权限控制
- 权限命名规范: `system:模块:操作` (如: `system:user:view`)
- 支持多权限组合: `hasAuthority('system:user:view') or hasAuthority('system:user:manage')`

### 4. 安全机制
- 自动获取当前用户客户域: `SecurityUtil.getCustomerId()`
- 请求参数自动设置租户隔离
- 统一异常处理和日志记录

---

## 🎯 功能需求分析

### 管理员设置核心功能

#### 1. 管理员列表管理
- **分页查询**: 支持按姓名、登录账号、角色、状态筛选
- **添加管理员**: 选择用户并分配角色
- **编辑管理员**: 修改用户角色信息  
- **删除管理员**: 移除管理员权限
- **状态管理**: 启用/禁用管理员账号

#### 2. 用户选择功能
- **人员搜索**: 复用现有 `searchPersonnel` 接口
- **多选支持**: 批量添加管理员
- **去重处理**: 避免重复添加

#### 3. 角色管理集成
- **角色列表**: 获取可分配的角色列表
- **角色权限**: 展示角色对应的权限信息
- **角色验证**: 确保角色分配的合法性

---

## 📐 接口设计规范

### 1. 路径规范
遵循RESTful设计原则，按模块划分：
- **基础路径**: `/admin-management`
- **管理员操作**: `/admin-management/admins/*`
- **角色相关**: `/admin-management/roles/*`

### 2. HTTP方法约定
- `GET`: 查询单个资源
- `POST`: 创建资源、复杂查询
- `PUT`: 更新资源
- `DELETE`: 删除资源

### 3. 权限命名规范
- `system:admin:view` - 查看管理员
- `system:admin:create` - 创建管理员
- `system:admin:update` - 更新管理员
- `system:admin:delete` - 删除管理员
- `system:admin:manage` - 管理员管理（包含所有操作）

---

## 🔌 管理员设置接口详细设计

### 1. 分页查询管理员列表

#### 接口信息
- **路径**: `POST /admin-management/admins/search`
- **权限**: `@PreAuthorize("hasAuthority('system:admin:view') or hasAuthority('system:admin:manage')")`

#### 请求参数
```java
public class SearchAdminCondition {
    private String adminName;        // 管理员姓名(模糊查询)
    private String loginAccount;     // 登录账号(模糊查询)  
    private String accountStatus;    // 账号状态: ACTIVE, INACTIVE
    private String roleId;          // 角色ID
    private String roleName;        // 角色名称
    private Integer pageNumber;     // 页码(从1开始)
    private Integer pageSize;       // 每页大小
    private String customerId;      // 客户域ID(自动设置)
}
```

#### 响应数据
```java
public class AdminVo {
    private String id;              // 用户ID
    private String username;        // 登录用户名
    private String realName;        // 真实姓名
    private String gender;          // 性别
    private String phone;           // 手机号
    private String email;           // 邮箱
    private String departmentName;  // 部门名称
    private String positionName;    // 职位名称
    private String jobNumber;       // 工号
    private String accountStatus;   // 账号状态
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime lastLoginTime; // 最后登录时间
    
    // 角色相关
    private List<AdminRoleVo> roles; // 用户角色列表
    
    // 微信绑定信息
    private String wechatNickname;   // 微信昵称
    private String wechatPhone;      // 微信绑定手机号
    private LocalDateTime wechatBindTime; // 微信绑定时间
}

public class AdminRoleVo {
    private String roleId;          // 角色ID
    private String roleName;        // 角色名称
    private String roleDescription; // 角色描述
    private String roleType;        // 角色类型
}
```

#### 响应示例
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "rows": [
            {
                "id": "user123",
                "username": "admin001",
                "realName": "张三",
                "gender": "男",
                "phone": "13800138001",
                "email": "zhangsan@example.com",
                "departmentName": "技术部",
                "positionName": "技术经理",
                "jobNumber": "T001",
                "accountStatus": "ACTIVE",
                "createTime": "2024-01-01T10:00:00",
                "lastLoginTime": "2024-08-13T09:30:00",
                "roles": [
                    {
                        "roleId": "role001",
                        "roleName": "超级管理员",
                        "roleDescription": "系统超级管理员",
                        "roleType": "ADMIN"
                    }
                ],
                "wechatNickname": "张三的微信",
                "wechatPhone": "13800138001",
                "wechatBindTime": "2024-01-02T14:00:00"
            }
        ],
        "total": 100,
        "pageNumber": 1,
        "pageSize": 10
    }
}
```

### 2. 添加管理员

#### 接口信息
- **路径**: `POST /admin-management/admins/add`
- **权限**: `@PreAuthorize("hasAuthority('system:admin:create') or hasAuthority('system:admin:manage')")`

#### 请求参数
```java
public class AddAdminRequest {
    private List<String> userIds;   // 用户ID列表
    private List<String> roleIds;   // 角色ID列表
    private String customerId;      // 客户域ID(自动设置)
}
```

#### 请求示例
```json
{
    "userIds": ["user001", "user002", "user003"],
    "roleIds": ["role001", "role002"]
}
```

#### 响应数据
```java
public class AddAdminResponse {
    private Integer successCount;    // 成功添加数量
    private Integer failCount;      // 失败数量
    private List<String> failUserIds; // 失败的用户ID列表
    private List<String> failReasons; // 失败原因列表
}
```

### 3. 更新管理员角色

#### 接口信息
- **路径**: `PUT /admin-management/admins/{userId}/roles`
- **权限**: `@PreAuthorize("hasAuthority('system:admin:update') or hasAuthority('system:admin:manage')")`

#### 请求参数
```java
public class UpdateAdminRolesRequest {
    private List<String> roleIds;   // 新的角色ID列表
}
```

### 4. 删除管理员

#### 接口信息
- **路径**: `DELETE /admin-management/admins/{userId}`
- **权限**: `@PreAuthorize("hasAuthority('system:admin:delete') or hasAuthority('system:admin:manage')")`

#### 响应数据
```json
{
    "code": 200,
    "message": "管理员删除成功",
    "data": null
}
```

### 5. 批量删除管理员

#### 接口信息
- **路径**: `DELETE /admin-management/admins/batch`
- **权限**: `@PreAuthorize("hasAuthority('system:admin:delete') or hasAuthority('system:admin:manage')")`

#### 请求参数
```java
public class BatchDeleteAdminRequest {
    private List<String> userIds;   // 用户ID列表
}
```

### 6. 更新管理员状态

#### 接口信息
- **路径**: `PUT /admin-management/admins/{userId}/status`
- **权限**: `@PreAuthorize("hasAuthority('system:admin:update') or hasAuthority('system:admin:manage')")`

#### 请求参数
```java
public class UpdateAdminStatusRequest {
    private String status;          // 状态: ACTIVE, INACTIVE
}
```

### 7. 获取可分配角色列表

#### 接口信息
- **路径**: `GET /admin-management/roles/available`
- **权限**: `@PreAuthorize("hasAuthority('system:admin:view') or hasAuthority('system:admin:manage')")`

#### 响应数据
```java
public class AvailableRoleVo {
    private String roleId;          // 角色ID
    private String roleName;        // 角色名称
    private String roleDescription; // 角色描述
    private String roleType;        // 角色类型
    private Boolean isDefault;      // 是否默认角色
    private List<String> permissions; // 权限列表
}
```

### 8. 复用现有人员搜索接口

#### 接口信息
- **路径**: `POST /scheme-management/personnel/list`
- **说明**: 复用现有接口，无需新增

---

## 🗄️ 数据库设计建议

### 现有表结构分析

#### sys_user 表（已存在）
```sql
CREATE TABLE sys_user (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键UUID',
    username VARCHAR(50) COMMENT '登录用户名',
    password VARCHAR(100) COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    gender VARCHAR(10) COMMENT '性别',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    job_number VARCHAR(30) COMMENT '工号',
    department_id VARCHAR(32) COMMENT '部门ID',
    department_name VARCHAR(100) COMMENT '部门名称',
    status VARCHAR(20) COMMENT '用户状态',
    customer_id VARCHAR(32) COMMENT '客户域ID',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    last_login_time DATETIME COMMENT '最后登录时间'
);
```

#### sys_user_role 表（已存在）
```sql
CREATE TABLE sys_user_role (
    id VARCHAR(32) PRIMARY KEY COMMENT '主键UUID',
    user_id VARCHAR(32) COMMENT '用户ID',
    role_id VARCHAR(32) COMMENT '角色ID',
    customer_id VARCHAR(32) COMMENT '客户域ID',
    create_time DATETIME COMMENT '创建时间'
);
```

### 索引优化建议
```sql
-- 管理员查询优化索引
CREATE INDEX idx_user_search ON sys_user (customer_id, status, real_name, username);
CREATE INDEX idx_user_department ON sys_user (customer_id, department_id, status);
CREATE INDEX idx_user_role_user ON sys_user_role (user_id, customer_id);
CREATE INDEX idx_user_role_role ON sys_user_role (role_id, customer_id);
```

---

## 🚀 实施建议

### 1. 开发优先级

#### 第一阶段（核心功能）
1. 管理员列表查询接口
2. 添加管理员接口  
3. 删除管理员接口
4. 角色列表查询接口

#### 第二阶段（增强功能）
1. 批量操作接口
2. 状态管理接口
3. 管理员详情接口
4. 权限验证优化

### 2. 技术实现要点

#### 权限控制
```java
@Component
public class AdminPermissionValidator {
    
    /**
     * 验证用户是否可以分配指定角色
     */
    public boolean canAssignRole(String userId, String roleId) {
        // 1. 检查当前用户权限级别
        // 2. 检查目标角色权限级别  
        // 3. 确保不能分配比自己更高的权限
        return true;
    }
}
```

#### 事务管理
```java
@Service
@Transactional
public class AdminManagementService {
    
    @Transactional(rollbackFor = Exception.class)
    public void addAdmins(AddAdminRequest request) {
        // 批量操作使用事务确保数据一致性
    }
}
```

#### 缓存策略
```java
@Cacheable(value = "adminRoles", key = "#userId")
public List<RoleVo> getUserRoles(String userId) {
    // 缓存用户角色信息
}
```

### 3. 前后端联调要点

#### 前端适配
1. 统一错误处理机制
2. 分页参数标准化
3. 权限按钮显示控制
4. 加载状态管理

#### 接口测试
1. 单元测试覆盖
2. 集成测试验证
3. 权限测试确认
4. 性能测试评估

### 4. 安全考虑

#### 数据安全
- 敏感数据加密存储
- 操作日志记录
- 数据访问审计

#### 接口安全  
- 参数校验
- SQL注入防护
- 权限越权检查
- 频率限制

---

## 📝 总结

本文档基于项目现有架构和代码规范，设计了完整的管理员设置模块API接口。主要特点：

1. **遵循现有规范**: 严格按照项目现有的API设计模式和代码风格
2. **权限安全**: 完整的权限控制和安全验证机制
3. **扩展性强**: 支持多租户、批量操作、状态管理等高级功能
4. **性能优化**: 合理的数据库索引和缓存策略
5. **易于维护**: 清晰的接口定义和完整的文档说明

建议按照第一阶段优先级进行开发，确保核心功能快速上线，后续逐步完善增强功能。