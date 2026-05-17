# SKILL管理平台 - 数据模型设计文档

**文档版本：** V1.0
**创建日期：** 2026-05-17
**编写人：** AI架构师
**审核人：** -

---

## 修订历史

| 版本 | 日期 | 修改说明 | 修改人 | 审核人 |
|------|------|---------|--------|--------|
| V1.0 | 2026-05-17 | 初始创建 | AI架构师 | - |

---

## 1. 引言

### 1.1 编写目的

本文档定义SKILL管理平台的数据库表结构、索引策略、缓存设计和全局数据结构，为后端开发提供数据层实现依据。

### 1.2 适用范围

SKILL管理平台后端服务，涵盖用户管理、SKILL生命周期管理、审核、部署、评价、收藏、分享、图谱、统计、日志、配置等全部功能模块。

### 1.3 参考文档

| 文档名称 | 文档编号 | 版本 | 发布日期 |
|----------|---------|------|---------|
| SKILL管理平台系统需求规格说明 | - | V1.0 | 2026-05-16 |

### 1.4 术语与缩写

| 术语/缩写 | 解释说明 |
|-----------|---------|
| SKILL | 平台管理的技能资源单元 |
| Suite | 套件SKILL，由多个SKILL组合而成 |
| RBAC | 基于角色的访问控制 |

---

## 2. 数据库环境

- **数据库类型：** PostgreSQL 16
- **字符集：** UTF-8
- **排序规则：** en_US.UTF-8

---

## 3. 数据库命名规范

| 对象类型 | 命名格式 | 示例 | 说明 |
|---------|---------|------|------|
| 表 | 模块_业务含义 | skill_versions | 小写字母+下划线 |
| 字段 | 业务含义_类型 | created_at | 小写字母+下划线 |
| 主键 | pk_表名 | pk_skills | - |
| 外键 | fk_源表_目标表 | fk_skill_versions_skills | - |
| 索引 | idx_表名_字段名 | idx_skills_name | - |
| 唯一索引 | uk_表名_字段名 | uk_users_employee_id | - |

---

## 4. 逻辑设计

### 4.1 数据表清单

| 表名 | 中文描述 | 所属模块 |
|------|---------|---------|
| users | 用户信息表 | F001用户认证 |
| roles | 角色表 | F002权限管理 |
| user_roles | 用户角色关联表 | F002权限管理 |
| permissions | 权限表 | F002权限管理 |
| role_permissions | 角色权限关联表 | F002权限管理 |
| skills | SKILL信息表 | F005存储服务 |
| skill_versions | SKILL版本表 | F009版本管理 |
| skill_files | SKILL文件表 | F003上传服务 |
| skill_tags | SKILL标签关联表 | F005存储服务 |
| tags | 标签表 | F005存储服务 |
| skill_reviews | SKILL审核记录表 | F011审核服务 |
| skill_ratings | SKILL评分表 | F013评价服务 |
| skill_comments | SKILL评论表 | F013评价服务 |
| skill_favorites | SKILL收藏表 | F014收藏服务 |
| skill_shares | SKILL分享表 | F015分享服务 |
| suites | 套件信息表 | F010套件管理 |
| suite_skills | 套件SKILL关联表 | F010套件管理 |
| deployments | 部署记录表 | F008部署服务 |
| deployment_configs | 部署配置表 | F008部署服务 |
| download_logs | 下载记录表 | F007下载服务 |
| operation_logs | 操作日志表 | F018日志管理 |
| notifications | 通知表 | F012消息通知 |
| user_notification_settings | 用户通知设置表 | F012消息通知 |
| system_configs | 系统配置表 | F019系统配置 |
| help_docs | 帮助文档表 | F020帮助中心 |
| feedbacks | 反馈表 | F021意见反馈 |
| skill_relations | SKILL关联关系表 | F016图谱服务 |
| position_skills | 职位SKILL映射表 | F016图谱服务 |

### 4.2 详细表结构

#### 4.2.1 users（用户信息表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 用户ID |
| employee_id | VARCHAR | 50 | NO | UNI | - | 公司工号 |
| name | VARCHAR | 100 | NO | - | - | 用户姓名 |
| email | VARCHAR | 200 | NO | UNI | - | 公司邮箱 |
| password_hash | VARCHAR | 255 | NO | - | - | 加密密码(bcrypt) |
| department | VARCHAR | 100 | NO | - | - | 部门名称 |
| avatar_url | VARCHAR | 500 | YES | - | NULL | 头像URL |
| status | VARCHAR | 20 | NO | - | 'pending' | 状态: pending/active/locked/disabled |
| last_login_at | TIMESTAMPTZ | - | YES | - | NULL | 最后登录时间 |
| login_fail_count | INT | - | NO | - | 0 | 连续登录失败次数 |
| locked_until | TIMESTAMPTZ | - | YES | - | NULL | 锁定截止时间 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |
| updated_at | TIMESTAMPTZ | - | NO | - | NOW() | 更新时间 |

