# SKILL管理平台 - 架构设计文档

**文档版本：** V1.0
**创建日期：** 2026-05-17

---

## 修订历史

| 版本 | 日期 | 修改说明 | 修改人 |
|------|------|---------|--------|
| V1.0 | 2026-05-17 | 初始创建 | AI架构师 |

---

## 1. 引言

### 1.1 目的

本文档定义SKILL管理平台的系统架构、模块划分、技术选型和核心流程设计，为开发团队提供实现指导。

### 1.2 设计范围

SKILL管理平台后端系统，包括用户认证、SKILL生命周期管理、审核、部署、评价、图谱等21个功能模块。

### 1.3 技术约束

| 约束类型 | 具体要求 |
|---------|---------|
| 后端框架 | Java 21 + Spring Boot 3.2 |
| 数据库 | PostgreSQL 16 |
| 缓存 | Redis 7 |
| 文件存储 | MinIO/S3 |
| 搜索引擎 | Elasticsearch 8 |
| 部署方式 | Docker / K8s |

---

## 2. 架构概述

### 2.1 架构风格

采用**分层架构 + 模块化单体**风格：

- 初期以单体部署，降低运维复杂度
- 按业务域划分模块，模块间通过接口通信
- 预留微服务拆分能力（模块边界清晰）

### 2.2 系统架构图

```
┌─────────────────────────────────────────────────────────────────┐
│                        客户端层                                  │
│   Web浏览器(React 19)    │    CLI工具    │    第三方集成          │
└────────────────────────────┬────────────────────────────────────┘
                             │ HTTPS
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                     API网关 / 负载均衡                           │
│              Nginx / K8s Ingress / Spring Gateway               │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                   SKILL管理平台后端服务                           │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │ 认证模块  │ │ SKILL模块│ │ 审核模块  │ │ 部署模块  │          │
│  │ (Auth)   │ │ (Skill)  │ │ (Review) │ │ (Deploy) │          │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘          │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │ 用户模块  │ │ 图谱模块  │ │ 统计模块  │ │ 通知模块  │          │
│  │ (User)   │ │ (Graph)  │ │ (Stats)  │ │ (Notify) │          │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘          │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │                    公共基础层                             │   │
│  │  认证鉴权 │ 文件处理 │ 缓存管理 │ 日志 │ 异常处理 │ 事件  │   │
│  └─────────────────────────────────────────────────────────┘   │
└────────────────────────────┬────────────────────────────────────┘
                             │
        ┌────────────────────┼────────────────────┐
        ▼                    ▼                    ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│  PostgreSQL  │  │    Redis     │  │   MinIO/S3   │
│   主数据库    │  │    缓存      │  │   文件存储    │
└──────────────┘  └──────────────┘  └──────────────┘
        │
        ▼
┌──────────────┐
│Elasticsearch │
│   搜索引擎   │
└──────────────┘
```

### 2.3 技术栈选型

| 层次 | 技术选择 | 选择理由 |
|------|---------|---------|
| Web框架 | Spring Boot 3.2 | 企业级标准，生态成熟 |
| 安全框架 | Spring Security + JWT | 与Spring Boot深度集成 |
| ORM | Spring Data JPA + MyBatis | JPA处理简单CRUD，MyBatis处理复杂查询 |
| 缓存 | Spring Data Redis | 统一缓存抽象 |
| 文件存储 | MinIO Java SDK | S3兼容API |
| 搜索 | Spring Data Elasticsearch | 全文检索 |
| 任务调度 | Spring Scheduler + XXL-JOB | 定时任务和分布式调度 |
| 消息队列 | RabbitMQ / Spring Events | 异步通知和事件驱动 |
| API文档 | SpringDoc OpenAPI 3.0 | 自动生成Swagger文档 |
| 容器化 | Docker + Docker Compose | 标准化部署 |

---

## 3. 模块划分

### 3.1 模块结构

