# SKILL管理平台 - API接口设计文档

**文档版本：** V1.0
**创建日期：** 2026-05-17
**编写人：** AI架构师

---

## 修订历史

| 版本 | 日期 | 修改说明 | 修改人 |
|------|------|---------|--------|
| V1.0 | 2026-05-17 | 初始创建 | AI架构师 |

---

## 1. API总览与约定

### 1.1 基础信息

- **Base URL:** `/api/v1`
- **协议:** HTTPS
- **数据格式:** JSON (Content-Type: application/json)
- **字符编码:** UTF-8
- **时间格式:** ISO 8601 (`2026-05-17T10:30:00Z`)

### 1.2 认证方式

所有需要认证的接口使用JWT Bearer Token：

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 1.3 统一响应格式

**成功响应：**
```json
{
  "code": "Success",
  "message": "操作成功",
  "data": {}
}
```

**分页响应：**
```json
{
  "code": "Success",
  "message": "success",
  "data": {
    "total": 100,
    "page": 1,
    "pageSize": 20,
    "items": []
  }
}
```

**错误响应：**
```json
{
  "code": "InvalidParameter",
  "message": "参数校验失败",
  "data": null
}
```

### 1.4 HTTP状态码

| 状态码 | 含义 | 使用场景 |
|--------|------|---------|
| 200 | OK | 成功 |
| 201 | Created | 创建成功 |
| 204 | No Content | 删除成功 |
| 400 | Bad Request | 参数错误 |
| 401 | Unauthorized | 未认证 |
| 403 | Forbidden | 无权限 |
| 404 | Not Found | 资源不存在 |
| 409 | Conflict | 资源冲突 |
| 422 | Unprocessable Entity | 业务校验失败 |
| 429 | Too Many Requests | 请求过多 |
| 500 | Internal Server Error | 服务器内部错误 |

### 1.5 分页参数

所有列表接口支持以下分页参数：

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| page | int | 1 | 页码(从1开始) |
| pageSize | int | 20 | 每页数量(最大100) |
| sortBy | string | createdAt | 排序字段 |
| sortOrder | string | desc | 排序方式: asc/desc |

---

## 2. API模块详细设计

### 2.1 用户认证服务 (F001)

#### 2.1.1 用户登录

- **路径:** `POST /api/v1/auth/login`
- **认证:** 无需认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| employeeId | string | 是 | 公司工号(6-20位) |
| password | string | 是 | 密码(8-50位) |
| loginType | string | 否 | 登录类型: password/sso，默认password |

**响应参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| token | string | JWT访问令牌 |
| refreshToken | string | 刷新令牌 |
| expiresIn | int | 令牌过期时间(秒) |
| userInfo | object | 用户信息 |
| userInfo.id | string | 用户ID |
| userInfo.employeeId | string | 工号 |
| userInfo.name | string | 姓名 |
| userInfo.department | string | 部门 |
| userInfo.role | string | 角色 |

**错误码：**

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| AUTH001 | 401 | 用户名或密码错误 |
| AUTH002 | 423 | 账号已被锁定 |
| AUTH003 | 403 | 账号未审核 |
| AUTH004 | 403 | 账号已被禁用 |
| AUTH005 | 401 | SSO认证失败 |

---

#### 2.1.2 用户注册

- **路径:** `POST /api/v1/auth/register`
- **认证:** 无需认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| employeeId | string | 是 | 公司工号 |
| name | string | 是 | 姓名(2-50位) |
| department | string | 是 | 部门 |
| email | string | 是 | 公司邮箱 |
| password | string | 是 | 密码(8-50位，含字母+数字+特殊符号) |

**错误码：**

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| AUTH006 | 409 | 工号已注册 |
| AUTH007 | 400 | 邮箱格式不正确 |

---

#### 2.1.3 刷新令牌

- **路径:** `POST /api/v1/auth/refresh`
- **认证:** 无需认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| refreshToken | string | 是 | 刷新令牌 |

---

#### 2.1.4 修改密码

- **路径:** `PUT /api/v1/auth/password`
- **认证:** 需要认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| oldPassword | string | 是 | 旧密码 |
| newPassword | string | 是 | 新密码 |

---

#### 2.1.5 获取当前用户信息

- **路径:** `GET /api/v1/auth/me`
- **认证:** 需要认证

---

### 2.2 权限管理服务 (F002)

#### 2.2.1 获取用户角色

- **路径:** `GET /api/v1/users/{userId}/roles`
- **认证:** 需要认证

---