**索引设计：**
- 主键：`pk_users` (id)
- 唯一索引：`uk_users_employee_id` (employee_id)
- 唯一索引：`uk_users_email` (email)
- 普通索引：`idx_users_status` (status)
- 普通索引：`idx_users_department` (department)

---

#### 4.2.2 roles（角色表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 角色ID |
| name | VARCHAR | 50 | NO | UNI | - | 角色名称(USER/DEVELOPER/ADMIN/SUPER_ADMIN) |
| display_name | VARCHAR | 100 | NO | - | - | 显示名称 |
| description | VARCHAR | 500 | YES | - | NULL | 角色描述 |
| is_system | BOOLEAN | - | NO | - | FALSE | 是否系统内置角色 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |
| updated_at | TIMESTAMPTZ | - | NO | - | NOW() | 更新时间 |

**索引设计：**
- 主键：`pk_roles` (id)
- 唯一索引：`uk_roles_name` (name)

---

#### 4.2.3 user_roles（用户角色关联表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 主键 |
| user_id | UUID | - | NO | FK | - | 用户ID |
| role_id | UUID | - | NO | FK | - | 角色ID |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_user_roles` (id)
- 唯一索引：`uk_user_roles_user_role` (user_id, role_id)
- 外键：`fk_user_roles_users` (user_id → users.id)
- 外键：`fk_user_roles_roles` (role_id → roles.id)

---

#### 4.2.4 permissions（权限表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 权限ID |
| code | VARCHAR | 100 | NO | UNI | - | 权限编码(如skill:public:read) |
| name | VARCHAR | 100 | NO | - | - | 权限名称 |
| resource | VARCHAR | 100 | NO | - | - | 资源类型 |
| action | VARCHAR | 50 | NO | - | - | 操作类型(read/write/delete/admin) |
| description | VARCHAR | 500 | YES | - | NULL | 权限描述 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_permissions` (id)
- 唯一索引：`uk_permissions_code` (code)
- 普通索引：`idx_permissions_resource_action` (resource, action)

---

#### 4.2.5 role_permissions（角色权限关联表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 主键 |
| role_id | UUID | - | NO | FK | - | 角色ID |
| permission_id | UUID | - | NO | FK | - | 权限ID |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_role_permissions` (id)
- 唯一索引：`uk_role_permissions_role_perm` (role_id, permission_id)
- 外键：`fk_role_permissions_roles` (role_id → roles.id)
- 外键：`fk_role_permissions_permissions` (permission_id → permissions.id)

---

#### 4.2.6 skills（SKILL信息表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | SKILL ID |
| name | VARCHAR | 200 | NO | - | - | SKILL名称 |
| description | TEXT | - | YES | - | NULL | 描述(最大500字) |
| skill_type | VARCHAR | 20 | NO | - | - | 类型: public/suite/personal |
| category | VARCHAR | 100 | NO | - | - | 分类 |
| current_version | VARCHAR | 50 | NO | - | '1.0.0' | 当前版本号 |
| status | VARCHAR | 30 | NO | - | 'draft' | 状态: draft/pending_review/approved/rejected/published/archived |
| owner_id | UUID | - | NO | FK | - | 所有者ID |
| developer_name | VARCHAR | 100 | YES | - | NULL | 开发者姓名(冗余) |
| download_count | INT | - | NO | - | 0 | 下载次数 |
| deploy_count | INT | - | NO | - | 0 | 部署次数 |
| avg_rating | DECIMAL | 3,2 | NO | - | 0.00 | 平均评分 |
| rating_count | INT | - | NO | - | 0 | 评价人数 |
| visibility | VARCHAR | 20 | NO | - | 'company' | 可见范围: department/company |
| file_size | BIGINT | - | NO | - | 0 | 文件大小(字节) |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |
| updated_at | TIMESTAMPTZ | - | NO | - | NOW() | 更新时间 |
| published_at | TIMESTAMPTZ | - | YES | - | NULL | 发布时间 |

**索引设计：**
- 主键：`pk_skills` (id)
- 普通索引：`idx_skills_type_status` (skill_type, status)
- 普通索引：`idx_skills_owner_id` (owner_id)
- 普通索引：`idx_skills_category` (category)
- 普通索引：`idx_skills_name_trgm` USING gin (name gin_trgm_ops) -- 模糊搜索
- 普通索引：`idx_skills_download_count` (download_count DESC)
- 普通索引：`idx_skills_avg_rating` (avg_rating DESC)
- 外键：`fk_skills_owner` (owner_id → users.id)

---

#### 4.2.7 skill_versions（SKILL版本表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 版本ID |
| skill_id | UUID | - | NO | FK | - | SKILL ID |
| version | VARCHAR | 50 | NO | - | - | 版本号(x.y.z) |
| tag | VARCHAR | 20 | YES | - | NULL | 标签: latest/beta/stable |
| changelog | TEXT | - | YES | - | NULL | 变更说明 |
| file_path | VARCHAR | 500 | NO | - | - | 文件存储路径 |
| file_size | BIGINT | - | NO | - | 0 | 文件大小(字节) |
| checksum | VARCHAR | 64 | NO | - | - | 文件校验SHA256 |
| created_by | UUID | - | NO | FK | - | 创建者ID |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_skill_versions` (id)
- 唯一索引：`uk_skill_versions_skill_version` (skill_id, version)
- 普通索引：`idx_skill_versions_skill_id` (skill_id)
- 外键：`fk_skill_versions_skills` (skill_id → skills.id)
- 外键：`fk_skill_versions_creator` (created_by → users.id)

