# SKILL管理平台 - API依赖关系文档

**文档版本：** V1.0
**创建日期：** 2026-05-17

---

## 1. API调用顺序依赖

### 1.1 SKILL上传流程

```
用户登录(POST /auth/login)
    ↓ 获得token
SKILL上传(POST /skills/upload)
    ↓ 返回skillId
格式校验(自动触发)
    ↓ 校验通过
管理员审核(POST /skills/{id}/review)
    ↓ 审核通过
SKILL发布(自动更新状态)
```

**依赖关系：**
- `/skills/upload` 依赖 `/auth/login` 获取认证token
- `/skills/{id}/review` 依赖 `/skills/upload` 返回的skillId
- 审核通过后自动触发状态变更，无需额外API

### 1.2 SKILL部署流程

```
查询SKILL详情(GET /skills/{id})
    ↓ 确认skillId和版本
一键部署(POST /skills/{id}/deploy)
    ↓ 返回deploymentId
轮询部署状态(GET /deployments/{id})
    ↓ 状态为running
访问endpoint
```

**依赖关系：**
- `/skills/{id}/deploy` 依赖skill存在且状态为approved/published
- `/deployments/{id}` 依赖 `/skills/{id}/deploy` 返回的deploymentId

### 1.3 套件创建与部署流程

```
查询可用SKILL(GET /skills?skillType=public)
    ↓ 选择skillIds
创建套件(POST /suites)
    ↓ 返回suiteId
套件部署(POST /suites/{id}/deploy)
    ↓ 按依赖顺序自动部署各SKILL
```

**依赖关系：**
- `/suites` 依赖至少2个已发布的SKILL
- `/suites/{id}/deploy` 依赖套件状态为published

### 1.4 SKILL评价流程

```
下载SKILL(GET /skills/{id}/download)
    ↓ 下载完成
提交评价(POST /skills/{id}/ratings)
```

**依赖关系：**
- `/skills/{id}/ratings` 依赖用户有下载记录

### 1.5 SKILL分享流程

```
生成分享链接(POST /skills/{id}/share)
    ↓ 返回shareToken
访问分享链接(GET /shared/{token})
    ↓ 查看SKILL详情
下载(SKILL需有权限)
```

**依赖关系：**
- `/shared/{token}` 不依赖认证
- 分享链接下载需用户有下载权限

### 1.6 用户注册审核流程

```
用户注册(POST /auth/register)
    ↓ 状态为pending
管理员审核(PUT /users/{id}/status)
    ↓ 状态改为active
用户登录(POST /auth/login)
```

**依赖关系：**
- `/auth/login` 依赖用户状态为active
- 管理员操作依赖ADMIN角色

---

## 2. API参数到数据模型字段映射

### 2.1 用户认证模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| employeeId | employee_id | users |
| password | password_hash | users(bcrypt加密) |
| name | name | users |
| email | email | users |
| department | department | users |
| token | JWT Claims | - |
| refreshToken | Redis存储 | - |

### 2.2 SKILL上传模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| file | file_path | skill_files(MinIO存储) |
| skillType | skill_type | skills |
| name | name | skills |
| description | description | skills |
| version | version | skill_versions |
| category | category | skills |
| tags | tag_id | skill_tags → tags |

### 2.3 SKILL查询模块

| API参数 | 数据模型字段 | 表名 | 查询方式 |
|---------|-------------|------|---------|
| keyword | name, description | skills | ES全文检索 |
| skillType | skill_type | skills | 精确匹配 |
| category | category | skills | 精确匹配 |
| tags | tag_id | skill_tags | JOIN查询 |
| sortBy=downloadCount | download_count | skills | 排序 |
| sortBy=avgRating | avg_rating | skills | 排序 |
| page, pageSize | - | - | LIMIT/OFFSET |