#### 2.2.2 获取用户权限

- **路径:** `GET /api/v1/users/{userId}/permissions`
- **认证:** 需要认证

---

#### 2.2.3 权限校验

- **路径:** `POST /api/v1/auth/check-permission`
- **认证:** 需要认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| resource | string | 是 | 资源标识(如skill:public:read) |
| resourceId | string | 否 | 资源ID |

**响应参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| allowed | boolean | 是否允许 |
| permissions | string[] | 用户拥有的权限列表 |

---

#### 2.2.4 角色管理（管理员）

- **路径:** `GET /api/v1/roles` - 获取角色列表
- **路径:** `POST /api/v1/roles` - 创建角色
- **路径:** `PUT /api/v1/roles/{roleId}` - 更新角色
- **路径:** `DELETE /api/v1/roles/{roleId}` - 删除角色
- **路径:** `PUT /api/v1/roles/{roleId}/permissions` - 分配权限

---

#### 2.2.5 用户角色分配（管理员）

- **路径:** `PUT /api/v1/users/{userId}/roles` - 分配用户角色

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| roleIds | string[] | 是 | 角色ID列表 |

---

### 2.3 SKILL上传服务 (F003)

#### 2.3.1 单文件上传

- **路径:** `POST /api/v1/skills/upload`
- **认证:** 需要认证
- **Content-Type:** multipart/form-data

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | SKILL文件(.json/.skill/.zip，最大100MB) |
| skillType | string | 是 | 类型: public/suite/personal |
| name | string | 是 | SKILL名称(1-100位) |
| description | string | 否 | 描述(最大500字) |
| version | string | 是 | 版本号(x.y.z) |
| category | string | 是 | 分类 |
| tags | string[] | 否 | 标签列表 |

**响应参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| skillId | string | SKILL ID |
| name | string | SKILL名称 |
| version | string | 版本号 |
| status | string | 状态(pending_validation) |
| uploadTime | string | 上传时间 |

**错误码：**

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| UPLOAD001 | 400 | 文件格式不支持 |
| UPLOAD002 | 413 | 文件大小超限 |
| UPLOAD003 | 400 | 文件名不规范 |
| UPLOAD004 | 507 | 存储空间不足 |
| UPLOAD005 | 409 | 版本号已存在 |

---

#### 2.3.2 批量上传

- **路径:** `POST /api/v1/skills/batch-upload`
- **认证:** 需要认证
- **Content-Type:** multipart/form-data

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| files | File[] | 是 | SKILL文件列表(最多10个) |
| skillType | string | 是 | 统一SKILL类型 |

---

#### 2.3.3 分片上传 - 初始化

- **路径:** `POST /api/v1/skills/upload/init`
- **认证:** 需要认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| fileName | string | 是 | 文件名 |
| fileSize | long | 是 | 文件大小 |
| chunkSize | int | 否 | 分片大小(默认5MB) |

**响应参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| uploadId | string | 上传任务ID |
| chunkTotal | int | 分片总数 |

---

#### 2.3.4 分片上传 - 上传分片

- **路径:** `POST /api/v1/skills/upload/{uploadId}/chunks/{chunkNo}`
- **认证:** 需要认证
- **Content-Type:** multipart/form-data

---

#### 2.3.5 分片上传 - 完成

- **路径:** `POST /api/v1/skills/upload/{uploadId}/complete`
- **认证:** 需要认证

---

### 2.4 SKILL查询服务 (F005/F006)

#### 2.4.1 SKILL列表查询

- **路径:** `GET /api/v1/skills`
- **认证:** 需要认证

**查询参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 否 | 搜索关键词 |
| skillType | string | 否 | 类型: public/suite/personal |
| category | string | 否 | 分类 |
| tags | string[] | 否 | 标签筛选 |
| status | string | 否 | 状态筛选 |
| page | int | 否 | 页码(默认1) |
| pageSize | int | 否 | 每页数量(默认20) |
| sortBy | string | 否 | 排序字段 |
| sortOrder | string | 否 | 排序方式 |

**响应参数（items中每项）：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | string | SKILL ID |
| name | string | 名称 |
| description | string | 描述 |
| skillType | string | 类型 |
| version | string | 当前版本 |
| category | string | 分类 |
| tags | string[] | 标签 |
| developer | string | 开发者姓名 |
| downloadCount | int | 下载次数 |
| avgRating | float | 平均评分 |
| status | string | 状态 |
| createdAt | string | 创建时间 |

---

#### 2.4.2 SKILL全文检索