---

#### 4.2.8 skill_files（SKILL文件表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 文件ID |
| skill_id | UUID | - | NO | FK | - | SKILL ID |
| version_id | UUID | - | NO | FK | - | 版本ID |
| file_name | VARCHAR | 255 | NO | - | - | 文件名 |
| file_path | VARCHAR | 500 | NO | - | - | MinIO存储路径 |
| file_size | BIGINT | - | NO | - | 0 | 文件大小(字节) |
| file_type | VARCHAR | 20 | NO | - | - | 文件类型: json/skill/zip |
| checksum | VARCHAR | 64 | NO | - | - | 文件校验SHA256 |
| chunk_total | INT | - | NO | - | 1 | 分片总数 |
| chunk_uploaded | INT | - | NO | - | 0 | 已上传分片数 |
| upload_status | VARCHAR | 20 | NO | - | 'uploading' | 上传状态: uploading/completed/failed |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_skill_files` (id)
- 普通索引：`idx_skill_files_skill_id` (skill_id)
- 普通索引：`idx_skill_files_version_id` (version_id)
- 外键：`fk_skill_files_skills` (skill_id → skills.id)
- 外键：`fk_skill_files_versions` (version_id → skill_versions.id)

---

#### 4.2.9 tags（标签表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 标签ID |
| name | VARCHAR | 50 | NO | UNI | - | 标签名称 |
| usage_count | INT | - | NO | - | 0 | 使用次数 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_tags` (id)
- 唯一索引：`uk_tags_name` (name)

---

#### 4.2.10 skill_tags（SKILL标签关联表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 主键 |
| skill_id | UUID | - | NO | FK | - | SKILL ID |
| tag_id | UUID | - | NO | FK | - | 标签ID |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_skill_tags` (id)
- 唯一索引：`uk_skill_tags_skill_tag` (skill_id, tag_id)
- 外键：`fk_skill_tags_skills` (skill_id → skills.id ON DELETE CASCADE)
- 外键：`fk_skill_tags_tags` (tag_id → tags.id)

---

#### 4.2.11 skill_reviews（SKILL审核记录表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 审核ID |
| skill_id | UUID | - | NO | FK | - | SKILL ID |
| version_id | UUID | - | YES | FK | - | 版本ID |
| action | VARCHAR | 20 | NO | - | - | 审核动作: approve/reject |
| comment | TEXT | - | YES | - | NULL | 审核意见 |
| reviewer_id | UUID | - | NO | FK | - | 审核人ID |
| reviewed_at | TIMESTAMPTZ | - | NO | - | NOW() | 审核时间 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_skill_reviews` (id)
- 普通索引：`idx_skill_reviews_skill_id` (skill_id)
- 普通索引：`idx_skill_reviews_reviewer_id` (reviewer_id)
- 外键：`fk_skill_reviews_skills` (skill_id → skills.id)
- 外键：`fk_skill_reviews_versions` (version_id → skill_versions.id)
- 外键：`fk_skill_reviews_reviewer` (reviewer_id → users.id)

---

#### 4.2.12 skill_ratings（SKILL评分表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 评分ID |
| skill_id | UUID | - | NO | FK | - | SKILL ID |
| user_id | UUID | - | NO | FK | - | 用户ID |
| rating | INT | - | NO | - | - | 评分(1-5) |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |
| updated_at | TIMESTAMPTZ | - | NO | - | NOW() | 更新时间 |

