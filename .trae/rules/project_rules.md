## 项目基本信息
- Spring Boot版本 : 2.7.6
- Java版本 : 1.8
- 编码格式 : UTF-8
- 包结构 : com.proshine.{项目名}
## 核心依赖配置
### 基础依赖
```
<properties>
    <java.version>1.8</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.
    sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.
    reporting.outputEncoding>
    <spring-boot.version>2.7.6</spring-boot.version>
</properties>
```
### 主要依赖
- spring-boot-starter-web : Web应用基础
- spring-boot-starter-data-jpa : JPA数据访问
- spring-boot-starter-security : 安全框架
- spring-boot-starter-quartz : 定时任务
- mysql-connector-java : MySQL数据库驱动
- lombok : 代码简化工具
- jjwt (0.9.1) : JWT令牌支持
- okhttp (4.9.2) : HTTP客户端
- commons-lang3 (3.12.0) : 通用工具类
- fastjson (2.0.8) : JSON处理
## 项目架构规范
### 包结构
### 编码规范 实体类规范
- 使用JPA注解： @Entity , @Table , @Id , @Column
- 使用Lombok注解： @Data , @NoArgsConstructor , @AllArgsConstructor
- 主键使用UUID策略： @GeneratedValue(generator = "uuid")
- 字段添加详细的 columnDefinition 注释
- 枚举类型使用 @Enumerated(EnumType.STRING) 控制器规范
- 使用 @RestController 和 @RequestMapping
- 统一返回 ResponseEntity<T> 格式
- 使用 @Slf4j 进行日志记录
- 异常处理使用try-catch包装 服务层规范
- 接口和实现分离
- 使用 @Service 注解
- 方法添加详细注释 数据访问层规范
- 继承 JpaRepository<Entity, String> 和 JpaSpecificationExecutor<Entity>
- 使用 @Repository 注解
- 自定义查询使用 @Query 注解
- 修改操作添加 @Modifying 和 @Transactional
### 配置文件规范 application.properties配置
- 服务端口： server.port=10086
- 数据库配置：MySQL + HikariCP连接池
- JPA配置： spring.jpa.hibernate.ddl-auto=update
- 文件上传限制： spring.servlet.multipart.max-file-size=500MB
- 安全配置：自定义permit-paths
- JWT缓存配置
### 通用响应格式
### Maven插件配置
- spring-boot-maven-plugin
- maven-compiler-plugin (Java 1.8)
- 自定义JVM参数配置

## API接口设计规范

### 1. 控制器层规范

#### 1.1 基础注解规范
```java
@RestController
@RequestMapping(value = "/模块名")
@Slf4j
public class XxxController {
    
    @Autowired
    private XxxService xxxService;
}
```

#### 1.2 URL路径命名规范
- **模块级路径**: 使用小写字母，多个单词用连字符分隔
  - `/system/account` - 系统账号模块
  - `/laboratory-info` - 实验室信息模块
  - `/authentication` - 认证模块
  - `/files` - 文件管理模块

- **操作级路径**: 使用动词或动词短语
  - `/search` - 查询操作
  - `/save` - 保存操作
  - `/findById` - 根据ID查询
  - `/delete` - 删除操作
  - `/moveToRecycleBin` - 移动到回收站
  - `/uploadFile` - 文件上传
  - `/downloadFile` - 文件下载

- **强制规范**: 所有接口路径都不能以`/api`开头

### 2. HTTP方法使用规范

#### 2.1 方法映射规范
```java
// 查询操作 - 使用POST（因为需要传递复杂查询条件）
@PostMapping(value = "/search")
public ResponseEntity<ResponsePageDataEntity<Entity>> search(@RequestBody SearchCondition condition)

// 保存操作 - 使用POST
@PostMapping(value = "/save")
public ResponseEntity<Void> save(@RequestBody Entity entity)

// 简单查询 - 使用GET
@GetMapping(value = "/findById")
public ResponseEntity<Entity> findById(@RequestParam String id)

// 删除操作 - 使用DELETE
@DeleteMapping(value = "/deleteAccount")
public ResponseEntity<Void> deleteAccount(String accountId)

// 状态变更 - 使用GET（简单参数）
@GetMapping(value = "/changeAccountStatus")
public ResponseEntity<Void> changeAccountStatus(String accountId, String option)

// 批量操作 - 使用POST
@PostMapping(value = "/moveToRecycleBin")
public ResponseEntity<Void> moveToRecycleBin(@RequestBody List<String> ids)
```

#### 2.2 HTTP方法选择原则
- **GET**: 简单查询，参数较少
- **POST**: 复杂查询、保存、批量操作、文件上传
- **DELETE**: 删除操作
- **PUT**: 完整更新（项目中较少使用）

### 3. 参数传递规范