```
skill-platform/
├── skill-common/              # 公共模块
│   ├── src/main/java/
│   │   └── com.skill.platform.common/
│   │       ├── config/        # 公共配置
│   │       ├── exception/     # 异常定义
│   │       ├── response/      # 统一响应
│   │       ├── util/          # 工具类
│   │       └── constant/      # 常量定义
│   └── pom.xml
│
├── skill-auth/                # 认证授权模块
│   ├── src/main/java/
│   │   └── com.skill.platform.auth/
│   │       ├── controller/    # API控制器
│   │       ├── service/       # 业务逻辑
│   │       ├── repository/    # 数据访问
│   │       ├── model/         # 实体/DTO
│   │       ├── security/      # 安全配置
│   │       └── event/         # 事件定义
│   └── pom.xml
│
├── skill-core/                # SKILL核心模块
│   ├── src/main/java/
│   │   └── com.skill.platform.core/
│   │       ├── controller/    # API控制器
│   │       ├── service/       # 业务逻辑
│   │       │   ├── skill/     # SKILL管理
│   │       │   ├── version/   # 版本管理
│   │       │   ├── upload/    # 上传处理
│   │       │   ├── download/  # 下载处理
│   │       │   ├── search/    # 搜索服务
│   │       │   └── validation/# 格式校验
│   │       ├── repository/    # 数据访问
│   │       └── model/         # 实体/DTO
│   └── pom.xml
│
├── skill-review/              # 审核模块
│   └── ...
│
├── skill-deploy/              # 部署模块
│   └── ...
│
├── skill-suite/               # 套件模块
│   └── ...
│
├── skill-social/              # 社交模块(评价/收藏/分享)
│   └── ...
│
├── skill-graph/               # 图谱模块
│   └── ...
│
├── skill-stats/               # 统计模块
│   └── ...
│
├── skill-notify/              # 通知模块
│   └── ...
│
├── skill-admin/               # 管理模块(日志/配置/帮助/反馈)
│   └── ...
│
└── skill-app/                 # 应用启动模块
    ├── src/main/java/
    │   └── com.skill.platform/
    │       └── SkillPlatformApplication.java
    ├── src/main/resources/
    │   └── application.yml
    └── pom.xml
```

### 3.2 模块职责