### 2.4 SKILL部署模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| skillId | skill_id | deployments |
| version | version_id | deployments |
| config.port | config_value | deployment_configs |
| config.replicas | config_value | deployment_configs |
| config.resources.cpu | config_value | deployment_configs |
| config.resources.memory | config_value | deployment_configs |
| status | status | deployments |
| endpoint | endpoint | deployments |

### 2.5 版本管理模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| targetVersion | version | skill_versions |
| tag | tag | skill_versions |
| changelog | changelog | skill_versions |

### 2.6 套件管理模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| name | name | suites |
| description | description | suites |
| skillIds | skill_id | suite_skills |
| category | category | suites |
| visibility | visibility | suites |
| sortOrder | sort_order | suite_skills(部署顺序) |

### 2.7 审核模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| action | action | skill_reviews |
| comment | comment | skill_reviews |
| status→skill.status | status | skills |
| reviewedBy | reviewer_id | skill_reviews |

### 2.8 评价模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| rating | rating | skill_ratings |
| comment | content | skill_comments |
| avg_rating | avg_rating | skills(聚合计算) |
| rating_count | rating_count | skills(聚合计算) |

### 2.9 收藏模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| skillId | skill_id | skill_favorites |
| userId | user_id | skill_favorites |

### 2.10 分享模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| shareType | share_type | skill_shares |
| shareToken | share_token | skill_shares |
| expiresAt | expires_at | skill_shares |

### 2.11 图谱模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| depth | 关联查询深度 | skill_relations(递归) |
| position | position | position_skills |
| relationType | relation_type | skill_relations |
| importance | importance | position_skills |

### 2.12 日志模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| logType | log_type | operation_logs |
| startTime | created_at >= | operation_logs |
| endTime | created_at <= | operation_logs |
| userId | user_id | operation_logs |
| action | action | operation_logs |

### 2.13 配置模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| configKey | config_key | system_configs |
| configValue | config_value | system_configs |

### 2.14 通知模块

| API参数 | 数据模型字段 | 表名 |
|---------|-------------|------|
| type | type | notifications |
| isRead | is_read | notifications |
| channel | channel | notifications |

---

## 3. 模块间依赖关系图

```
┌─────────────────────────────────────────────────────────────┐
│                      认证服务(F001)                          │
│  登录/注册/Token管理/密码管理                                  │
└──────────────────────────┬──────────────────────────────────┘
                           │ 所有模块依赖认证
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      权限服务(F002)                          │
│  角色管理/权限校验/RBAC控制                                    │
└──────────────────────────┬──────────────────────────────────┘
                           │ 业务模块依赖权限校验
                           ▼
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│ 上传(F003)│  │ 存储(F005)│  │ 检索(F006)│  │ 下载(F007)│
│          │→│          │→│          │→│          │
└──────────┘  └────┬─────┘  └──────────┘  └──────────┘
                   │
         ┌─────────┼─────────┬──────────┬──────────┐
         ▼         ▼         ▼          ▼          ▼
    ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
    │版本(F009)│ │审核(F011)│ │部署(F008)│ │评价(F013)│ │套件(F010)│
    └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘
                   │
         ┌─────────┼─────────┬──────────┐
         ▼         ▼         ▼          ▼
    ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐
    │收藏(F014)│ │分享(F015)│ │图谱(F016)│ │通知(F012)│
    └─────────┘ └─────────┘ └─────────┘ └─────────┘
```

### 3.1 核心依赖链

1. **认证 → 所有模块**：JWT令牌校验
2. **权限 → 业务操作**：RBAC权限校验
3. **上传 → 校验 → 存储**：文件处理链
4. **存储 → 版本/审核/部署/检索**：SKILL数据依赖
5. **审核 → 通知**：审核结果触发通知
6. **下载 → 评价**：下载记录是评价前置条件
7. **版本 → 套件/部署/图谱**：版本管理支撑

### 3.2 数据流向

```
用户上传文件 → MinIO存储
                ↓
         PostgreSQL元数据
                ↓
         Elasticsearch索引
                ↓
         用户检索/下载/部署
```