#### 3.1 请求参数类型
```java
// 复杂对象参数 - 使用@RequestBody
@PostMapping("/search")
public ResponseEntity<ResponsePageDataEntity<Entity>> search(@RequestBody SearchCondition condition)

// 简单参数 - 使用@RequestParam
@GetMapping("/findById")
public ResponseEntity<Entity> findById(@RequestParam String id)

// 路径参数 - 使用@PathVariable（项目中较少使用）

// 文件上传 - 使用@ModelAttribute
@PostMapping("/uploadFile")
public ResponseEntity<File> uploadFile(@ModelAttribute UploadFileVo fileVo)
```

#### 3.2 查询条件设计规范
```java
// 基础查询条件类
@Data
public class SearchBaseCondition {
    private String customerId;     // 客户域
    private Integer pageNumber = 1; // 当前页码
    private Integer pageSize = 10;  // 每页大小
}

// 具体查询条件继承基础类
@Data
public class SearchAccountCondition extends SearchBaseCondition {
    private String name;           // 人员姓名
    private String email;          // 邮箱
    private String departmentId;   // 所属部门ID
    private Account.Status accountStatus = Account.Status.NORMAL; // 账号状态
}
```

### 4. 响应格式规范

#### 4.1 统一响应实体
```java
@Data
public class ResponseEntity<T> {
    private int code;        // 状态码：200成功，500失败
    private String message;  // 消息信息
    private T data;         // 返回数据
    
    // 成功响应
    public static <T> ResponseEntity<T> success(T data)
    
    // 失败响应
    public static <T> ResponseEntity<T> fail(String message)
}
```

#### 4.2 分页响应实体
```java
@Data
public class ResponsePageDataEntity<T> {
    private Long total;      // 符合要求的数据总条数
    private List<T> rows;    // 数据信息集合
}
```

#### 4.3 响应格式示例
```java
// 成功响应
return ResponseEntity.success(data);

// 分页查询响应
return ResponseEntity.success(pageData);

// 失败响应
return ResponseEntity.fail("错误信息");

// 无返回数据的成功响应
return ResponseEntity.success(null);
```

### 5. 异常处理规范

#### 5.1 统一异常处理模式
```java
@PostMapping("/save")
public ResponseEntity<Void> save(@RequestBody Entity entity) {
    try {
        // 设置客户域
        entity.setCustomerId(SecurityUtil.getCustomerId());
        // 业务逻辑处理
        service.save(entity);
        return ResponseEntity.success(null);
    } catch (Exception e) {
        log.error("保存失败：", e);
        return ResponseEntity.fail(e.getMessage());
    }
}
```

#### 5.2 异常处理要点
- 使用try-catch包装所有业务逻辑
- 记录详细的错误日志
- 返回用户友好的错误信息
- 不暴露系统内部错误细节

### 6. 安全规范

#### 6.1 客户域设置
```java
// 自动设置客户域，避免越权访问
condition.setCustomerId(SecurityUtil.getCustomerId());
entity.setCustomerId(SecurityUtil.getCustomerId());
```

#### 6.2 权限验证
- 通过Spring Security进行接口权限控制
- 在application.properties中配置免认证路径
- 使用JWT进行用户身份验证

### 7. 日志规范

#### 7.1 日志记录要点
```java
// 接口入口日志
log.info("==========/api/authentication/login=============");

// 业务处理日志
log.info("放到jwt中的信息json字符串：{}", jsonString);

// 错误日志
log.error("查询系统账号失败：", e);
```

#### 7.2 日志级别使用
- **INFO**: 重要业务操作记录
- **DEBUG**: 调试信息
- **ERROR**: 异常错误信息

### 8. 文件处理规范

#### 8.1 文件上传接口
```java
@PostMapping("/uploadFile")
public ResponseEntity<File> uploadFile(@ModelAttribute UploadFileVo fileVo)

@PostMapping("/uploadFileAndSaveFileInfo")
public ResponseEntity<File> uploadFileAndSaveFileInfo(@ModelAttribute UploadFileVo fileVo)
```

#### 8.2 文件下载接口
```java
@GetMapping("/downloadFile")
public void downloadFile(@RequestParam String id, HttpServletResponse response)
```

### 9. 批量操作规范

#### 9.1 批量操作接口设计
```java
// 批量删除
@PostMapping("/delete")
public ResponseEntity<Void> delete(@RequestBody List<String> ids)

// 批量移动到回收站
@PostMapping("/moveToRecycleBin")
public ResponseEntity<Void> moveToRecycleBin(@RequestBody List<String> ids)
```

#### 9.2 批量操作要点
- 使用POST方法传递ID列表
- 在Service层进行批量处理
- 注意事务处理

### 10. API文档规范

#### 10.1 方法注释规范
```java
/**
 * 分页查询系统账号信息
 *
 * @param condition 查询条件实体类
 * @return ResponseEntity<ResponsePageDataEntity<AccountVo>>
 */
@PostMapping("/search")
public ResponseEntity<ResponsePageDataEntity<AccountVo>> searchAccount(@RequestBody SearchAccountCondition condition)
```