| 模块 | 职责 | 对外接口 |
|------|------|---------|
| skill-common | 公共配置、工具类、异常定义 | 内部依赖 |
| skill-auth | 用户认证、JWT管理、RBAC权限 | /api/v1/auth/*, /api/v1/users/*, /api/v1/roles/* |
| skill-core | SKILL CRUD、上传下载、搜索、版本管理 | /api/v1/skills/* |
| skill-review | SKILL审核流程 | /api/v1/reviews/* |
| skill-deploy | 一键部署、容器管理 | /api/v1/deployments/* |
| skill-suite | 套件管理、依赖解析 | /api/v1/suites/* |
| skill-social | 评价、收藏、分享 | /api/v1/ratings/*, /api/v1/favorites/*, /api/v1/share/* |
| skill-graph | SKILL关联图谱、职位映射 | /api/v1/skills/*/relations, /api/v1/graph/* |
| skill-stats | 数据统计、趋势分析 | /api/v1/statistics/* |
| skill-notify | 通知发送、通知设置 | /api/v1/notifications/* |
| skill-admin | 日志、配置、帮助、反馈 | /api/v1/logs/*, /api/v1/configs/*, /api/v1/help/*, /api/v1/feedbacks/* |
| skill-app | 应用启动、配置组装 | - |

### 3.3 模块依赖关系

```
skill-app
    ├── skill-auth
    ├── skill-core
    │   └── skill-common
    ├── skill-review
    │   ├── skill-core
    │   └── skill-notify
    ├── skill-deploy
    │   └── skill-core
    ├── skill-suite
    │   └── skill-core
    ├── skill-social
    │   └── skill-core
    ├── skill-graph
    │   └── skill-core
    ├── skill-stats
    │   └── skill-core
    ├── skill-notify
    │   └── skill-auth
    └── skill-admin
        └── skill-auth
```

---

## 4. 核心流程设计

### 4.1 SKILL上传流程

```
用户                  Controller          ValidationService       FileService           SkillService          DB/MinIO
 │                       │                      │                     │                     │                    │
 │ POST /skills/upload   │                      │                     │                     │                    │
 │──────────────────────>│                      │                     │                     │                    │
 │                       │   validate(file)     │                     │                     │                    │
 │                       │─────────────────────>│                     │                     │                    │
 │                       │                      │  格式/命名/安全校验   │                     │                    │
 │                       │   ValidationResult   │                     │                     │                    │
 │                       │<─────────────────────│                     │                     │                    │
 │                       │                      │                     │                     │                    │
 │                       │   uploadFile(file)   │                     │                     │                    │
 │                       │──────────────────────────────────────────>│                     │                    │
 │                       │                      │                     │  存储到MinIO         │                    │
 │                       │                      │                     │──────────────────────────────────────────>│
 │                       │                      │                     │  file_path           │                    │
 │                       │   filePath           │                     │<──────────────────────────────────────────│
 │                       │<──────────────────────────────────────────│                     │                    │
 │                       │                      │                     │                     │                    │
 │                       │   createSkill(dto)   │                     │                     │                    │
 │                       │──────────────────────────────────────────────────────────────>│                    │
 │                       │                      │                     │                     │  INSERT skills     │
 │                       │                      │                     │                     │──────────────────>│
 │                       │                      │                     │                     │  skillId           │
 │                       │                      │                     │                     │<──────────────────│
 │                       │   SkillDTO           │                     │                     │                    │
 │                       │<──────────────────────────────────────────────────────────────│                    │
 │   201 Created         │                      │                     │                     │                    │
 │<──────────────────────│                      │                     │                     │                    │
 │                       │                      │                     │                     │                    │
 │                       │   (异步) 发送审核通知   │                     │                     │                    │
 │                       │────────────────────────────────────────────────────────────────────────────────────>│
```

### 4.2 SKILL一键部署流程

```
用户                  Controller          DeployService          DockerService         SkillService
 │                       │                      │                     │                     │
 │ POST /skills/{id}/deploy                     │                     │                     │
 │──────────────────────>│                      │                     │                     │
 │                       │  checkPermission()   │                     │                     │
 │                       │──────────────────────────────────────────────────────────────>│
 │                       │  hasPermission       │                     │                     │
 │                       │<──────────────────────────────────────────────────────────────│
 │                       │                      │                     │                     │
 │                       │  deploy(skillId, config)                    │                     │
 │                       │─────────────────────>│                     │                     │
 │                       │                      │                     │                     │
 │                       │                      │  获取SKILL文件        │                     │
 │                       │                      │──────────────────────────────────────────>│
 │                       │                      │  fileStream         │                     │
 │                       │                      │<──────────────────────────────────────────│
 │                       │                      │                     │                     │
 │                       │                      │  buildImage(file)   │                     │
 │                       │                      │────────────────────>│                     │
 │                       │                      │  imageId            │                     │
 │                       │                      │<────────────────────│                     │
 │                       │                      │                     │                     │
 │                       │                      │  runContainer(image, config)              │
 │                       │                      │────────────────────>│                     │
 │                       │                      │  containerId, endpoint                   │
 │                       │                      │<────────────────────│                     │
 │                       │                      │                     │                     │
 │                       │                      │  保存部署记录         │                     │
 │                       │                      │──────────────────────────────────────────>│
 │                       │  DeploymentDTO       │                     │                     │
 │                       │<─────────────────────│                     │                     │
 │   202 Accepted        │                      │                     │                     │
 │<──────────────────────│                      │                     │                     │
```

### 4.3 SKILL审核流程

```
[待审核] ──管理员批准──> [已发布]
    │                      │
    │                      │
    └──管理员拒绝──> [已拒绝] ──开发者修改重新提交──> [待审核]
```

**状态机定义：**

| 当前状态 | 事件 | 目标状态 | 操作 |
|---------|------|---------|------|
| draft | submit | pending_review | 发送审核通知 |
| pending_review | approve | published | 发送审核结果通知 |
| pending_review | reject | rejected | 发送审核结果通知 |
| rejected | resubmit | pending_review | 发送审核通知 |
| published | archive | archived | - |
| archived | restore | published | - |

### 4.4 用户认证流程

```
用户                  AuthController       AuthService           JWTService            Redis
 │                       │                    │                     │                    │
 │ POST /auth/login      │                    │                     │                    │
 │──────────────────────>│                    │                     │                    │
 │                       │  authenticate()    │                     │                    │
 │                       │───────────────────>│                     │                    │
 │                       │                    │  校验密码            │                    │
 │                       │                    │  检查账号状态         │                    │
 │                       │                    │                     │                    │
 │                       │                    │  generateToken()    │                    │
 │                       │                    │────────────────────>│                    │
 │                       │                    │  accessToken        │                    │
 │                       │                    │<────────────────────│                    │
 │                       │                    │                     │                    │
 │                       │                    │  存储refreshToken   │                    │
 │                       │                    │────────────────────────────────────────>│
 │                       │  LoginResponse     │                     │                    │
 │                       │<───────────────────│                     │                    │
 │   200 OK              │                    │                     │                    │
 │<──────────────────────│                    │                     │                    │
```

### 4.5 权限校验流程

```
请求                  AuthFilter           JWTService            PermissionService     Redis/DB
 │                       │                    │                     │                    │
 │ Authorization: Bearer │                    │                     │                    │
 │──────────────────────>│                    │                     │                    │
 │                       │  validateToken()   │                     │                    │
 │                       │───────────────────>│                     │                    │
 │                       │  Claims            │                     │                    │
 │                       │<───────────────────│                     │                    │
 │                       │                    │                     │                    │
 │                       │  checkPermission(userId, resource)       │                    │
 │                       │──────────────────────────────────────>│                    │
 │                       │                    │                     │  查询Redis缓存      │
 │                       │                    │                     │──────────────────>│
 │                       │                    │                     │  permissions       │
 │                       │                    │                     │<──────────────────│
 │                       │                    │                     │                    │
 │                       │                    │                     │  缓存miss则查DB     │
 │                       │  allowed           │                     │                    │
 │                       │<──────────────────────────────────────│                    │
 │                       │                    │                     │                    │
 │   继续处理/403         │                    │                     │                    │
 │<──────────────────────│                    │                     │                    │
```

---

## 5. 事件驱动设计

### 5.1 事件定义

| 事件 | 触发时机 | 处理方 |
|------|---------|--------|
| SkillUploadedEvent | SKILL上传完成 | 格式校验、索引更新 |
| SkillValidatedEvent | 格式校验完成 | 状态更新 |
| SkillApprovedEvent | 审核通过 | 状态更新、通知 |
| SkillRejectedEvent | 审核拒绝 | 状态更新、通知 |
| SkillDownloadedEvent | SKILL下载 | 下载统计、日志 |
| SkillDeployedEvent | 部署成功 | 部署统计、日志 |
| UserRegisteredEvent | 用户注册 | 通知管理员审核 |
| RatingSubmittedEvent | 评价提交 | 更新平均评分 |

### 5.2 事件处理流程

```java
// 事件发布
@Service
public class SkillService {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public SkillDTO uploadSkill(MultipartFile file, SkillUploadRequest request) {
        // ... 上传逻辑
        eventPublisher.publishEvent(new SkillUploadedEvent(skill));
        return skillDTO;
    }
}

// 事件监听
@Component
public class SkillEventListener {
    @Async
    @EventListener
    public void onSkillUploaded(SkillUploadedEvent event) {
        // 异步执行格式校验
        validationService.validate(event.getSkill());
    }

    @Async
    @EventListener
    public void onSkillApproved(SkillApprovedEvent event) {
        // 异步更新ES索引
        searchService.indexSkill(event.getSkill());
        // 异步发送通知
        notificationService.sendApprovalNotification(event.getSkill());
    }
}
```

---

## 6. 缓存架构

### 6.1 缓存层次

```
请求 → 本地缓存(Caffeine) → Redis缓存 → 数据库
```

### 6.2 缓存策略

| 数据类型 | 缓存位置 | TTL | 更新策略 |
|---------|---------|-----|---------|
| 用户信息 | Redis | 5min | Cache-Aside |
| 用户权限 | Redis | 5min | Cache-Aside |
| SKILL详情 | Redis | 5min | Cache-Aside |
| 热门SKILL | Redis SortedSet | 10min | 定时刷新 |
| 搜索结果 | Redis | 5min | Cache-Aside |
| 系统配置 | Caffeine + Redis | 本地1min/Redis30min | 写时失效 |
| 分类列表 | Redis | 30min | 定时刷新 |

---

## 7. 错误处理架构

### 7.1 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<?> handleBusinessException(BusinessException e) {
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<?> handleValidationException(MethodArgumentNotValidException e) {
        return ApiResponse.error("InvalidParameter", e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<?> handleAccessDeniedException(AccessDeniedException e) {
        return ApiResponse.error("PermissionDenied", "权限不足");
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleException(Exception e) {
        log.error("Unexpected error", e);
        return ApiResponse.error("InternalError", "服务器内部错误");
    }
}
```

### 7.2 错误码体系

```
错误码格式: {模块前缀}{3位数字}

通用错误:    Success, InvalidParameter, Unauthorized, PermissionDenied, NotFound, Conflict, InternalError
认证错误:    AUTH001-AUTH099
权限错误:    PERM001-PERM099
上传错误:    UPLOAD001-UPLOAD099
SKILL错误:   SKILL001-SKILL099
下载错误:    DOWNLOAD001-DOWNLOAD099
部署错误:    DEPLOY001-DEPLOY099
版本错误:    VERSION001-VERSION099
套件错误:    SUITE001-SUITE099
审核错误:    REVIEW001-REVIEW099
评价错误:    RATING001-RATING099
图谱错误:    GRAPH001-GRAPH099
日志错误:    LOG001-LOG099
配置错误:    CONFIG001-CONFIG099
搜索错误:    SEARCH001-SEARCH099
```

---

## 8. 中间件设计

### 8.1 请求处理管道

```
请求 → Nginx → API Gateway
    → JWT认证过滤器
    → 权限校验过滤器
    → 请求日志拦截器
    → 限流拦截器
    → Controller
    → Service
    → Repository
    → 响应
```

### 8.2 中间件清单

| 中间件 | 顺序 | 功能 |
|--------|------|------|
| JwtAuthenticationFilter | 1 | JWT令牌解析和验证 |
| PermissionFilter | 2 | RBAC权限校验 |
| RequestLoggingInterceptor | 3 | 请求日志记录 |
| RateLimitInterceptor | 4 | API限流 |
| CorsFilter | 5 | 跨域配置 |
| ResponseWrapperAdvice | 6 | 响应体统一包装 |

---

## 9. 数据库访问架构

### 9.1 读写分离（预留）

```
写请求 → Master DB
读请求 → Slave DB (通过@ReadOnly注解路由)
```

### 9.2 事务管理

- 写操作使用Spring声明式事务 `@Transactional`
- 只读查询使用 `@Transactional(readOnly = true)`
- 跨模块操作使用编程式事务

### 9.3 分页查询

使用Spring Data的Pageable统一处理分页：

```java
@GetMapping("/skills")
public ApiResponse<PageResponse<SkillDTO>> listSkills(
    @RequestParam(defaultValue = "1") int page,
    @RequestParam(defaultValue = "20") int pageSize,
    SkillQueryParams params) {
    Page<Skill> result = skillService.findSkills(params, PageRequest.of(page - 1, pageSize));
    return ApiResponse.ok(PageResponse.from(result));
}
```

---

## 10. 安全架构

### 10.1 认证流程

```
1. 用户提交工号+密码
2. 服务端校验密码(bcrypt)
3. 检查账号状态(active/locked/disabled)
4. 生成JWT(含userId, roles, permissions)
5. 生成refreshToken存入Redis
6. 返回token对给客户端
```

### 10.2 鉴权流程

```
1. 请求携带Authorization: Bearer {token}
2. JwtFilter解析token，验证签名和过期时间
3. 从token中提取userId和roles
4. PermissionFilter检查接口所需权限
5. 权限不足返回403
```

### 10.3 安全头配置

```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())  // 前后端分离，使用JWT
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'"))
                .frameOptions(frame -> frame.deny())
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }
}
```

---

## 11. 可扩展性设计

### 11.1 SKILL格式扩展

通过策略模式支持新增SKILL格式：

```java
public interface SkillValidator {
    boolean supports(String fileType);
    ValidationResult validate(InputStream file);
}

@Component
public class JsonSkillValidator implements SkillValidator {
    @Override
    public boolean supports(String fileType) {
        return "json".equals(fileType);
    }
    // ...
}
```

### 11.2 通知渠道扩展

```java
public interface NotificationChannel {
    String getChannelType();
    void send(Notification notification);
}

@Component
public class InAppNotificationChannel implements NotificationChannel { ... }

@Component
public class EmailNotificationChannel implements NotificationChannel { ... }
```

### 11.3 部署目标扩展

```java
public interface DeploymentTarget {
    String getTargetType();
    DeploymentResult deploy(DeploymentRequest request);
}

@Component
public class DockerDeploymentTarget implements DeploymentTarget { ... }

@Component
public class K8sDeploymentTarget implements DeploymentTarget { ... }
```

---

## 12. 详细流程设计

### 12.1 SKILL上传子模块

#### 12.1.1 处理流程

```
1. 接收上传请求(MultipartFile + 元数据)
2. 文件预校验
   a. 检查文件扩展名(.json/.skill/.zip)
   b. 检查文件大小(<=100MB)
   c. 检查文件名(无特殊字符)
3. 计算文件checksum(SHA256)
4. 上传文件到MinIO
   a. 生成存储路径: /skills/{skillId}/{version}/{filename}
   b. 上传文件流
   c. 验证上传完整性
5. 创建SKILL记录(状态: draft)
6. 创建版本记录
7. 创建文件记录
8. 处理标签(创建不存在的标签)
9. 发布SkillUploadedEvent
10. 返回SKILL信息
```

#### 12.1.2 分片上传流程

```
1. 初始化上传会话
   a. 生成uploadId
   b. 计算分片数
   c. 创建临时目录
   d. 返回uploadId和分片信息
2. 逐片上传
   a. 接收分片数据
   b. 校验分片checksum
   c. 存储到临时目录
   d. 记录已上传分片
   e. 返回上传进度
3. 完成上传
   a. 检查所有分片是否上传完成
   b. 合并分片为完整文件
   c. 校验完整文件checksum
   d. 上传合并后的文件到MinIO
   e. 清理临时文件
   f. 创建SKILL记录
   g. 返回SKILL信息
```

#### 12.1.3 关键算法

**断点续传判断：**
```
输入: uploadId, chunkNo
处理:
1. 查询Redis获取已上传分片列表: SISMEMBER upload:{uploadId}:chunks {chunkNo}
2. 如果已上传，跳过
3. 如果未上传，执行上传
输出: 是否需要上传
```

---

### 12.2 SKILL格式校验子模块

#### 12.2.1 校验流程

```
1. 文件格式校验
   a. 检查文件扩展名
   b. 检查文件魔数(magic number)
   c. 不通过则返回UPLOAD001

2. 文件命名校验
   a. 检查是否包含特殊字符
   b. 检查是否包含路径遍历字符(..)
   c. 检查是否为保留文件名
   d. 不通过则返回UPLOAD003

3. 文件内容校验(JSON格式)
   a. 解析JSON语法
   b. 校验必需字段(name, version, triggers, steps)
   c. 不通过则返回校验错误详情

4. 文件内容校验(ZIP格式)
   a. 解压ZIP文件
   b. 检查核心文件是否存在
   c. 校验内部文件格式
   d. 不通过则返回校验错误详情

5. 版本格式校验
   a. 检查版本号格式(x.y.z)
   b. 检查版本号是否已存在
   c. 不通过则返回VERSION002

6. 大小校验
   a. 单文件<=100MB
   b. 批量总大小<=500MB
   c. 个人存储<=1GB
   d. 不通过则返回UPLOAD002/UPLOAD004

7. 安全校验
   a. 恶意代码扫描
   b. 敏感信息检测
   c. 不通过则返回安全错误
```

#### 12.2.2 校验器实现

```java
public interface FileValidator {
    int getOrder();
    ValidationResult validate(FileValidationContext context);
}

@Component
public class ValidationChain {
    private final List<FileValidator> validators;

    public ValidationResult validate(FileValidationContext context) {
        List<ValidationError> errors = new ArrayList<>();
        for (FileValidator validator : validators) {
            ValidationResult result = validator.validate(context);
            if (!result.isValid()) {
                errors.addAll(result.getErrors());
            }
        }
        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
    }
}
```

---

### 12.3 SKILL搜索子模块

#### 12.3.1 搜索流程

```
1. 接收搜索请求(keyword, filters)
2. 参数校验和清洗
3. 检查Redis缓存
   a. 缓存命中，返回缓存结果
   b. 缓存未命中，继续
4. 构建ES查询
   a. 全文检索(name, description, tags)
   b. 过滤条件(skillType, category, status)
   c. 高亮显示匹配内容
   d. 计算相关度评分
5. 执行ES查询
6. 补充搜索建议
7. 缓存搜索结果(TTL 5min)
8. 返回搜索结果
```

#### 12.3.2 ES索引更新策略

```
SKILL创建/更新 -> 发布SkillIndexEvent -> 异步更新ES索引
SKILL删除 -> 发布SkillDeleteEvent -> 异步删除ES索引
定时任务 -> 每天凌晨全量重建索引
```

---

### 12.4 部署子模块

#### 12.4.1 部署流程

```
1. 接收部署请求(skillId, version, config)
2. 权限校验(所有者或管理员)
3. 获取SKILL文件
   a. 从MinIO下载SKILL文件到临时目录
   b. 解压文件
4. 构建Docker镜像
   a. 生成Dockerfile
   b. 执行docker build
   c. 推送镜像到私有仓库
5. 创建部署记录(状态: deploying)
6. 启动容器
   a. 配置端口映射
   b. 配置资源限制
   c. 配置副本数
   d. 执行docker run / kubectl apply
7. 健康检查
   a. 轮询容器状态
   b. 检查端口是否可访问
   c. 超时5分钟自动回滚
8. 更新部署记录(状态: running)
9. 返回部署结果
```

#### 12.4.2 部署状态机

```
[创建] --开始部署--> [部署中]
[部署中] --成功--> [运行中]
[部署中] --失败--> [失败]
[部署中] --超时--> [失败] (自动回滚)
[运行中] --停止--> [已停止]
[运行中] --回滚--> [回滚中]
[回滚中] --成功--> [运行中]
[回滚中] --失败--> [失败]
[已停止] --重启--> [部署中]
```

---

### 12.5 审核子模块

#### 12.5.1 审核流程

```
1. 开发者提交SKILL审核
   a. 检查SKILL状态(draft/rejected)
   b. 更新状态为pending_review
   c. 发送通知给管理员

2. 管理员审核
   a. 获取待审核列表
   b. 查看SKILL详情和校验结果
   c. 执行审核操作:
      - 批准: 更新状态为published，发送通知给开发者
      - 拒绝: 更新状态为rejected，记录拒绝原因，发送通知给开发者

3. 审核超时处理
   a. 定时任务检查24小时未审核的SKILL
   b. 发送提醒通知给管理员
```

#### 12.5.2 并发控制

```
审核操作使用分布式锁:
1. 获取锁: SET review:lock:{skillId} {userId} NX EX 300
2. 执行审核
3. 释放锁: DEL review:lock:{skillId}
4. 锁超时自动释放(5分钟)
```

---

### 12.6 图谱子模块

#### 12.6.1 关联查询流程

```
1. 接收查询请求(skillId, depth)
2. 查询直接关联(prerequisite, advanced, related)
3. 递归查询关联的关联(直到达到depth)
4. 检测并避免循环关联
5. 构建图谱数据(nodes, edges)
6. 返回图谱结果
```

#### 12.6.2 关联推荐算法

```
1. 基于分类相似度
   - 同分类的SKILL推荐为related
2. 基于标签重叠
   - 标签重叠度>50%推荐为related
3. 基于用户行为
   - 经常一起下载的SKILL推荐为related
4. 基于管理员配置
   - 管理员手动设置的关联优先级最高
```

---

### 12.7 统计子模块

#### 12.7.1 统计计算流程

```
实时指标(SKILL总数、今日上传/下载/部署):
  -> 直接查询数据库COUNT

小时级指标(热门SKILL排行):
  -> 定时任务每小时计算，存入Redis SortedSet

天级指标(活跃用户、趋势数据):
  -> 定时任务每天凌晨计算，存入统计表

历史趋势:
  -> 从统计表查询，支持日/周/月维度
```

---

## 13. 总结

### 13.1 关键设计决策

| 决策 | 选择 | 理由 |
|------|------|------|
| 架构风格 | 分层架构+模块化单体 | 初期降低复杂度，预留微服务能力 |
| 认证方案 | JWT + Spring Security | 无状态、易扩展、与Spring深度集成 |
| 文件存储 | MinIO | S3兼容、私有部署、成本低 |
| 搜索引擎 | Elasticsearch | 全文检索、模糊匹配、高亮显示 |
| 缓存策略 | Redis Cache-Aside | 简单可靠、性能好 |
| 事件驱动 | Spring Events | 轻量级、无需额外中间件 |
| 部署方式 | Docker + K8s | 标准化、自动化、易扩展 |

### 13.2 风险和缓解

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| ES集群故障 | 搜索不可用 | 降级为数据库LIKE查询 |
| MinIO故障 | 文件不可用 | 多副本+跨区域备份 |
| 大文件上传超时 | 上传失败 | 分片上传+断点续传 |
| 部署超时 | 部署失败 | 5分钟超时+自动回滚 |
| 并发审核冲突 | 数据不一致 | 分布式锁+乐观锁 |