- **路径:** `POST /api/v1/skills/search`
- **认证:** 需要认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 是 | 搜索关键词 |
| filters | object | 否 | 筛选条件 |
| filters.skillType | string | 否 | 类型 |
| filters.category | string | 否 | 分类 |
| filters.tags | string[] | 否 | 标签 |

**响应参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| total | int | 总数 |
| items | array | 搜索结果 |
| items[].id | string | SKILL ID |
| items[].name | string | 名称 |
| items[].highlight | string | 高亮匹配 |
| items[].score | float | 相关度评分 |
| suggestions | string[] | 搜索建议 |

---

#### 2.4.3 SKILL详情

- **路径:** `GET /api/v1/skills/{skillId}`
- **认证:** 需要认证

---

#### 2.4.4 SKILL更新

- **路径:** `PUT /api/v1/skills/{skillId}`
- **认证:** 需要认证（所有者或管理员）

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | string | 否 | 名称 |
| description | string | 否 | 描述 |
| category | string | 否 | 分类 |
| tags | string[] | 否 | 标签 |

---

#### 2.4.5 SKILL删除

- **路径:** `DELETE /api/v1/skills/{skillId}`
- **认证:** 需要认证（所有者或管理员）

---

### 2.5 SKILL下载服务 (F007)

#### 2.5.1 单文件下载

- **路径:** `GET /api/v1/skills/{skillId}/download`
- **认证:** 需要认证

**查询参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| version | string | 否 | 版本号(默认最新) |

**响应：** 文件流 (application/octet-stream)

**错误码：**

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| DOWNLOAD001 | 403 | 无权下载 |
| DOWNLOAD002 | 404 | 文件不存在 |
| DOWNLOAD003 | 404 | 版本不存在 |

---

#### 2.5.2 批量下载

- **路径:** `POST /api/v1/skills/batch-download`
- **认证:** 需要认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| skillIds | string[] | 是 | SKILL ID列表(最多20个) |

**响应：** ZIP文件流

---

### 2.6 SKILL一键部署服务 (F008)

#### 2.6.1 一键部署

- **路径:** `POST /api/v1/skills/{skillId}/deploy`
- **认证:** 需要认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| version | string | 否 | 版本号(默认最新) |
| config | object | 否 | 部署配置 |
| config.port | int | 否 | 端口(默认8080) |
| config.replicas | int | 否 | 副本数(默认1) |
| config.resources | object | 否 | 资源限制 |
| config.resources.cpu | string | 否 | CPU(默认"0.5") |
| config.resources.memory | string | 否 | 内存(默认"512Mi") |

**响应参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| deploymentId | string | 部署ID |
| skillId | string | SKILL ID |
| version | string | 版本号 |
| status | string | 状态(running) |
| endpoint | string | 访问端点 |
| deployedAt | string | 部署时间 |

**错误码：**

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| DEPLOY001 | 403 | 无权部署 |
| DEPLOY002 | 422 | 格式校验未通过 |
| DEPLOY003 | 409 | 端口被占用 |
| DEPLOY004 | 503 | 资源不足 |
| DEPLOY005 | 504 | 部署超时 |

---

#### 2.6.2 查询部署状态

- **路径:** `GET /api/v1/deployments/{deploymentId}`
- **认证:** 需要认证

---

#### 2.6.3 查询部署历史

- **路径:** `GET /api/v1/skills/{skillId}/deployments`
- **认证:** 需要认证

---

#### 2.6.4 部署回滚

- **路径:** `POST /api/v1/deployments/{deploymentId}/rollback`
- **认证:** 需要认证

---

### 2.7 版本管理服务 (F009)

#### 2.7.1 版本列表

- **路径:** `GET /api/v1/skills/{skillId}/versions`
- **认证:** 需要认证

**响应参数（items中每项）：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| version | string | 版本号 |
| tag | string | 标签(latest/beta/stable) |
| createdAt | string | 创建时间 |
| createdBy | string | 创建者 |
| changelog | string | 变更说明 |

---

#### 2.7.2 版本回滚

- **路径:** `POST /api/v1/skills/{skillId}/versions/rollback`
- **认证:** 需要认证（所有者或管理员）

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| targetVersion | string | 是 | 目标版本号 |

---

#### 2.7.3 版本标签设置

- **路径:** `PUT /api/v1/skills/{skillId}/versions/{version}/tag`
- **认证:** 需要认证（所有者或管理员）

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| tag | string | 是 | 标签: latest/beta/stable |

---