**索引设计：**
- 主键：`pk_skill_ratings` (id)
- 唯一索引：`uk_skill_ratings_skill_user` (skill_id, user_id)
- 外键：`fk_skill_ratings_skills` (skill_id → skills.id)
- 外键：`fk_skill_ratings_users` (user_id → users.id)

---

#### 4.2.13 skill_comments（SKILL评论表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 评论ID |
| skill_id | UUID | - | NO | FK | - | SKILL ID |
| user_id | UUID | - | NO | FK | - | 用户ID |
| content | TEXT | - | NO | - | - | 评论内容(最大500字) |
| parent_id | UUID | - | YES | FK | - | 父评论ID(回复) |
| is_deleted | BOOLEAN | - | NO | - | FALSE | 是否已删除 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |
| updated_at | TIMESTAMPTZ | - | NO | - | NOW() | 更新时间 |

**索引设计：**
- 主键：`pk_skill_comments` (id)
- 普通索引：`idx_skill_comments_skill_id` (skill_id, created_at DESC)
- 普通索引：`idx_skill_comments_user_id` (user_id)
- 外键：`fk_skill_comments_skills` (skill_id → skills.id)
- 外键：`fk_skill_comments_users` (user_id → users.id)
- 外键：`fk_skill_comments_parent` (parent_id → skill_comments.id)

---

#### 4.2.14 skill_favorites（SKILL收藏表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 主键 |
| skill_id | UUID | - | NO | FK | - | SKILL ID |
| user_id | UUID | - | NO | FK | - | 用户ID |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 收藏时间 |

**索引设计：**
- 主键：`pk_skill_favorites` (id)
- 唯一索引：`uk_skill_favorites_skill_user` (skill_id, user_id)
- 普通索引：`idx_skill_favorites_user_id` (user_id)
- 外键：`fk_skill_favorites_skills` (skill_id → skills.id)
- 外键：`fk_skill_favorites_users` (user_id → users.id)

---

#### 4.2.15 skill_shares（SKILL分享表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 分享ID |
| skill_id | UUID | - | NO | FK | - | SKILL ID |
| share_token | VARCHAR | 64 | NO | UNI | - | 分享令牌 |
| share_type | VARCHAR | 20 | NO | - | 'link' | 分享类型: link/department |
| created_by | UUID | - | NO | FK | - | 创建者ID |
| expires_at | TIMESTAMPTZ | - | NO | - | - | 过期时间(7天) |
| access_count | INT | - | NO | - | 0 | 访问次数 |
| is_active | BOOLEAN | - | NO | - | TRUE | 是否有效 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_skill_shares` (id)
- 唯一索引：`uk_skill_shares_token` (share_token)
- 普通索引：`idx_skill_shares_skill_id` (skill_id)
- 外键：`fk_skill_shares_skills` (skill_id → skills.id)
- 外键：`fk_skill_shares_creator` (created_by → users.id)

---

#### 4.2.16 suites（套件信息表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 套件ID |
| name | VARCHAR | 200 | NO | - | - | 套件名称 |
| description | TEXT | - | YES | - | NULL | 描述 |
| version | VARCHAR | 50 | NO | - | '1.0.0' | 版本号 |
| category | VARCHAR | 100 | NO | - | - | 分类 |
| visibility | VARCHAR | 20 | NO | - | 'company' | 可见范围 |
| status | VARCHAR | 20 | NO | - | 'draft' | 状态: draft/published/archived |
| owner_id | UUID | - | NO | FK | - | 所有者ID |
| skill_count | INT | - | NO | - | 0 | 包含SKILL数量 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |
| updated_at | TIMESTAMPTZ | - | NO | - | NOW() | 更新时间 |

**索引设计：**
- 主键：`pk_suites` (id)
- 普通索引：`idx_suites_owner_id` (owner_id)
- 普通索引：`idx_suites_status` (status)
- 外键：`fk_suites_owner` (owner_id → users.id)

---

#### 4.2.17 suite_skills（套件SKILL关联表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 主键 |
| suite_id | UUID | - | NO | FK | - | 套件ID |
| skill_id | UUID | - | NO | FK | - | SKILL ID |
| sort_order | INT | - | NO | - | 0 | 排序序号(部署顺序) |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_suite_skills` (id)
- 唯一索引：`uk_suite_skills_suite_skill` (suite_id, skill_id)
- 外键：`fk_suite_skills_suites` (suite_id → suites.id ON DELETE CASCADE)
- 外键：`fk_suite_skills_skills` (skill_id → skills.id)