#### 10.2 注释要点
- 详细描述接口功能
- 说明参数含义
- 明确返回值类型
- 特殊业务逻辑需要额外说明

## 总结

这套API规范确保了接口的一致性、安全性和可维护性，为前后端协作提供了标准化的接口设计模式，适合企业级Spring Boot应用开发。




## 身份认证安全规范
### 1.1 JWT令牌认证
- 使用JWT (JSON Web Token) 进行用户身份验证
- 依赖 jjwt (0.9.1) 库实现JWT功能
- 通过 JwtAuthenticationFilter 过滤器进行令牌验证
- 支持JWT缓存机制，可通过 jwt.cache.enabled 配置开关
### 1.2 密码安全
- 使用 BCryptPasswordEncoder 进行密码加密
- 密码存储采用不可逆加密算法
### 1.3 会话管理
- 采用无状态会话策略： SessionCreationPolicy.STATELESS
- 禁用CSRF保护（适用于API接口）
## 2. 权限控制安全规范
### 2.1 基于角色的访问控制(RBAC)
- 实现自定义 CustomAccessDecisionManager 访问决策管理器
- 使用 CustomFilterSecurityMetadataSource 动态获取资源权限
- 支持细粒度的URL级别权限控制
### 2.2 权限验证流程
```
// 权限检查逻辑
- 白名单路径直接放行
- 动态从数据库获取URL对应的角色权限
- 匹配用户角色与资源所需权限
- 无权限时抛出AccessDeniedException
```
### 2.3 方法级安全
- 启用 @EnableGlobalMethodSecurity(prePostEnabled = true)
- 支持 @PreAuthorize 和 @PostAuthorize 注解
## 3. 客户域隔离安全规范
### 3.1 多租户数据隔离
```
// 自动设置客户域，防止越权访问
condition.setCustomerId(SecurityUtil.getCustomerId());
entity.setCustomerId(SecurityUtil.getCustomerId());
```
### 3.2 数据访问控制
- 所有查询条件继承 SearchBaseCondition ，包含 customerId 字段
- 通过 SecurityUtil.getCustomerId() 获取当前用户客户域
- 确保用户只能访问自己客户域的数据
## 4. 接口安全规范
### 4.1 白名单配置
- 通过 security.permit-paths 配置免认证路径
- 当前配置： /api/authentication/login,/danger-source-category-and-item/save
- 注意 ：项目已规定不能使用 /api 前缀，需要调整白名单配置
### 4.2 请求过滤
- 所有请求经过 JwtAuthenticationFilter 验证
- Authorization头部令牌格式验证
- 令牌过期和有效性检查
## 5. 数据库安全规范
### 5.1 连接安全
- 使用HikariCP连接池，配置连接超时和池大小限制
- 数据库连接参数： maximum-pool-size=20 , minimum-idle=5
- 连接超时设置： connection-timeout=30000ms
### 5.2 SQL安全
- 使用JPA/Hibernate ORM，防止SQL注入
- 自定义查询使用 @Query 注解，参数化查询
- 修改操作添加 @Modifying 和 @Transactional 注解
## 6. 文件上传安全规范
### 6.1 文件大小限制
- 单个文件最大： 500MB
- 请求总大小最大： 500MB
- 通过Spring Boot配置进行限制
### 6.2 文件处理安全
- 使用 @ModelAttribute 接收文件上传参数
- 文件信息存储到数据库进行管理
## 7. 异常处理安全规范
### 7.1 错误信息安全
- 统一异常处理，避免敏感信息泄露
- 返回用户友好的错误信息
- 详细错误信息仅记录到日志中
### 7.2 访问拒绝处理
- 自定义访问拒绝响应格式
- 返回标准化的401/403状态码
## 8. 日志安全规范
### 8.1 安全日志记录
```
// 记录关键安全事件
log.info("用户登录: {}", username);
log.debug("权限检查: {} for URL: {}", authority, requestUrl);
log.error("访问被拒绝: {}", requestUrl);
```
### 8.2 敏感信息保护
- 避免在日志中记录密码、令牌等敏感信息
- 使用适当的日志级别（DEBUG、INFO、ERROR）
## 9. 配置安全规范
### 9.1 敏感配置保护
- 数据库密码等敏感信息应使用环境变量或加密配置
- JWT密钥应定期轮换
- 生产环境禁用SQL日志输出
### 9.2 环境隔离
- 开发、测试、生产环境使用不同的配置
- 生产环境启用更严格的安全策略
## 10. 合规性要求
### 10.1 数据保护
- 实现客户域数据隔离，符合数据保护要求
- 用户数据访问需要相应权限验证
### 10.2 审计追踪
- 记录用户操作日志
- 保留安全相关的审计信息
- 支持操作溯源和责任追踪