### 2.8 套件SKILL管理服务 (F010)

#### 2.8.1 创建套件

- **路径:** `POST /api/v1/suites`
- **认证:** 需要认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | string | 是 | 套件名称(1-100位) |
| description | string | 否 | 描述(最大500字) |
| skillIds | string[] | 是 | SKILL ID列表(2-50个) |
| category | string | 是 | 分类 |
| visibility | string | 是 | 可见范围: department/company |

**错误码：**

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| SUITE001 | 400 | SKILL数量不足 |
| SUITE002 | 422 | SKILL冲突 |
| SUITE003 | 403 | 无权添加SKILL |
| SUITE004 | 422 | 循环依赖 |

---

#### 2.8.2 套件列表

- **路径:** `GET /api/v1/suites`
- **认证:** 需要认证

---

#### 2.8.3 套件详情

- **路径:** `GET /api/v1/suites/{suiteId}`
- **认证:** 需要认证

---

#### 2.8.4 更新套件

- **路径:** `PUT /api/v1/suites/{suiteId}`
- **认证:** 需要认证（所有者或管理员）

---

#### 2.8.5 删除套件

- **路径:** `DELETE /api/v1/suites/{suiteId}`
- **认证:** 需要认证（所有者或管理员）

---

#### 2.8.6 套件部署

- **路径:** `POST /api/v1/suites/{suiteId}/deploy`
- **认证:** 需要认证

---

### 2.9 SKILL审核服务 (F011)

#### 2.9.1 审核列表

- **路径:** `GET /api/v1/reviews`
- **认证:** 需要认证（管理员）

**查询参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 否 | 审核状态: pending/approved/rejected |
| skillType | string | 否 | SKILL类型 |
| page | int | 否 | 页码 |

---

#### 2.9.2 审核操作

- **路径:** `POST /api/v1/skills/{skillId}/review`
- **认证:** 需要认证（管理员）

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| action | string | 是 | 审核动作: approve/reject |
| comment | string | 否 | 审核意见(拒绝时必填) |

**响应参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| skillId | string | SKILL ID |
| action | string | 审核动作 |
| reviewedBy | string | 审核人 |
| reviewedAt | string | 审核时间 |

**错误码：**

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| REVIEW001 | 403 | 无权审核 |
| REVIEW002 | 422 | SKILL状态错误 |
| REVIEW003 | 409 | 重复审核 |

---

### 2.10 SKILL评价服务 (F013)

#### 2.10.1 提交评价

- **路径:** `POST /api/v1/skills/{skillId}/ratings`
- **认证:** 需要认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| rating | int | 是 | 评分(1-5) |
| comment | string | 否 | 评论内容(最大500字) |

**错误码：**

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| RATING001 | 422 | 未下载过该SKILL |
| RATING002 | 409 | 重复评价 |
| RATING003 | 400 | 评分超出范围 |

---

#### 2.10.2 查看评价列表

- **路径:** `GET /api/v1/skills/{skillId}/ratings`
- **认证:** 需要认证

---

#### 2.10.3 修改评价

- **路径:** `PUT /api/v1/skills/{skillId}/ratings`
- **认证:** 需要认证

---

### 2.11 SKILL收藏服务 (F014)

#### 2.11.1 添加收藏

- **路径:** `POST /api/v1/favorites`
- **认证:** 需要认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| skillId | string | 是 | SKILL ID |

---

#### 2.11.2 取消收藏

- **路径:** `DELETE /api/v1/favorites/{skillId}`
- **认证:** 需要认证

---

#### 2.11.3 收藏列表

- **路径:** `GET /api/v1/favorites`
- **认证:** 需要认证

---

### 2.12 SKILL分享服务 (F015)

#### 2.12.1 生成分享链接

- **路径:** `POST /api/v1/skills/{skillId}/share`
- **认证:** 需要认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| shareType | string | 否 | 分享类型: link/department(默认link) |

**响应参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| shareToken | string | 分享令牌 |
| shareUrl | string | 分享链接 |
| expiresAt | string | 过期时间 |

---

#### 2.12.2 访问分享链接

- **路径:** `GET /api/v1/shared/{shareToken}`
- **认证:** 无需认证

---

### 2.13 SKILL图谱服务 (F016)

#### 2.13.1 查询SKILL关联

- **路径:** `GET /api/v1/skills/{skillId}/relations`
- **认证:** 需要认证

**查询参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| depth | int | 否 | 关联深度(默认2，最大3) |