---

#### 4.2.18 deployments（部署记录表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 部署ID |
| skill_id | UUID | - | NO | FK | - | SKILL ID |
| version_id | UUID | - | NO | FK | - | 版本ID |
| user_id | UUID | - | NO | FK | - | 部署者ID |
| status | VARCHAR | 20 | NO | - | 'pending' | 状态: pending/deploying/running/failed/rolled_back |
| endpoint | VARCHAR | 500 | YES | - | NULL | 访问端点 |
| container_id | VARCHAR | 100 | YES | - | NULL | 容器ID |
| config_snapshot | JSONB | - | YES | - | NULL | 部署配置快照 |
| error_message | TEXT | - | YES | - | NULL | 错误信息 |
| started_at | TIMESTAMPTZ | - | YES | - | NULL | 开始时间 |
| completed_at | TIMESTAMPTZ | - | YES | - | NULL | 完成时间 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_deployments` (id)
- 普通索引：`idx_deployments_skill_id` (skill_id)
- 普通索引：`idx_deployments_user_id` (user_id)
- 普通索引：`idx_deployments_status` (status)
- 外键：`fk_deployments_skills` (skill_id → skills.id)
- 外键：`fk_deployments_versions` (version_id → skill_versions.id)
- 外键：`fk_deployments_users` (user_id → users.id)

---

#### 4.2.19 deployment_configs（部署配置表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 配置ID |
| deployment_id | UUID | - | NO | FK | - | 部署ID |
| config_key | VARCHAR | 100 | NO | - | - | 配置键 |
| config_value | TEXT | - | NO | - | - | 配置值 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_deployment_configs` (id)
- 唯一索引：`uk_deployment_configs_deploy_key` (deployment_id, config_key)
- 外键：`fk_deployment_configs_deployments` (deployment_id → deployments.id ON DELETE CASCADE)

---

#### 4.2.20 download_logs（下载记录表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 记录ID |
| skill_id | UUID | - | NO | FK | - | SKILL ID |
| version_id | UUID | - | YES | FK | - | 版本ID |
| user_id | UUID | - | NO | FK | - | 下载者ID |
| download_type | VARCHAR | 20 | NO | - | 'single' | 下载类型: single/batch |
| ip_address | VARCHAR | 50 | YES | - | NULL | 下载IP |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 下载时间 |

**索引设计：**
- 主键：`pk_download_logs` (id)
- 普通索引：`idx_download_logs_skill_id` (skill_id, created_at DESC)
- 普通索引：`idx_download_logs_user_id` (user_id, created_at DESC)
- 外键：`fk_download_logs_skills` (skill_id → skills.id)
- 外键：`fk_download_logs_users` (user_id → users.id)

---

#### 4.2.21 operation_logs（操作日志表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 日志ID |
| log_type | VARCHAR | 20 | NO | - | 'operation' | 日志类型: operation/error/security |
| action | VARCHAR | 100 | NO | - | - | 操作动作(如skill.upload) |
| user_id | UUID | - | YES | FK | - | 操作用户ID |
| user_name | VARCHAR | 100 | YES | - | NULL | 用户姓名(冗余) |
| resource_type | VARCHAR | 50 | YES | - | NULL | 资源类型 |
| resource_id | UUID | - | YES | - | NULL | 资源ID |
| resource_name | VARCHAR | 200 | YES | - | NULL | 资源名称(冗余) |
| ip_address | VARCHAR | 50 | YES | - | NULL | 操作IP |
| user_agent | VARCHAR | 500 | YES | - | NULL | User-Agent |
| result | VARCHAR | 20 | NO | - | 'success' | 操作结果: success/failure |
| detail | JSONB | - | YES | - | NULL | 详细信息 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 操作时间 |

**索引设计：**
- 主键：`pk_operation_logs` (id)
- 普通索引：`idx_operation_logs_type_created` (log_type, created_at DESC)
- 普通索引：`idx_operation_logs_user_id` (user_id, created_at DESC)
- 普通索引：`idx_operation_logs_resource` (resource_type, resource_id)

---

#### 4.2.22 notifications（通知表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 通知ID |
| user_id | UUID | - | NO | FK | - | 接收者ID |
| type | VARCHAR | 30 | NO | - | - | 通知类型: review_request/review_result/operation/system/feedback/reminder |
| title | VARCHAR | 200 | NO | - | - | 通知标题 |
| content | TEXT | - | NO | - | - | 通知内容 |
| data | JSONB | - | YES | - | NULL | 关联数据 |
| is_read | BOOLEAN | - | NO | - | FALSE | 是否已读 |
| channel | VARCHAR | 20 | NO | - | 'in_app' | 通知渠道: in_app/email |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |

**索引设计：**
- 主键：`pk_notifications` (id)
- 普通索引：`idx_notifications_user_read` (user_id, is_read, created_at DESC)
- 普通索引：`idx_notifications_type` (type)
- 外键：`fk_notifications_users` (user_id → users.id)

---

#### 4.2.23 user_notification_settings（用户通知设置表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 主键 |
| user_id | UUID | - | NO | FK | - | 用户ID |
| notification_type | VARCHAR | 30 | NO | - | - | 通知类型 |
| in_app_enabled | BOOLEAN | - | NO | - | TRUE | 是否启用站内通知 |
| email_enabled | BOOLEAN | - | NO | - | TRUE | 是否启用邮件通知 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |
| updated_at | TIMESTAMPTZ | - | NO | - | NOW() | 更新时间 |

**索引设计：**
- 主键：`pk_user_notification_settings` (id)
- 唯一索引：`uk_user_notification_settings` (user_id, notification_type)
- 外键：`fk_user_notification_settings_users` (user_id → users.id)

---

#### 4.2.24 system_configs（系统配置表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 配置ID |
| config_key | VARCHAR | 200 | NO | UNI | - | 配置键 |
| config_value | TEXT | - | NO | - | - | 配置值(JSON格式) |
| value_type | VARCHAR | 20 | NO | - | 'string' | 值类型: string/number/boolean/json |
| description | VARCHAR | 500 | YES | - | NULL | 配置说明 |
| is_sensitive | BOOLEAN | - | NO | - | FALSE | 是否敏感配置(加密存储) |
| updated_by | UUID | - | YES | FK | - | 最后更新者ID |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |
| updated_at | TIMESTAMPTZ | - | NO | - | NOW() | 更新时间 |

**索引设计：**
- 主键：`pk_system_configs` (id)
- 唯一索引：`uk_system_configs_key` (config_key)
- 外键：`fk_system_configs_updater` (updated_by → users.id)

---

#### 4.2.25 help_docs（帮助文档表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 文档ID |
| title | VARCHAR | 200 | NO | - | - | 文档标题 |
| content | TEXT | - | NO | - | - | 文档内容(Markdown) |
| doc_type | VARCHAR | 20 | NO | - | 'guide' | 类型: guide/faq/video |
| category | VARCHAR | 100 | YES | - | NULL | 分类 |
| sort_order | INT | - | NO | - | 0 | 排序序号 |
| is_published | BOOLEAN | - | NO | - | FALSE | 是否发布 |
| created_by | UUID | - | NO | FK | - | 创建者ID |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |
| updated_at | TIMESTAMPTZ | - | NO | - | NOW() | 更新时间 |

**索引设计：**
- 主键：`pk_help_docs` (id)
- 普通索引：`idx_help_docs_type_published` (doc_type, is_published)
- 外键：`fk_help_docs_creator` (created_by → users.id)

---

#### 4.2.26 feedbacks（反馈表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 反馈ID |
| user_id | UUID | - | NO | FK | - | 提交者ID |
| title | VARCHAR | 200 | NO | - | - | 反馈标题 |
| content | TEXT | - | NO | - | - | 反馈内容(最大1000字) |
| category | VARCHAR | 50 | YES | - | NULL | 反馈分类 |
| status | VARCHAR | 20 | NO | - | 'pending' | 状态: pending/processing/resolved |
| reply | TEXT | - | YES | - | NULL | 管理员回复 |
| replied_by | UUID | - | YES | FK | - | 回复者ID |
| replied_at | TIMESTAMPTZ | - | YES | - | NULL | 回复时间 |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |
| updated_at | TIMESTAMPTZ | - | NO | - | NOW() | 更新时间 |

**索引设计：**
- 主键：`pk_feedbacks` (id)
- 普通索引：`idx_feedbacks_user_id` (user_id)
- 普通索引：`idx_feedbacks_status` (status)
- 外键：`fk_feedbacks_users` (user_id → users.id)
- 外键：`fk_feedbacks_replier` (replied_by → users.id)

---

#### 4.2.27 skill_relations（SKILL关联关系表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 关联ID |
| source_skill_id | UUID | - | NO | FK | - | 源SKILL ID |
| target_skill_id | UUID | - | NO | FK | - | 目标SKILL ID |
| relation_type | VARCHAR | 30 | NO | - | - | 关联类型: prerequisite/advanced/related/composed |
| relation_label | VARCHAR | 100 | YES | - | NULL | 关联标签(如"前置技能") |
| source | VARCHAR | 20 | NO | - | 'system' | 来源: system_recommended/admin_configured |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |
| updated_at | TIMESTAMPTZ | - | NO | - | NOW() | 更新时间 |

**索引设计：**
- 主键：`pk_skill_relations` (id)
- 唯一索引：`uk_skill_relations_source_target` (source_skill_id, target_skill_id, relation_type)
- 普通索引：`idx_skill_relations_target` (target_skill_id)
- 外键：`fk_skill_relations_source` (source_skill_id → skills.id)
- 外键：`fk_skill_relations_target` (target_skill_id → skills.id)

---

#### 4.2.28 position_skills（职位SKILL映射表）

| 字段名 | 数据类型 | 长度 | 允许空 | 键类型 | 默认值 | 字段说明 |
|--------|---------|------|--------|--------|--------|---------|
| id | UUID | - | NO | PRI | gen_random_uuid() | 映射ID |
| position | VARCHAR | 100 | NO | - | - | 职位名称 |
| skill_id | UUID | - | NO | FK | - | SKILL ID |
| importance | VARCHAR | 20 | NO | - | 'required' | 重要性: required/recommended/optional |
| source | VARCHAR | 20 | NO | - | 'system' | 来源: system_recommended/admin_configured |
| created_at | TIMESTAMPTZ | - | NO | - | NOW() | 创建时间 |
| updated_at | TIMESTAMPTZ | - | NO | - | NOW() | 更新时间 |

**索引设计：**
- 主键：`pk_position_skills` (id)
- 唯一索引：`uk_position_skills_pos_skill` (position, skill_id)
- 普通索引：`idx_position_skills_skill_id` (skill_id)
- 外键：`fk_position_skills_skills` (skill_id → skills.id)

---

## 5. 物理设计

### 5.1 索引设计汇总

| 索引类型 | 数量 | 说明 |
|---------|------|------|
| 主键索引 | 28 | 每表一个UUID主键 |
| 唯一索引 | 15 | 业务唯一性约束 |
| 普通索引 | 35+ | 查询优化 |
| GIN索引 | 1 | skills.name模糊搜索 |
| 外键索引 | 25+ | 引用完整性 |

### 5.2 数据库参数配置

```sql
-- 连接配置
max_connections = 200
shared_buffers = 4GB
effective_cache_size = 12GB

-- 性能配置
work_mem = 64MB
maintenance_work_mem = 512MB
random_page_cost = 1.1

-- 日志配置
log_min_duration_statement = 1000  -- 记录超过1秒的查询
```

---

## 6. Redis缓存策略

### 6.1 缓存Key设计

| Key模式 | 数据类型 | TTL | 说明 |
|---------|---------|-----|------|
| `user:{userId}` | Hash | 5min | 用户信息缓存 |
| `user:{userId}:permissions` | Set | 5min | 用户权限缓存 |
| `skill:{skillId}` | Hash | 5min | SKILL详情缓存 |
| `skill:hot:list` | SortedSet | 10min | 热门SKILL列表(TOP100) |
| `skill:search:{hash}` | String | 5min | 搜索结果缓存 |
| `skill:category:list` | List | 30min | 分类列表缓存 |
| `config:{configKey}` | String | 30min | 系统配置缓存 |
| `token:{tokenId}` | String | 2h | JWT令牌黑名单 |
| `upload:chunk:{fileId}:{chunkNo}` | String | 1h | 上传分片临时存储 |
| `deploy:lock:{skillId}` | String | 5min | 部署分布式锁 |
| `review:lock:{skillId}` | String | 5min | 审核分布式锁 |
| `ratelimit:{userId}:{api}` | String | 1min | API限流计数器 |

### 6.2 缓存更新策略

- **写入策略**：Cache-Aside（旁路缓存），先更新DB，再删除缓存
- **读取策略**：先读缓存，miss则读DB并回填缓存
- **一致性**：通过消息队列异步通知缓存失效
- **热点数据**：使用Redis SortedSet维护热门SKILL排行

---

## 7. 全局数据结构定义

### 7.1 通用响应结构

```java
public class ApiResponse<T> {
    private String code;      // 错误码: "Success" 或具体错误码
    private String message;   // 响应消息
    private T data;           // 响应数据
}

public class PageResponse<T> {
    private int total;        // 总记录数
    private int page;         // 当前页码
    private int pageSize;     // 每页数量
    private List<T> items;    // 数据列表
}
```

### 7.2 用户上下文