**响应参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| centerSkill | object | 中心SKILL |
| centerSkill.id | string | SKILL ID |
| centerSkill.name | string | 名称 |
| relations | array | 关联关系 |
| relations[].skillId | string | 关联SKILL ID |
| relations[].skillName | string | 关联SKILL名称 |
| relations[].relationType | string | 关联类型 |
| relations[].relationLabel | string | 关联标签 |

---

#### 2.13.2 查询职位映射

- **路径:** `GET /api/v1/positions/{position}/skills`
- **认证:** 需要认证

**响应参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| position | string | 职位名称 |
| requiredSkills | array | 必需技能 |
| requiredSkills[].skillId | string | SKILL ID |
| requiredSkills[].skillName | string | 名称 |
| requiredSkills[].importance | string | 重要性 |
| requiredSkills[].source | string | 来源 |
| optionalSkills | array | 可选技能 |

---

#### 2.13.3 管理SKILL关联（管理员）

- **路径:** `POST /api/v1/skills/{skillId}/relations` - 添加关联
- **路径:** `DELETE /api/v1/skills/{skillId}/relations/{relationId}` - 删除关联

---

### 2.14 数据统计服务 (F017)

#### 2.14.1 仪表盘统计

- **路径:** `GET /api/v1/statistics/dashboard`
- **认证:** 需要认证（管理员）

**响应参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| skillTotal | int | SKILL总数 |
| skillByType | object | 按类型统计 |
| todayUploads | int | 今日上传数 |
| todayDownloads | int | 今日下载数 |
| todayDeploys | int | 今日部署数 |
| activeUsers | int | 活跃用户数 |

---

#### 2.14.2 趋势统计

- **路径:** `GET /api/v1/statistics/trends`
- **认证:** 需要认证（管理员）

**查询参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| metric | string | 是 | 指标: uploads/downloads/deploys/users |
| period | string | 否 | 周期: day/week/month(默认day) |
| startTime | string | 否 | 开始时间 |
| endTime | string | 否 | 结束时间 |

---

#### 2.14.3 热门SKILL排行

- **路径:** `GET /api/v1/statistics/hot-skills`
- **认证:** 需要认证

---

### 2.15 日志管理服务 (F018)

#### 2.15.1 日志查询

- **路径:** `GET /api/v1/logs`
- **认证:** 需要认证（管理员）

**查询参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| logType | string | 否 | 日志类型: operation/error/security |
| startTime | string | 否 | 开始时间 |
| endTime | string | 否 | 结束时间 |
| userId | string | 否 | 操作用户 |
| keyword | string | 否 | 关键词 |
| page | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量(最大100) |

**错误码：**

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| LOG001 | 400 | 时间范围过大(超过90天) |
| LOG002 | 400 | 导出数量过大 |

---

#### 2.15.2 日志导出

- **路径:** `POST /api/v1/logs/export`
- **认证:** 需要认证（管理员）

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| format | string | 是 | 导出格式: excel/csv |
| filters | object | 否 | 筛选条件(同查询接口) |

---

### 2.16 系统配置服务 (F019)

#### 2.16.1 获取配置列表

- **路径:** `GET /api/v1/configs`
- **认证:** 需要认证（管理员）

---

#### 2.16.2 获取单个配置

- **路径:** `GET /api/v1/configs/{configKey}`
- **认证:** 需要认证（管理员）

---

#### 2.16.3 更新配置

- **路径:** `PUT /api/v1/configs/{configKey}`
- **认证:** 需要认证（超级管理员）

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| configValue | any | 是 | 配置值 |

**错误码：**

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| CONFIG001 | 404 | 配置不存在 |
| CONFIG002 | 400 | 配置值无效 |
| CONFIG003 | 403 | 无权修改 |

---

### 2.17 帮助中心服务 (F020)

#### 2.17.1 帮助文档列表

- **路径:** `GET /api/v1/help-docs`
- **认证:** 无需认证

**查询参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| docType | string | 否 | 文档类型: guide/faq/video |
| keyword | string | 否 | 搜索关键词 |

---

#### 2.17.2 帮助文档详情

- **路径:** `GET /api/v1/help-docs/{docId}`
- **认证:** 无需认证

---

#### 2.17.3 帮助文档管理（管理员）

- **路径:** `POST /api/v1/help-docs` - 创建文档
- **路径:** `PUT /api/v1/help-docs/{docId}` - 更新文档
- **路径:** `DELETE /api/v1/help-docs/{docId}` - 删除文档

---

### 2.18 意见反馈服务 (F021)

#### 2.18.1 提交反馈

- **路径:** `POST /api/v1/feedbacks`
- **认证:** 需要认证

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| title | string | 是 | 反馈标题 |
| content | string | 是 | 反馈内容(最大1000字) |
| category | string | 否 | 反馈分类 |

---

#### 2.18.2 我的反馈列表

- **路径:** `GET /api/v1/feedbacks`
- **认证:** 需要认证

---

#### 2.18.3 反馈管理（管理员）

- **路径:** `GET /api/v1/admin/feedbacks` - 所有反馈列表
- **路径:** `POST /api/v1/admin/feedbacks/{feedbackId}/reply` - 回复反馈

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| reply | string | 是 | 回复内容 |

---

### 2.19 通知服务 (F012)

#### 2.19.1 通知列表

- **路径:** `GET /api/v1/notifications`
- **认证:** 需要认证

**查询参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| isRead | boolean | 否 | 是否已读 |
| type | string | 否 | 通知类型 |
| page | int | 否 | 页码 |

---

#### 2.19.2 标记已读

- **路径:** `PUT /api/v1/notifications/{notificationId}/read`
- **认证:** 需要认证

---

#### 2.19.3 全部标记已读

- **路径:** `PUT /api/v1/notifications/read-all`
- **认证:** 需要认证

---

#### 2.19.4 通知设置

- **路径:** `GET /api/v1/notifications/settings` - 获取设置
- **路径:** `PUT /api/v1/notifications/settings` - 更新设置

---

### 2.20 用户管理（管理员）

#### 2.20.1 用户列表

- **路径:** `GET /api/v1/users`
- **认证:** 需要认证（管理员）

---

#### 2.20.2 用户详情

- **路径:** `GET /api/v1/users/{userId}`
- **认证:** 需要认证（管理员）

---

#### 2.20.3 更新用户状态

- **路径:** `PUT /api/v1/users/{userId}/status`
- **认证:** 需要认证（管理员）

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | string | 是 | 状态: active/locked/disabled |

---

## 3. 全局错误码汇总

### 3.1 通用错误码

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| Success | 200 | 成功 |
| InvalidParameter | 400 | 参数错误 |
| Unauthorized | 401 | 未认证 |
| PermissionDenied | 403 | 无权限 |
| NotFound | 404 | 资源不存在 |
| Conflict | 409 | 资源冲突 |
| InternalError | 500 | 服务器内部错误 |
| ServiceUnavailable | 503 | 服务不可用 |

### 3.2 业务错误码

| 模块 | 错误码范围 | 说明 |
|------|-----------|------|
| AUTH | AUTH001-AUTH099 | 认证相关 |
| PERM | PERM001-PERM099 | 权限相关 |
| UPLOAD | UPLOAD001-UPLOAD099 | 上传相关 |
| SKILL | SKILL001-SKILL099 | SKILL相关 |
| DOWNLOAD | DOWNLOAD001-DOWNLOAD099 | 下载相关 |
| DEPLOY | DEPLOY001-DEPLOY099 | 部署相关 |
| VERSION | VERSION001-VERSION099 | 版本相关 |
| SUITE | SUITE001-SUITE099 | 套件相关 |
| REVIEW | REVIEW001-REVIEW099 | 审核相关 |
| RATING | RATING001-RATING099 | 评价相关 |
| GRAPH | GRAPH001-GRAPH099 | 图谱相关 |
| LOG | LOG001-LOG099 | 日志相关 |
| CONFIG | CONFIG001-CONFIG099 | 配置相关 |
| SEARCH | SEARCH001-SEARCH099 | 搜索相关 |

---

## 4. 认证与授权说明

### 4.1 JWT令牌结构

```json
{
  "sub": "user001",
  "employeeId": "EMP001",
  "name": "张三",
  "roles": ["USER"],
  "iat": 1715932200,
  "exp": 1715939400
}
```

### 4.2 权限模型

采用RBAC模型：
- 用户 → 角色（多对多）
- 角色 → 权限（多对多）
- 权限格式：`{resource}:{action}`，如 `skill:public:read`

### 4.3 接口权限要求

| 接口类别 | 最低角色要求 |
|---------|------------|
| 查看公共SKILL | USER |
| 上传个人SKILL | USER |
| 提交公共SKILL审核 | DEVELOPER |
| 审核SKILL | ADMIN |
| 用户管理 | ADMIN |
| 系统配置 | SUPER_ADMIN |
| 查看日志 | ADMIN |
| 数据统计 | ADMIN |