```java
public class UserContext {
    private UUID userId;
    private String employeeId;
    private String name;
    private String email;
    private String department;
    private List<String> roles;
    private Set<String> permissions;
}
```

### 7.3 SKILL索引文档（Elasticsearch）

```json
{
  "mappings": {
    "properties": {
      "id": {"type": "keyword"},
      "name": {"type": "text", "analyzer": "ik_max_word"},
      "description": {"type": "text", "analyzer": "ik_smart"},
      "skill_type": {"type": "keyword"},
      "category": {"type": "keyword"},
      "tags": {"type": "keyword"},
      "owner_name": {"type": "keyword"},
      "download_count": {"type": "integer"},
      "avg_rating": {"type": "float"},
      "status": {"type": "keyword"},
      "created_at": {"type": "date"},
      "suggest": {"type": "completion"}
    }
  }
}
```

---

## 8. 配置文件定义

### 8.1 应用配置（application.yml）

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/skill_platform
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD}
    database: 0

  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 500MB

minio:
  endpoint: ${MINIO_ENDPOINT:http://localhost:9000}
  access-key: ${MINIO_ACCESS_KEY}
  secret-key: ${MINIO_SECRET_KEY}
  bucket-name: skill-files

jwt:
  secret: ${JWT_SECRET}
  expiration: 7200        # 2小时
  refresh-expiration: 604800  # 7天

elasticsearch:
  host: ${ES_HOST:localhost}
  port: ${ES_PORT:9200}
```

### 8.2 初始化数据

```sql
-- 初始化角色
INSERT INTO roles (id, name, display_name, is_system) VALUES
  (gen_random_uuid(), 'USER', '普通用户', true),
  (gen_random_uuid(), 'DEVELOPER', 'SKILL开发者', true),
  (gen_random_uuid(), 'ADMIN', '普通管理员', true),
  (gen_random_uuid(), 'SUPER_ADMIN', '超级管理员', true);

-- 初始化权限
INSERT INTO permissions (id, code, name, resource, action) VALUES
  (gen_random_uuid(), 'skill:public:read', '查看公共SKILL', 'skill:public', 'read'),
  (gen_random_uuid(), 'skill:public:write', '编辑公共SKILL', 'skill:public', 'write'),
  (gen_random_uuid(), 'skill:public:delete', '删除公共SKILL', 'skill:public', 'delete'),
  (gen_random_uuid(), 'skill:public:download', '下载公共SKILL', 'skill:public', 'download'),
  (gen_random_uuid(), 'skill:public:deploy', '部署公共SKILL', 'skill:public', 'deploy'),
  (gen_random_uuid(), 'skill:personal:read', '查看个人SKILL', 'skill:personal', 'read'),
  (gen_random_uuid(), 'skill:personal:write', '编辑个人SKILL', 'skill:personal', 'write'),
  (gen_random_uuid(), 'skill:personal:delete', '删除个人SKILL', 'skill:personal', 'delete'),
  (gen_random_uuid(), 'skill:review:approve', '审核SKILL', 'skill:review', 'approve'),
  (gen_random_uuid(), 'user:read', '查看用户', 'user', 'read'),
  (gen_random_uuid(), 'user:write', '编辑用户', 'user', 'write'),
  (gen_random_uuid(), 'user:delete', '删除用户', 'user', 'delete'),
  (gen_random_uuid(), 'system:config:read', '查看系统配置', 'system:config', 'read'),
  (gen_random_uuid(), 'system:config:write', '编辑系统配置', 'system:config', 'write'),
  (gen_random_uuid(), 'system:log:read', '查看日志', 'system:log', 'read');

-- 初始化系统配置
INSERT INTO system_configs (id, config_key, config_value, value_type, description) VALUES
  (gen_random_uuid(), 'skill.maxFileSize', '104857600', 'number', '单个SKILL文件最大大小(字节)'),
  (gen_random_uuid(), 'skill.maxBatchSize', '524288000', 'number', '批量上传总大小限制(字节)'),
  (gen_random_uuid(), 'skill.maxPersonalStorage', '1073741824', 'number', '个人存储空间限制(字节)'),
  (gen_random_uuid(), 'skill.allowedFormats', '["json","skill","zip"]', 'json', '允许的文件格式'),
  (gen_random_uuid(), 'deploy.timeout', '300', 'number', '部署超时时间(秒)'),
  (gen_random_uuid(), 'share.expiryDays', '7', 'number', '分享链接有效期(天)'),
  (gen_random_uuid(), 'user.maxFavorites', '100', 'number', '用户最大收藏数'),
  (gen_random_uuid(), 'user.maxDevices', '5', 'number', '最大同时登录设备数');
```
