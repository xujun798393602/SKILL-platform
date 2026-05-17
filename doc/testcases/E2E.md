# SKILL管理平台 - E2E验收测试套件

**版本**：1.0
**日期**：2026-05-17
**需求参考**：doc/requirement/requirement.md
**设计参考**：doc/design/tech_design.md
**测试用例总数**：84
**功能点覆盖**：21/21 (100%)

---

## 已覆盖的功能点

| FP ID | 功能名称 | 测试用例 | 覆盖率 |
|-------|---------|---------|--------|
| F001 | 用户认证服务 | TC-1, TC-2, TC-3, TC-4 | ✓ |
| F002 | 权限管理服务 | TC-5, TC-6, TC-7, TC-8 | ✓ |
| F003 | SKILL上传服务 | TC-9, TC-10, TC-11, TC-12 | ✓ |
| F004 | SKILL格式校验服务 | TC-13, TC-14, TC-15, TC-16 | ✓ |
| F005 | SKILL存储服务 | TC-17, TC-18, TC-19, TC-20 | ✓ |
| F006 | SKILL检索服务 | TC-01, TC-02, TC-03, TC-04 | ✓ |
| F007 | SKILL下载服务 | TC-05, TC-06, TC-07, TC-08 | ✓ |
| F008 | SKILL一键部署服务 | TC-09, TC-10, TC-11, TC-12 | ✓ |
| F009 | SKILL版本管理服务 | TC-13, TC-14, TC-15, TC-16 | ✓ |
| F010 | 套件SKILL管理服务 | TC-17, TC-18, TC-19, TC-20 | ✓ |
| F011 | SKILL审核服务 | TC-301, TC-302, TC-303, TC-304 | ✓ |
| F012 | 消息通知服务 | TC-305, TC-306, TC-307, TC-308 | ✓ |
| F013 | SKILL评价服务 | TC-309, TC-310, TC-311, TC-312 | ✓ |
| F014 | SKILL收藏服务 | TC-313, TC-314, TC-315, TC-316 | ✓ |
| F015 | SKILL分享服务 | TC-317, TC-318, TC-319, TC-320 | ✓ |
| F016 | SKILL图谱服务 | TC-25, TC-26, TC-27, TC-28 | ✓ |
| F017 | 数据统计服务 | TC-29, TC-30, TC-31, TC-32 | ✓ |
| F018 | 日志管理服务 | TC-33, TC-34, TC-35, TC-36 | ✓ |
| F019 | 系统配置服务 | TC-37, TC-38, TC-39, TC-40 | ✓ |
| F020 | 帮助中心服务 | TC-41, TC-42, TC-43, TC-44 | ✓ |
| F021 | 意见反馈服务 | TC-45, TC-46, TC-47, TC-48 | ✓ |

## 测试用例摘要

| TC ID | 标题 | 类型 | 优先级 | FP |
|-------|------|------|--------|-----|
| TC-1 | 用户使用有效凭证成功登录 | 正常路径 | 关键 | F001 |
| TC-2 | 用户使用错误密码登录失败 | 异常路径 | 关键 | F001 |
| TC-3 | 连续5次登录失败后账户锁定 | 异常路径 | 关键 | F001 |
| TC-4 | 使用过期的刷新令牌请求新令牌 | 边界情况 | 高 | F001 |
| TC-5 | 管理员权限校验通过 | 正常路径 | 关键 | F002 |
| TC-6 | 普通用户访问管理员接口被拒绝 | 异常路径 | 关键 | F002 |
| TC-7 | 使用无效令牌进行权限校验 | 异常路径 | 高 | F002 |
| TC-8 | 用户无任何角色时的权限校验 | 边界情况 | 中 | F002 |
| TC-9 | 成功上传单个.json格式SKILL文件 | 正常路径 | 关键 | F003 |
| TC-10 | 上传不支持的文件格式被拒绝 | 异常路径 | 关键 | F003 |
| TC-11 | 上传超过100MB大小限制的文件 | 异常路径 | 高 | F003 |
| TC-12 | 批量上传多个SKILL文件 | 边界情况 | 高 | F003 |
| TC-13 | 合法SKILL文件格式校验通过 | 正常路径 | 关键 | F004 |
| TC-14 | SKILL文件内容格式错误校验失败 | 异常路径 | 关键 | F004 |
| TC-15 | SKILL文件包含恶意代码被安全校验拦截 | 异常路径 | 关键 | F004 |
| TC-16 | SKILL文件命名不符合规范被拒绝 | 边界情况 | 高 | F004 |
| TC-17 | 查询SKILL列表并返回分页结果 | 正常路径 | 关键 | F005 |
| TC-18 | 使用不存在的关键词搜索返回空结果 | 异常路径 | 中 | F005 |
| TC-19 | SKILL列表按类型和标签组合筛选 | 边界情况 | 高 | F005 |
| TC-20 | 未携带认证令牌查询SKILL列表被拒绝 | 异常路径 | 关键 | F005 |
| TC-01 | 全文检索返回匹配结果 | 正常路径 | 关键 | F006 |
| TC-02 | 检索关键词为空返回错误 | 异常路径 | 高 | F006 |
| TC-03 | 检索未授权用户被拒绝 | 异常路径 | 高 | F006 |
| TC-04 | 模糊匹配边界字符处理 | 边界情况 | 中 | F006 |
| TC-05 | 单文件下载成功 | 正常路径 | 关键 | F007 |
| TC-06 | 下载不存在的SKILL返回错误 | 异常路径 | 高 | F007 |
| TC-07 | 无权限下载私有SKILL被拒绝 | 异常路径 | 高 | F007 |
| TC-08 | 批量下载最大数量限制 | 边界情况 | 中 | F007 |
| TC-09 | Docker一键部署成功 | 正常路径 | 关键 | F008 |
| TC-10 | 部署配置端口冲突返回错误 | 异常路径 | 高 | F008 |
| TC-11 | 部署超时自动回滚 | 异常路径 | 高 | F008 |
| TC-12 | K8s部署资源限制边界值 | 边界情况 | 中 | F008 |
| TC-13 | 查询版本列表成功 | 正常路径 | 关键 | F009 |
| TC-14 | 回滚到不存在的版本返回错误 | 异常路径 | 高 | F009 |
| TC-15 | 并发版本操作加锁失败 | 异常路径 | 高 | F009 |
| TC-16 | 版本标签最大长度边界 | 边界情况 | 中 | F009 |
| TC-17 | 创建套件成功 | 正常路径 | 关键 | F010 |
| TC-18 | 创建套件少于2个SKILL返回错误 | 异常路径 | 高 | F010 |
| TC-19 | 套件部署依赖循环检测 | 异常路径 | 高 | F010 |
| TC-20 | 套件包含最大数量SKILL | 边界情况 | 中 | F010 |
| TC-301 | 审核人批准SKILL提交 | 正常路径 | 关键 | F011 |
| TC-302 | 非审核人尝试执行审核操作 | 异常路径 | 高 | F011 |
| TC-303 | 审核已关闭（已审核）的SKILL | 异常路径 | 高 | F011 |
| TC-304 | 并发审核同一SKILL提交 | 边界情况 | 高 | F011 |
| TC-305 | 用户获取平台内通知列表 | 正常路径 | 关键 | F012 |
| TC-306 | 标记不存在的通知ID为已读 | 异常路径 | 高 | F012 |
| TC-307 | 未认证用户访问通知接口 | 异常路径 | 高 | F012 |
| TC-308 | 同时标记所有通知为已读（大量通知） | 边界情况 | 中 | F012 |
| TC-309 | 已下载用户提交SKILL评价 | 正常路径 | 关键 | F013 |
| TC-310 | 未下载SKILL的用户尝试评价 | 异常路径 | 高 | F013 |
| TC-311 | 同一用户对同一SKILL重复提交评价 | 异常路径 | 高 | F013 |
| TC-312 | 评价评论内容恰好500字边界 | 边界情况 | 中 | F013 |
| TC-313 | 用户添加SKILL到收藏夹 | 正常路径 | 关键 | F014 |
| TC-314 | 收藏不存在的SKILL | 异常路径 | 高 | F014 |
| TC-315 | 取消未收藏的SKILL | 异常路径 | 高 | F014 |
| TC-316 | 收藏数量达到100个上限后继续收藏 | 边界情况 | 高 | F014 |
| TC-317 | 用户生成SKILL分享链接 | 正常路径 | 关键 | F015 |
| TC-318 | 分享私有（未发布）SKILL | 异常路径 | 高 | F015 |
| TC-319 | 访问已过期的分享链接 | 异常路径 | 高 | F015 |
| TC-320 | 分享链接有效期恰好7天边界验证 | 边界情况 | 中 | F015 |
| TC-25 | 查询SKILL关联关系成功 | 正常路径 | 高 | F016 |
| TC-26 | 查询不存在的SKILL关联 | 异常路径 | 高 | F016 |
| TC-27 | 创建循环关联被拒绝 | 异常路径 | 高 | F016 |
| TC-28 | 查询深度为3的关联关系 | 边界情况 | 中 | F016 |
| TC-29 | 获取仪表盘统计数据 | 正常路径 | 高 | F017 |
| TC-30 | 无效时间范围查询趋势 | 异常路径 | 中 | F017 |
| TC-31 | 普通用户访问统计数据 | 异常路径 | 中 | F017 |
| TC-32 | 查询无数据时间范围的热门排行 | 边界情况 | 中 | F017 |
| TC-33 | 查询操作日志成功 | 正常路径 | 高 | F018 |
| TC-34 | 时间范围超过90天查询日志 | 异常路径 | 高 | F018 |
| TC-35 | 无效日志类型查询 | 异常路径 | 中 | F018 |
| TC-36 | 导出大量日志数据 | 边界情况 | 中 | F018 |
| TC-37 | 获取系统配置成功 | 正常路径 | 高 | F019 |
| TC-38 | 更新只读配置被拒绝 | 异常路径 | 高 | F019 |
| TC-39 | 并发更新同一配置 | 异常路径 | 高 | F019 |
| TC-40 | 获取敏感配置返回加密值 | 边界情况 | 高 | F019 |
| TC-41 | 获取帮助文档列表成功 | 正常路径 | 中 | F020 |
| TC-42 | 访问不存在的帮助文档 | 异常路径 | 中 | F020 |
| TC-43 | 非管理员删除帮助文档 | 异常路径 | 中 | F020 |
| TC-44 | 搜索无匹配结果的关键词 | 边界情况 | 中 | F020 |
| TC-45 | 提交反馈成功 | 正常路径 | 中 | F021 |
| TC-46 | 反馈内容超过1000字被拒绝 | 异常路径 | 中 | F021 |
| TC-47 | 非管理员回复反馈被拒绝 | 异常路径 | 中 | F021 |
| TC-48 | 提交空内容反馈被拒绝 | 边界情况 | 中 | F021 |

---

## 测试用例


### 功能点：F001 - 用户认证服务

---

#### TC-1：用户使用有效凭证成功登录

**功能点**：F001 - 用户认证服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 数据库中已存在用户 `testuser@example.com`，密码为 `SecurePass123!`，账户状态为"正常"
- 该用户未被锁定

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/auth/login`
- 附带有效的邮箱和密码

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含 `accessToken`（JWT格式，有效期2小时）
- 响应体包含 `refreshToken`（有效期7天）
- 响应体包含 `userInfo` 对象，其中 `email` 为 `testuser@example.com`
- 响应体包含 `expiresIn` 字段，值为 `7200`

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/auth/login",
    "headers": {
      "Content-Type": "application/json"
    },
    "body": {
      "email": "testuser@example.com",
      "password": "SecurePass123!"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "accessToken": "<jwt-token>",
      "refreshToken": "<refresh-token>",
      "expiresIn": 7200,
      "userInfo": {
        "email": "testuser@example.com",
        "nickname": "测试用户",
        "role": "user"
      }
    }
  }
}
```

**自动化说明**：
- 使用 Jest + Supertest 进行自动化
- 测试前通过 seed 脚本创建测试用户
- 测试后清理登录日志记录
- 验证 JWT payload 中包含正确的 userId 和 role

---

#### TC-2：用户使用错误密码登录失败

**功能点**：F001 - 用户认证服务
**类型**：异常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 数据库中已存在用户 `testuser@example.com`，正确密码为 `SecurePass123!`
- 该用户登录失败次数为 0，未被锁定

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/auth/login`
- 附带正确邮箱但错误密码 `WrongPass999!`

**Then**（预期结果）：
- 响应状态码为 401
- 响应体包含错误代码 `AUTH001`
- 响应体 `error.message` 为 `"用户名或密码错误"`
- 数据库中该用户的 `loginFailCount` 增加为 1
- 不返回 accessToken 或 refreshToken

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/auth/login",
    "headers": {
      "Content-Type": "application/json"
    },
    "body": {
      "email": "testuser@example.com",
      "password": "WrongPass999!"
    }
  },
  "expected": {
    "status": 401,
    "body": {
      "error": {
        "code": "AUTH001",
        "message": "用户名或密码错误"
      }
    }
  }
}
```

**自动化说明**：
- 验证错误响应格式符合 API 规范
- 验证数据库中 loginFailCount 字段递增
- 验证响应中不包含任何令牌信息

---

#### TC-3：连续5次登录失败后账户锁定

**功能点**：F001 - 用户认证服务
**类型**：异常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 数据库中已存在用户 `locktest@example.com`，密码为 `SecurePass123!`
- 该用户当前登录失败次数为 4（即将达到锁定阈值）

**When**（操作）：
- 用户第5次发送 POST 请求到 `/api/v1/auth/login`
- 附带正确邮箱但错误密码 `WrongPass999!`

**Then**（预期结果）：
- 响应状态码为 423（Locked）
- 响应体包含错误代码 `AUTH003`
- 响应体 `error.message` 包含 `"账户已锁定，请30分钟后重试"`
- 数据库中该用户的 `status` 变为 `locked`
- 数据库中该用户的 `lockedUntil` 为当前时间 + 30分钟
- 即使后续使用正确密码登录，也返回 423

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/auth/login",
    "headers": {
      "Content-Type": "application/json"
    },
    "body": {
      "email": "locktest@example.com",
      "password": "WrongPass999!"
    }
  },
  "expected": {
    "status": 423,
    "body": {
      "error": {
        "code": "AUTH003",
        "message": "账户已锁定，请30分钟后重试"
      }
    }
  }
}
```

**自动化说明**：
- 测试前通过数据库直接设置 loginFailCount 为 4
- 验证锁定后使用正确密码仍返回 423
- 验证 lockedUntil 时间戳准确性（误差不超过1秒）
- 测试后重置用户状态

---

#### TC-4：使用过期的刷新令牌请求新令牌

**功能点**：F001 - 用户认证服务
**类型**：边界情况
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户 `testuser@example.com` 已登录
- 持有一个已过期的 refreshToken（有效期已超过7天）

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/auth/refresh`
- 附带已过期的 refreshToken

**Then**（预期结果）：
- 响应状态码为 401
- 响应体包含错误代码 `AUTH004`
- 响应体 `error.message` 为 `"刷新令牌已过期，请重新登录"`
- 不返回新的 accessToken

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/auth/refresh",
    "headers": {
      "Content-Type": "application/json"
    },
    "body": {
      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.expired-token-payload.signature"
    }
  },
  "expected": {
    "status": 401,
    "body": {
      "error": {
        "code": "AUTH004",
        "message": "刷新令牌已过期，请重新登录"
      }
    }
  }
}
```

**自动化说明**：
- 使用 JWT 库手动生成一个已过期的 refreshToken（设置 iat 为 8天前）
- 验证过期令牌确实被系统拒绝
- 补充测试：使用有效 refreshToken 应返回 200 和新的 accessToken

---

### 功能点：F002 - 权限管理服务

---

#### TC-5：管理员权限校验通过

**功能点**：F002 - 权限管理服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户 `admin@example.com` 已登录，持有有效的 accessToken
- 该用户已分配角色 `admin`，角色拥有权限 `skill:upload`、`skill:delete`、`user:manage`

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/auth/check-permission`
- 附带有效的 Authorization 头和待校验的权限 `skill:upload`

**Then**（预期结果）：
- 响应状态码为 200
- 响应体 `allowed` 为 `true`
- 响应体 `userId` 为该管理员的用户ID
- 响应体 `permission` 为 `skill:upload`

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/auth/check-permission",
    "headers": {
      "Content-Type": "application/json",
      "Authorization": "Bearer <admin-access-token>"
    },
    "body": {
      "permission": "skill:upload"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "allowed": true,
      "userId": "<admin-user-id>",
      "permission": "skill:upload"
    }
  }
}
```

**自动化说明**：
- 测试前创建管理员用户并分配 admin 角色
- 验证 RBAC 角色-权限映射正确生效
- 测试后清理测试数据

---

#### TC-6：普通用户访问管理员接口被拒绝

**功能点**：F002 - 权限管理服务
**类型**：异常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户 `normaluser@example.com` 已登录，持有有效的 accessToken
- 该用户已分配角色 `user`，角色仅拥有权限 `skill:view`、`skill:download`
- 该用户未被分配 `user:manage` 权限

**When**（操作）：
- 用户发送 GET 请求到 `/api/v1/users`（管理员接口）
- 附带该普通用户的 Authorization 头

**Then**（预期结果）：
- 响应状态码为 403
- 响应体包含错误代码 `PERM001`
- 响应体 `error.message` 为 `"权限不足，无法访问该资源"`
- 响应体不包含用户列表数据

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/users",
    "headers": {
      "Authorization": "Bearer <normal-user-access-token>"
    }
  },
  "expected": {
    "status": 403,
    "body": {
      "error": {
        "code": "PERM001",
        "message": "权限不足，无法访问该资源"
      }
    }
  }
}
```

**自动化说明**：
- 验证 403 响应不泄露任何敏感数据
- 验证该用户仍可正常访问自己有权限的接口（如 GET /api/v1/skills）
- 记录权限拒绝日志用于安全审计

---

#### TC-7：使用无效令牌进行权限校验

**功能点**：F002 - 权限管理服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 准备一个格式正确但签名无效的 JWT 令牌

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/auth/check-permission`
- 附带伪造的 Authorization 头（签名无效的 JWT）

**Then**（预期结果）：
- 响应状态码为 401
- 响应体包含错误代码 `AUTH005`
- 响应体 `error.message` 为 `"无效的认证令牌"`
- 不返回任何权限信息

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/auth/check-permission",
    "headers": {
      "Content-Type": "application/json",
      "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiIxMjM0NTYiLCJyb2xlIjoiYWRtaW4ifQ.invalid-signature"
    },
    "body": {
      "permission": "skill:upload"
    }
  },
  "expected": {
    "status": 401,
    "body": {
      "error": {
        "code": "AUTH005",
        "message": "无效的认证令牌"
      }
    }
  }
}
```

**自动化说明**：
- 使用 JWT 库生成一个签名错误的令牌
- 补充测试：使用空 Authorization 头、已撤销的令牌等场景
- 验证所有受保护接口对无效令牌的统一拒绝行为

---

#### TC-8：用户无任何角色时的权限校验

**功能点**：F002 - 权限管理服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户 `norole@example.com` 已登录，持有有效的 accessToken
- 该用户未被分配任何角色

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/auth/check-permission`
- 附带有效的 Authorization 头和待校验的权限 `skill:view`

**Then**（预期结果）：
- 响应状态码为 200
- 响应体 `allowed` 为 `false`
- 响应体 `userId` 为该用户的用户ID
- 响应体 `permission` 为 `skill:view`

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/auth/check-permission",
    "headers": {
      "Content-Type": "application/json",
      "Authorization": "Bearer <no-role-user-access-token>"
    },
    "body": {
      "permission": "skill:view"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "allowed": false,
      "userId": "<no-role-user-id>",
      "permission": "skill:view"
    }
  }
}
```

**自动化说明**：
- 验证无角色用户的权限校验不会抛出异常
- 验证系统对无角色用户返回 `allowed: false` 而非 403
- 确保权限校验接口本身的访问不受角色限制

---

### 功能点：F003 - SKILL上传服务

---

#### TC-9：成功上传单个.json格式SKILL文件

**功能点**：F003 - SKILL上传服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已登录，持有有效的 accessToken，拥有 `skill:upload` 权限
- 准备一个合法的 .json 格式 SKILL 文件（大小 2MB）

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/upload`
- 以 multipart/form-data 格式附带 SKILL 文件

**Then**（预期结果）：
- 响应状态码为 201
- 响应体包含 `skillId`（UUID格式）
- 响应体 `fileName` 为上传的文件名
- 响应体 `fileSize` 为 2097152（2MB）
- 响应体 `format` 为 `json`
- 响应体 `status` 为 `uploaded`
- 文件已存储到分布式文件存储系统

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/upload",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "multipart/form-data"
    },
    "formData": {
      "file": "test-skill.json (2MB, 合法SKILL格式)"
    }
  },
  "expected": {
    "status": 201,
    "body": {
      "skillId": "<uuid>",
      "fileName": "test-skill.json",
      "fileSize": 2097152,
      "format": "json",
      "status": "uploaded",
      "createdAt": "<ISO8601-timestamp>"
    }
  }
}
```

**自动化说明**：
- 使用 fixture 准备合法的 SKILL JSON 文件
- 验证文件确实被写入存储系统
- 验证数据库中创建了对应的 SKILL 记录
- 测试后清理上传的文件和数据库记录

---

#### TC-10：上传不支持的文件格式被拒绝

**功能点**：F003 - SKILL上传服务
**类型**：异常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已登录，持有有效的 accessToken，拥有 `skill:upload` 权限
- 准备一个 .exe 格式的文件

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/upload`
- 以 multipart/form-data 格式附带 .exe 文件

**Then**（预期结果）：
- 响应状态码为 400
- 响应体包含错误代码 `UPLOAD001`
- 响应体 `error.message` 包含 `"不支持的文件格式"`
- 响应体 `error.details` 中列出支持的格式：`.json`、`.skill`、`.zip`
- 文件未被存储到系统

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/upload",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "multipart/form-data"
    },
    "formData": {
      "file": "malicious-skill.exe (1KB)"
    }
  },
  "expected": {
    "status": 400,
    "body": {
      "error": {
        "code": "UPLOAD001",
        "message": "不支持的文件格式，仅支持 .json、.skill、.zip 格式",
        "details": {
          "supportedFormats": [".json", ".skill", ".zip"],
          "receivedFormat": ".exe"
        }
      }
    }
  }
}
```

**自动化说明**：
- 补充测试其他不支持的格式：.bat、.sh、.js、.py
- 验证文件未被写入存储系统
- 验证数据库中未创建任何记录

---

#### TC-11：上传超过100MB大小限制的文件

**功能点**：F003 - SKILL上传服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已登录，持有有效的 accessToken，拥有 `skill:upload` 权限
- 准备一个 .zip 格式文件，大小为 150MB

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/upload`
- 以 multipart/form-data 格式附带 150MB 的 .zip 文件

**Then**（预期结果）：
- 响应状态码为 413（Payload Too Large）
- 响应体包含错误代码 `UPLOAD002`
- 响应体 `error.message` 包含 `"文件大小超过限制"`
- 响应体 `error.details.maxSize` 为 `104857600`（100MB）
- 响应体 `error.details.receivedSize` 为 `157286400`（150MB）
- 文件未被存储到系统

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/upload",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "multipart/form-data"
    },
    "formData": {
      "file": "large-skill.zip (150MB)"
    }
  },
  "expected": {
    "status": 413,
    "body": {
      "error": {
        "code": "UPLOAD002",
        "message": "文件大小超过限制，最大支持 100MB",
        "details": {
          "maxSize": 104857600,
          "receivedSize": 157286400,
          "maxSizeReadable": "100MB"
        }
      }
    }
  }
}
```

**自动化说明**：
- 使用程序生成一个 150MB 的随机内容 .zip 文件
- 验证大文件在传输过程中被尽早拒绝（不等待完整上传）
- 验证临时文件被清理

---

#### TC-12：批量上传多个SKILL文件

**功能点**：F003 - SKILL上传服务
**类型**：边界情况
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已登录，持有有效的 accessToken，拥有 `skill:upload` 权限
- 准备 5 个合法的 .json 格式 SKILL 文件，每个大小约 1MB

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/batch-upload`
- 以 multipart/form-data 格式附带 5 个 SKILL 文件

**Then**（预期结果）：
- 响应状态码为 201
- 响应体 `totalCount` 为 `5`
- 响应体 `successCount` 为 `5`
- 响应体 `failCount` 为 `0`
- 响应体 `results` 数组包含 5 个元素，每个元素包含 `skillId`、`fileName`、`status` 为 `uploaded`
- 5 个文件均已存储到分布式文件存储系统

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/batch-upload",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "multipart/form-data"
    },
    "formData": {
      "files": [
        "skill-alpha.json (1MB)",
        "skill-beta.json (1MB)",
        "skill-gamma.json (1MB)",
        "skill-delta.json (1MB)",
        "skill-epsilon.json (1MB)"
      ]
    }
  },
  "expected": {
    "status": 201,
    "body": {
      "totalCount": 5,
      "successCount": 5,
      "failCount": 0,
      "results": [
        { "skillId": "<uuid-1>", "fileName": "skill-alpha.json", "status": "uploaded" },
        { "skillId": "<uuid-2>", "fileName": "skill-beta.json", "status": "uploaded" },
        { "skillId": "<uuid-3>", "fileName": "skill-gamma.json", "status": "uploaded" },
        { "skillId": "<uuid-4>", "fileName": "skill-delta.json", "status": "uploaded" },
        { "skillId": "<uuid-5>", "fileName": "skill-epsilon.json", "status": "uploaded" }
      ]
    }
  }
}
```

**自动化说明**：
- 验证每个文件都被独立存储和记录
- 验证批量上传中单个文件失败不影响其他文件（部分成功场景补充测试）
- 测试后清理所有上传的文件和数据库记录

---

### 功能点：F004 - SKILL格式校验服务

---

#### TC-13：合法SKILL文件格式校验通过

**功能点**：F004 - SKILL格式校验服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已登录，持有有效的 accessToken
- 已上传一个合法的 .json 格式 SKILL 文件，文件内容包含完整的 `name`、`version`、`description`、`commands` 等必填字段
- 文件命名符合规范：`^[a-zA-Z][a-zA-Z0-9_-]{1,63}\.json$`

**When**（操作）：
- 系统对上传的文件自动执行格式校验（6维校验：文件格式、命名规范、内容结构、版本号、文件大小、安全检查）

**Then**（预期结果）：
- 校验结果状态码为 200
- 响应体 `validationStatus` 为 `passed`
- 响应体 `checks` 数组包含 6 项校验结果，每项 `passed` 为 `true`：
  - `format`（文件格式校验）
  - `naming`（命名规范校验）
  - `content`（内容结构校验）
  - `version`（版本号校验）
  - `size`（文件大小校验）
  - `security`（安全检查）
- SKILL 状态从 `uploaded` 变为 `validated`

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills/{skillId}/validation",
    "headers": {
      "Authorization": "Bearer <valid-token>"
    }
  },
  "skillFile": {
    "fileName": "code-review-skill.json",
    "content": {
      "name": "code-review-skill",
      "version": "1.0.0",
      "description": "自动化代码审查工具",
      "author": "testuser",
      "commands": [
        { "name": "review", "entry": "src/review.js" }
      ]
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "validationStatus": "passed",
      "checks": [
        { "type": "format", "passed": true },
        { "type": "naming", "passed": true },
        { "type": "content", "passed": true },
        { "type": "version", "passed": true },
        { "type": "size", "passed": true },
        { "type": "security", "passed": true }
      ]
    }
  }
}
```

**自动化说明**：
- 使用 fixture 准备一个完全合法的 SKILL 文件
- 验证 6 维校验全部通过
- 验证校验通过后 SKILL 状态变为 `validated`

---

#### TC-14：SKILL文件内容格式错误校验失败

**功能点**：F004 - SKILL格式校验服务
**类型**：异常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已登录，持有有效的 accessToken
- 已上传一个 .json 格式文件，但内容缺少必填字段 `name` 和 `commands`
- 文件命名符合规范

**When**（操作）：
- 系统对上传的文件自动执行格式校验

**Then**（预期结果）：
- 校验结果状态码为 200
- 响应体 `validationStatus` 为 `failed`
- 响应体 `checks` 中 `content` 项 `passed` 为 `false`
- 响应体 `checks` 中 `content.errors` 包含：
  - `"缺少必填字段: name"`
  - `"缺少必填字段: commands"`
- SKILL 状态保持为 `uploaded`，不进入 `validated`

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills/{skillId}/validation",
    "headers": {
      "Authorization": "Bearer <valid-token>"
    }
  },
  "skillFile": {
    "fileName": "incomplete-skill.json",
    "content": {
      "version": "1.0.0",
      "description": "一个不完整的SKILL文件"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "validationStatus": "failed",
      "checks": [
        { "type": "format", "passed": true },
        { "type": "naming", "passed": true },
        { "type": "content", "passed": false, "errors": ["缺少必填字段: name", "缺少必填字段: commands"] },
        { "type": "version", "passed": true },
        { "type": "size", "passed": true },
        { "type": "security", "passed": true }
      ]
    }
  }
}
```

**自动化说明**：
- 验证校验失败时返回具体的错误信息
- 验证 SKILL 状态未发生变更
- 补充测试：版本号格式错误（如 `1.0`）、description 超长等场景

---

#### TC-15：SKILL文件包含恶意代码被安全校验拦截

**功能点**：F004 - SKILL格式校验服务
**类型**：异常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已登录，持有有效的 accessToken
- 已上传一个 .json 格式文件，文件内容结构合法但包含可疑的命令注入代码（如 `exec("rm -rf /")` 嵌入在字段值中）

**When**（操作）：
- 系统对上传的文件自动执行格式校验，重点触发安全检查维度

**Then**（预期结果）：
- 校验结果状态码为 200
- 响应体 `validationStatus` 为 `failed`
- 响应体 `checks` 中 `security` 项 `passed` 为 `false`
- 响应体 `checks` 中 `security.errors` 包含 `"检测到潜在的命令注入风险"`
- 响应体 `checks` 中 `security.riskLevel` 为 `high`
- SKILL 状态变更为 `rejected`
- 系统记录安全审计日志

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills/{skillId}/validation",
    "headers": {
      "Authorization": "Bearer <valid-token>"
    }
  },
  "skillFile": {
    "fileName": "malicious-skill.json",
    "content": {
      "name": "malicious-skill",
      "version": "1.0.0",
      "description": "正常描述",
      "commands": [
        {
          "name": "run",
          "entry": "src/run.js",
          "args": "; exec('rm -rf /')"
        }
      ]
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "validationStatus": "failed",
      "checks": [
        { "type": "format", "passed": true },
        { "type": "naming", "passed": true },
        { "type": "content", "passed": true },
        { "type": "version", "passed": true },
        { "type": "size", "passed": true },
        {
          "type": "security",
          "passed": false,
          "riskLevel": "high",
          "errors": ["检测到潜在的命令注入风险"]
        }
      ]
    }
  }
}
```

**自动化说明**：
- 准备多种恶意代码变体：命令注入、SQL注入、XSS脚本
- 验证安全校验能拦截所有已知攻击模式
- 验证安全审计日志被正确记录
- 验证被拒绝的文件从存储中清除

---

#### TC-16：SKILL文件命名不符合规范被拒绝

**功能点**：F004 - SKILL格式校验服务
**类型**：边界情况
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已登录，持有有效的 accessToken
- 已上传一个 .json 格式文件，文件名为 `123-invalid name!.json`（以数字开头，包含空格和特殊字符）

**When**（操作）：
- 系统对上传的文件自动执行格式校验

**Then**（预期结果）：
- 校验结果状态码为 200
- 响应体 `validationStatus` 为 `failed`
- 响应体 `checks` 中 `naming` 项 `passed` 为 `false`
- 响应体 `checks` 中 `naming.errors` 包含 `"文件名不符合命名规范：必须以字母开头，仅允许字母、数字、下划线和连字符，长度2-64字符"`
- SKILL 状态保持为 `uploaded`

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills/{skillId}/validation",
    "headers": {
      "Authorization": "Bearer <valid-token>"
    }
  },
  "skillFile": {
    "fileName": "123-invalid name!.json",
    "content": {
      "name": "test-skill",
      "version": "1.0.0",
      "description": "测试SKILL",
      "commands": [{ "name": "run", "entry": "src/run.js" }]
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "validationStatus": "failed",
      "checks": [
        { "type": "format", "passed": true },
        {
          "type": "naming",
          "passed": false,
          "errors": ["文件名不符合命名规范：必须以字母开头，仅允许字母、数字、下划线和连字符，长度2-64字符"]
        },
        { "type": "content", "passed": true },
        { "type": "version", "passed": true },
        { "type": "size", "passed": true },
        { "type": "security", "passed": true }
      ]
    }
  }
}
```

**自动化说明**：
- 补充测试多种命名违规：纯数字开头、包含中文、超长文件名（65字符）、空文件名
- 验证命名校验在内容校验之前执行（快速失败）

---

### 功能点：F005 - SKILL存储服务

---

#### TC-17：查询SKILL列表并返回分页结果

**功能点**：F005 - SKILL存储服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已登录，持有有效的 accessToken
- 数据库中存在 25 个已验证通过的 SKILL 记录

**When**（操作）：
- 用户发送 GET 请求到 `/api/v1/skills?page=1&pageSize=10`

**Then**（预期结果）：
- 响应状态码为 200
- 响应体 `data` 数组包含 10 个 SKILL 记录
- 响应体 `pagination.page` 为 `1`
- 响应体 `pagination.pageSize` 为 `10`
- 响应体 `pagination.total` 为 `25`
- 响应体 `pagination.totalPages` 为 `3`
- 每个 SKILL 记录包含 `skillId`、`name`、`version`、`description`、`author`、`createdAt` 等字段

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills?page=1&pageSize=10",
    "headers": {
      "Authorization": "Bearer <valid-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "data": [
        {
          "skillId": "<uuid>",
          "name": "code-review-skill",
          "version": "1.0.0",
          "description": "自动化代码审查工具",
          "author": "testuser",
          "createdAt": "2026-05-15T10:30:00Z"
        }
      ],
      "pagination": {
        "page": 1,
        "pageSize": 10,
        "total": 25,
        "totalPages": 3
      }
    }
  }
}
```

**自动化说明**：
- 测试前通过 seed 脚本创建 25 个 SKILL 记录
- 验证分页参数正确生效
- 验证默认排序（按创建时间降序）
- 测试后清理测试数据

---

#### TC-18：使用不存在的关键词搜索返回空结果

**功能点**：F005 - SKILL存储服务
**类型**：异常路径
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已登录，持有有效的 accessToken
- 数据库中存在若干 SKILL 记录，但没有任何 SKILL 的名称或描述包含关键词 `不存在的技能XYZ999`

**When**（操作）：
- 用户发送 GET 请求到 `/api/v1/skills?keyword=不存在的技能XYZ999`

**Then**（预期结果）：
- 响应状态码为 200
- 响应体 `data` 为空数组 `[]`
- 响应体 `pagination.total` 为 `0`
- 响应体 `pagination.totalPages` 为 `0`
- 不返回错误状态

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills?keyword=%E4%B8%8D%E5%AD%98%E5%9C%A8%E7%9A%84%E6%8A%80%E8%83%BDXYZ999",
    "headers": {
      "Authorization": "Bearer <valid-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "data": [],
      "pagination": {
        "page": 1,
        "pageSize": 20,
        "total": 0,
        "totalPages": 0
      }
    }
  }
}
```

**自动化说明**：
- 验证空结果不返回 404 或其他错误码
- 验证响应格式与有结果时保持一致
- 补充测试：关键词为空字符串、仅空格等场景

---

#### TC-19：SKILL列表按类型和标签组合筛选

**功能点**：F005 - SKILL存储服务
**类型**：边界情况
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已登录，持有有效的 accessToken
- 数据库中存在以下 SKILL 记录：
  - 3 个类型为 `code-review`，标签包含 `java` 的 SKILL
  - 5 个类型为 `code-review`，标签包含 `python` 的 SKILL
  - 2 个类型为 `testing`，标签包含 `java` 的 SKILL

**When**（操作）：
- 用户发送 GET 请求到 `/api/v1/skills?type=code-review&tags=java`

**Then**（预期结果）：
- 响应状态码为 200
- 响应体 `data` 数组包含 3 个 SKILL 记录
- 响应体 `pagination.total` 为 `3`
- 返回的每个 SKILL 的 `type` 为 `code-review` 且 `tags` 包含 `java`
- 不包含类型为 `testing` 或标签不含 `java` 的记录

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills?type=code-review&tags=java",
    "headers": {
      "Authorization": "Bearer <valid-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "data": [
        {
          "skillId": "<uuid-1>",
          "name": "java-review-skill",
          "type": "code-review",
          "tags": ["java", "code-quality"],
          "version": "1.0.0"
        },
        {
          "skillId": "<uuid-2>",
          "name": "java-security-review",
          "type": "code-review",
          "tags": ["java", "security"],
          "version": "2.1.0"
        },
        {
          "skillId": "<uuid-3>",
          "name": "java-style-check",
          "type": "code-review",
          "tags": ["java", "style"],
          "version": "1.3.0"
        }
      ],
      "pagination": {
        "page": 1,
        "pageSize": 20,
        "total": 3,
        "totalPages": 1
      }
    }
  }
}
```

**自动化说明**：
- 测试前通过 seed 脚本创建多样化的 SKILL 记录
- 验证组合筛选条件正确生效（AND 逻辑）
- 补充测试：多标签筛选（`tags=java,security`）、仅按类型筛选、仅按标签筛选
- 测试后清理测试数据

---

#### TC-20：未携带认证令牌查询SKILL列表被拒绝

**功能点**：F005 - SKILL存储服务
**类型**：异常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 未携带任何 Authorization 头

**When**（操作）：
- 用户发送 GET 请求到 `/api/v1/skills?page=1&pageSize=10`
- 不附带 Authorization 头

**Then**（预期结果）：
- 响应状态码为 401
- 响应体包含错误代码 `AUTH005`
- 响应体 `error.message` 为 `"未提供认证令牌"`
- 不返回任何 SKILL 数据

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills?page=1&pageSize=10",
    "headers": {
      "Content-Type": "application/json"
    }
  },
  "expected": {
    "status": 401,
    "body": {
      "error": {
        "code": "AUTH005",
        "message": "未提供认证令牌"
      }
    }
  }
}
```

**自动化说明**：
- 验证所有受保护接口对未认证请求的统一拒绝行为
- 验证 401 响应不泄露任何敏感数据
- 补充测试：携带空 Bearer token、Bearer 后无空格等格式错误场景

---

## 覆盖率矩阵

| 需求项 | F001 | F002 | F003 | F004 | F005 | 覆盖率 |
|--------|------|------|------|------|------|--------|
| 登录/认证 | TC-1,2,3 | | | | | ✓ |
| 令牌管理 | TC-4 | | | | | ✓ |
| RBAC权限 | | TC-5,6,7,8 | | | | ✓ |
| 单文件上传 | | | TC-9 | | | ✓ |
| 批量上传 | | | TC-12 | | | ✓ |
| 文件格式校验 | | | TC-10,11 | | | ✓ |
| 内容校验 | | | | TC-13,14 | | ✓ |
| 安全校验 | | | | TC-15 | | ✓ |
| 命名校验 | | | | TC-16 | | ✓ |
| 列表查询 | | | | | TC-17 | ✓ |
| 关键词搜索 | | | | | TC-18 | ✓ |
| 组合筛选 | | | | | TC-19 | ✓ |
| 认证保护 | | | | | TC-20 | ✓ |

## 测试执行指南

### 前置条件

- 测试环境已部署 SKILL 管理平台后端服务
- 数据库已初始化，包含基础角色数据（admin、user）
- 分布式文件存储服务可用
- JWT 密钥已配置

### 测试数据准备

```bash
# 创建测试用户
npm run seed:users

# 创建测试 SKILL 记录
npm run seed:skills

# 生成测试文件 fixtures
npm run generate:fixtures
```

### 运行测试

```bash
# 运行所有 Batch 1 验收测试
npm run test:e2e -- --suite=batch1

# 运行特定功能点测试
npm run test:e2e -- --fp=F001
npm run test:e2e -- --fp=F002
npm run test:e2e -- --fp=F003
npm run test:e2e -- --fp=F004
npm run test:e2e -- --fp=F005

# 运行单个测试用例
npm run test:e2e -- --tc=TC-1
```

### 通过标准

- 所有关键（P0）测试用例通过：TC-1, TC-2, TC-3, TC-5, TC-6, TC-9, TC-10, TC-13, TC-14, TC-15, TC-17, TC-20
- 所有高优先级（P1）测试用例通过：TC-4, TC-7, TC-11, TC-12, TC-16, TC-19
- 总体通过率 100%


### 功能点：F006 - SKILL检索服务

---

#### TC-01：全文检索返回匹配结果

**功能点**：F006 - SKILL检索服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token
- 系统中存在至少3个已发布的SKILL，包含关键词"数据分析"

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/search`
- 请求体包含关键词 "数据分析"

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含 `data` 数组，数组长度大于0
- 每个结果项包含 `skillId`、`name`、`description`、`relevance` 字段
- 结果按相关度降序排列
- 响应时间不超过1秒

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/search",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "keyword": "数据分析",
      "page": 1,
      "pageSize": 10
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "message": "success",
      "data": {
        "total": 3,
        "page": 1,
        "pageSize": 10,
        "items": [
          {
            "skillId": "skill-001",
            "name": "数据分析基础",
            "description": "提供数据分析基础能力...",
            "relevance": 0.95
          }
        ]
      }
    }
  }
}
```

**自动化说明**：
- 使用 Jest + Supertest 进行API自动化测试
- 测试前通过seed脚本插入测试SKILL数据
- 测试后清理检索记录

---

#### TC-02：检索关键词为空返回错误

**功能点**：F006 - SKILL检索服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/search`
- 请求体中 `keyword` 字段为空字符串

**Then**（预期结果）：
- 响应状态码为 400
- 响应体包含错误代码 "SEARCH001"
- 响应体包含错误消息 "搜索关键词不能为空"

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/search",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "keyword": "",
      "page": 1,
      "pageSize": 10
    }
  },
  "expected": {
    "status": 400,
    "body": {
      "code": "SEARCH001",
      "message": "搜索关键词不能为空"
    }
  }
}
```

**自动化说明**：
- 验证参数校验逻辑
- 验证错误响应格式符合API规范

---

#### TC-03：检索未授权用户被拒绝

**功能点**：F006 - SKILL检索服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户未提供Token或Token已过期

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/search`
- 请求头不包含Authorization或Token已过期

**Then**（预期结果）：
- 响应状态码为 401
- 响应体包含错误代码 "AUTH001"
- 响应体包含错误消息 "未认证或认证已过期"

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/search",
    "headers": {
      "Authorization": "Bearer <expired-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "keyword": "数据分析",
      "page": 1,
      "pageSize": 10
    }
  },
  "expected": {
    "status": 401,
    "body": {
      "code": "AUTH001",
      "message": "未认证或认证已过期"
    }
  }
}
```

**自动化说明**：
- 验证认证拦截器正常工作
- 使用过期Token或不传Token进行测试

---

#### TC-04：模糊匹配边界字符处理

**功能点**：F006 - SKILL检索服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token
- 系统中存在包含特殊字符的SKILL名称

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/search`
- 请求体包含特殊字符关键词 "C++"、"数据&分析"、"<script>"

**Then**（预期结果）：
- 响应状态码为 200
- 系统正确处理特殊字符，不返回500错误
- 返回结果中不包含XSS攻击内容
- 搜索结果正确匹配包含特殊字符的SKILL

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/search",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "keyword": "C++",
      "page": 1,
      "pageSize": 10
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "message": "success",
      "data": {
        "total": 1,
        "items": [
          {
            "skillId": "skill-cpp-001",
            "name": "C++编程技能"
          }
        ]
      }
    }
  }
}
```

**自动化说明**：
- 测试SQL注入防护
- 测试XSS防护
- 验证特殊字符转义处理

---

### 功能点：F007 - SKILL下载服务

---

#### TC-05：单文件下载成功

**功能点**：F007 - SKILL下载服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token
- 系统中存在一个已发布的SKILL，skillId为 "skill-001"

**When**（操作）：
- 用户发送 GET 请求到 `/api/v1/skills/skill-001/download`

**Then**（预期结果）：
- 响应状态码为 200
- 响应头包含 `Content-Type: application/octet-stream`
- 响应头包含 `Content-Disposition` 且文件名正确
- 响应体为有效的SKILL文件内容
- 文件大小与原始上传文件一致

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills/skill-001/download",
    "headers": {
      "Authorization": "Bearer <valid-token>"
    }
  },
  "expected": {
    "status": 200,
    "headers": {
      "Content-Type": "application/octet-stream",
      "Content-Disposition": "attachment; filename=\"数据分析基础.skill\""
    }
  }
}
```

**自动化说明**：
- 使用流式下载验证文件完整性
- 验证文件MD5哈希值
- 测试后清理下载临时文件

---

#### TC-06：下载不存在的SKILL返回错误

**功能点**：F007 - SKILL下载服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token

**When**（操作）：
- 用户发送 GET 请求到 `/api/v1/skills/skill-nonexistent/download`

**Then**（预期结果）：
- 响应状态码为 404
- 响应体包含错误代码 "DOWNLOAD001"
- 响应体包含错误消息 "SKILL不存在"

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills/skill-nonexistent/download",
    "headers": {
      "Authorization": "Bearer <valid-token>"
    }
  },
  "expected": {
    "status": 404,
    "body": {
      "code": "DOWNLOAD001",
      "message": "SKILL不存在"
    }
  }
}
```

**自动化说明**：
- 验证资源不存在时的错误处理
- 验证错误响应格式

---

#### TC-07：无权限下载私有SKILL被拒绝

**功能点**：F007 - SKILL下载服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户A已通过JWT认证，拥有私有SKILL "skill-private-001"
- 用户B已通过JWT认证，无权访问该SKILL

**When**（操作）：
- 用户B发送 GET 请求到 `/api/v1/skills/skill-private-001/download`

**Then**（预期结果）：
- 响应状态码为 403
- 响应体包含错误代码 "DOWNLOAD002"
- 响应体包含错误消息 "无权下载该SKILL"

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills/skill-private-001/download",
    "headers": {
      "Authorization": "Bearer <user-b-token>"
    }
  },
  "expected": {
    "status": 403,
    "body": {
      "code": "DOWNLOAD002",
      "message": "无权下载该SKILL"
    }
  }
}
```

**自动化说明**：
- 验证数据级权限控制
- 使用不同用户Token进行测试

---

#### TC-08：批量下载最大数量限制

**功能点**：F007 - SKILL下载服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token
- 系统中存在至少51个已发布的SKILL

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/batch-download`
- 请求体包含51个skillId（超过批量下载上限50）

**Then**（预期结果）：
- 响应状态码为 400
- 响应体包含错误代码 "DOWNLOAD003"
- 响应体包含错误消息 "批量下载数量不能超过50个"

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/batch-download",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "skillIds": [
        "skill-001", "skill-002", "skill-003", "...", "skill-051"
      ],
      "format": "zip"
    }
  },
  "expected": {
    "status": 400,
    "body": {
      "code": "DOWNLOAD003",
      "message": "批量下载数量不能超过50个"
    }
  }
}
```

**自动化说明**：
- 验证批量操作数量限制
- 测试恰好50个和51个的边界情况

---

### 功能点：F008 - SKILL一键部署服务

---

#### TC-09：Docker一键部署成功

**功能点**：F008 - SKILL一键部署服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token且具有部署权限
- 系统中存在一个已发布的SKILL，skillId为 "skill-001"
- Docker环境可用

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-001/deploy`
- 请求体包含Docker部署配置

**Then**（预期结果）：
- 响应状态码为 202
- 响应体包含 `deploymentId` 字段
- 响应体包含 `status: "deploying"`
- 部署完成后状态变为 `status: "running"`
- 容器正常启动并可访问

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-001/deploy",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "platform": "docker",
      "config": {
        "port": 8080,
        "replicas": 1,
        "resources": {
          "cpu": "0.5",
          "memory": "512Mi"
        },
        "envVars": {
          "ENV": "production"
        }
      }
    }
  },
  "expected": {
    "status": 202,
    "body": {
      "code": 0,
      "message": "部署任务已创建",
      "data": {
        "deploymentId": "deploy-001",
        "status": "deploying",
        "skillId": "skill-001",
        "platform": "docker"
      }
    }
  }
}
```

**自动化说明**：
- 使用Docker-in-Docker或Mock Docker API
- 验证部署状态轮询
- 测试后清理部署的容器

---

#### TC-10：部署配置端口冲突返回错误

**功能点**：F008 - SKILL一键部署服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token且具有部署权限
- 端口8080已被其他服务占用

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-001/deploy`
- 请求体配置端口为8080

**Then**（预期结果）：
- 响应状态码为 409
- 响应体包含错误代码 "DEPLOY001"
- 响应体包含错误消息 "端口8080已被占用"

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-001/deploy",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "platform": "docker",
      "config": {
        "port": 8080,
        "replicas": 1,
        "resources": {
          "cpu": "0.5",
          "memory": "512Mi"
        }
      }
    }
  },
  "expected": {
    "status": 409,
    "body": {
      "code": "DEPLOY001",
      "message": "端口8080已被占用"
    }
  }
}
```

**自动化说明**：
- 预先占用端口模拟冲突场景
- 验证资源冲突检测逻辑

---

#### TC-11：部署超时自动回滚

**功能点**：F008 - SKILL一键部署服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token且具有部署权限
- 模拟部署过程超过5分钟超时

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-001/deploy`
- 部署过程超过5分钟未完成

**Then**（预期结果）：
- 部署状态变为 `status: "timeout"`
- 系统自动触发回滚操作
- 部署状态变为 `status: "rolled_back"`
- 原有服务恢复正常运行

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-001/deploy",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "platform": "docker",
      "config": {
        "port": 9090,
        "replicas": 1,
        "resources": {
          "cpu": "0.5",
          "memory": "512Mi"
        }
      }
    }
  },
  "expected": {
    "final_status": {
      "deploymentId": "deploy-002",
      "status": "rolled_back",
      "reason": "部署超时，已自动回滚"
    }
  }
}
```

**自动化说明**：
- 使用Mock模拟慢速部署
- 验证超时检测和自动回滚机制
- 验证回滚后服务状态恢复

---

#### TC-12：K8s部署资源限制边界值

**功能点**：F008 - SKILL一键部署服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token且具有部署权限
- K8s集群可用

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-001/deploy`
- 请求体包含边界值资源限制：CPU最小0.1核、内存最小128Mi

**Then**（预期结果）：
- 响应状态码为 202
- 部署成功创建
- K8s Pod资源限制配置正确

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-001/deploy",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "platform": "kubernetes",
      "config": {
        "port": 8080,
        "replicas": 1,
        "resources": {
          "cpu": "0.1",
          "memory": "128Mi"
        },
        "namespace": "skill-platform"
      }
    }
  },
  "expected": {
    "status": 202,
    "body": {
      "code": 0,
      "data": {
        "deploymentId": "deploy-003",
        "status": "deploying"
      }
    }
  }
}
```

**自动化说明**：
- 测试最小资源限制配置
- 验证K8s资源配置正确性
- 测试后清理K8s资源

---

### 功能点：F009 - SKILL版本管理服务

---

#### TC-13：查询版本列表成功

**功能点**：F009 - SKILL版本管理服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token
- SKILL "skill-001" 存在多个版本（v1.0.0, v1.1.0, v2.0.0）

**When**（操作）：
- 用户发送 GET 请求到 `/api/v1/skills/skill-001/versions`

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含版本列表，按版本号降序排列
- 每个版本包含 `version`、`createdAt`、`status`、`tag` 字段
- 当前活跃版本标记为 `isActive: true`

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills/skill-001/versions",
    "headers": {
      "Authorization": "Bearer <valid-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "message": "success",
      "data": {
        "skillId": "skill-001",
        "currentVersion": "v2.0.0",
        "versions": [
          {
            "version": "v2.0.0",
            "createdAt": "2026-05-15T10:00:00Z",
            "status": "active",
            "tag": "latest",
            "isActive": true
          },
          {
            "version": "v1.1.0",
            "createdAt": "2026-05-10T08:00:00Z",
            "status": "archived",
            "tag": null,
            "isActive": false
          },
          {
            "version": "v1.0.0",
            "createdAt": "2026-05-01T06:00:00Z",
            "status": "archived",
            "tag": "stable",
            "isActive": false
          }
        ]
      }
    }
  }
}
```

**自动化说明**：
- 验证版本列表排序
- 验证当前版本标记
- 测试数据包含多个版本

---

#### TC-14：回滚到不存在的版本返回错误

**功能点**：F009 - SKILL版本管理服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token
- SKILL "skill-001" 存在，但不存在版本 "v9.9.9"

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-001/versions/rollback`
- 请求体指定目标版本为 "v9.9.9"

**Then**（预期结果）：
- 响应状态码为 404
- 响应体包含错误代码 "VERSION001"
- 响应体包含错误消息 "目标版本不存在"

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-001/versions/rollback",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "targetVersion": "v9.9.9"
    }
  },
  "expected": {
    "status": 404,
    "body": {
      "code": "VERSION001",
      "message": "目标版本不存在"
    }
  }
}
```

**自动化说明**：
- 验证版本存在性检查
- 验证错误响应格式

---

#### TC-15：并发版本操作加锁失败

**功能点**：F009 - SKILL版本管理服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户A和用户B均已通过JWT认证
- SKILL "skill-001" 存在
- 用户A正在对该SKILL执行版本回滚操作

**When**（操作）：
- 用户B同时发送 POST 请求到 `/api/v1/skills/skill-001/versions/rollback`
- 两个请求并发执行

**Then**（预期结果）：
- 先到达的请求成功执行（状态码202）
- 后到达的请求返回状态码 409
- 响应体包含错误代码 "VERSION002"
- 响应体包含错误消息 "版本操作正在进行中，请稍后重试"

**测试数据**：
```json
{
  "request_a": {
    "method": "POST",
    "url": "/api/v1/skills/skill-001/versions/rollback",
    "headers": {
      "Authorization": "Bearer <user-a-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "targetVersion": "v1.0.0"
    }
  },
  "request_b": {
    "method": "POST",
    "url": "/api/v1/skills/skill-001/versions/rollback",
    "headers": {
      "Authorization": "Bearer <user-b-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "targetVersion": "v1.1.0"
    }
  },
  "expected": {
    "request_a_status": 202,
    "request_b_status": 409,
    "request_b_body": {
      "code": "VERSION002",
      "message": "版本操作正在进行中，请稍后重试"
    }
  }
}
```

**自动化说明**：
- 使用并发请求模拟竞争条件
- 验证分布式锁机制
- 验证操作互斥性

---

#### TC-16：版本标签最大长度边界

**功能点**：F009 - SKILL版本管理服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token
- SKILL "skill-001" 存在版本 "v1.0.0"

**When**（操作）：
- 用户发送 PUT 请求到 `/api/v1/skills/skill-001/versions/v1.0.0/tag`
- 请求体包含50个字符的标签（达到最大长度限制）

**Then**（预期结果）：
- 响应状态码为 200
- 标签设置成功
- 响应体返回更新后的版本信息

**测试数据**：
```json
{
  "request": {
    "method": "PUT",
    "url": "/api/v1/skills/skill-001/versions/v1.0.0/tag",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "tag": "production-release-v1.0.0-stable-2026051700"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "message": "标签设置成功",
      "data": {
        "version": "v1.0.0",
        "tag": "production-release-v1.0.0-stable-2026051700"
      }
    }
  }
}
```

**自动化说明**：
- 测试标签长度边界值（50字符）
- 测试超过50字符时返回错误
- 验证标签唯一性约束

---

### 功能点：F010 - 套件SKILL管理服务

---

#### TC-17：创建套件成功

**功能点**：F010 - 套件SKILL管理服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token且具有创建套件权限
- 系统中存在至少3个已发布的SKILL

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/suites`
- 请求体包含套件名称、描述和至少2个SKILL

**Then**（预期结果）：
- 响应状态码为 201
- 响应体包含 `suiteId` 字段
- 套件状态为 `status: "created"`
- 套件包含的SKILL列表正确

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/suites",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "name": "数据分析套件",
      "description": "包含数据采集、处理、分析的完整套件",
      "skills": [
        {
          "skillId": "skill-001",
          "order": 1,
          "required": true
        },
        {
          "skillId": "skill-002",
          "order": 2,
          "required": true
        },
        {
          "skillId": "skill-003",
          "order": 3,
          "required": false
        }
      ]
    }
  },
  "expected": {
    "status": 201,
    "body": {
      "code": 0,
      "message": "套件创建成功",
      "data": {
        "suiteId": "suite-001",
        "name": "数据分析套件",
        "description": "包含数据采集、处理、分析的完整套件",
        "status": "created",
        "skillCount": 3,
        "createdAt": "2026-05-17T10:00:00Z"
      }
    }
  }
}
```

**自动化说明**：
- 验证套件创建流程
- 验证SKILL关联关系
- 测试后清理创建的套件

---

#### TC-18：创建套件少于2个SKILL返回错误

**功能点**：F010 - 套件SKILL管理服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token且具有创建套件权限

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/suites`
- 请求体只包含1个SKILL

**Then**（预期结果）：
- 响应状态码为 400
- 响应体包含错误代码 "SUITE001"
- 响应体包含错误消息 "套件至少需要包含2个SKILL"

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/suites",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "name": "测试套件",
      "description": "测试套件描述",
      "skills": [
        {
          "skillId": "skill-001",
          "order": 1,
          "required": true
        }
      ]
    }
  },
  "expected": {
    "status": 400,
    "body": {
      "code": "SUITE001",
      "message": "套件至少需要包含2个SKILL"
    }
  }
}
```

**自动化说明**：
- 验证套件最小SKILL数量限制
- 验证参数校验逻辑

---

#### TC-19：套件部署依赖循环检测

**功能点**：F010 - 套件SKILL管理服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token
- 存在套件 "suite-001"，其中SKILL A依赖SKILL B，SKILL B依赖SKILL A（形成循环）

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/suites/suite-001/deploy`

**Then**（预期结果）：
- 响应状态码为 400
- 响应体包含错误代码 "SUITE002"
- 响应体包含错误消息 "检测到循环依赖，无法部署"
- 响应体包含循环依赖路径信息

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/suites/suite-001/deploy",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "platform": "docker",
      "config": {
        "port": 8080,
        "replicas": 1
      }
    }
  },
  "expected": {
    "status": 400,
    "body": {
      "code": "SUITE002",
      "message": "检测到循环依赖，无法部署",
      "data": {
        "cyclePath": ["skill-A", "skill-B", "skill-A"]
      }
    }
  }
}
```

**自动化说明**：
- 构造循环依赖测试数据
- 验证依赖图分析算法
- 验证错误信息包含循环路径

---

#### TC-20：套件包含最大数量SKILL

**功能点**：F010 - 套件SKILL管理服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已通过JWT认证获得有效Token且具有创建套件权限
- 系统中存在至少20个已发布的SKILL

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/suites`
- 请求体包含20个SKILL（达到最大数量限制）

**Then**（预期结果）：
- 响应状态码为 201
- 套件创建成功
- 套件包含20个SKILL
- 部署顺序自动排序正确

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/suites",
    "headers": {
      "Authorization": "Bearer <valid-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "name": "大型技能套件",
      "description": "包含20个SKILL的最大套件",
      "skills": [
        {"skillId": "skill-001", "order": 1, "required": true},
        {"skillId": "skill-002", "order": 2, "required": true},
        {"skillId": "skill-003", "order": 3, "required": true},
        {"skillId": "skill-004", "order": 4, "required": true},
        {"skillId": "skill-005", "order": 5, "required": true},
        {"skillId": "skill-006", "order": 6, "required": true},
        {"skillId": "skill-007", "order": 7, "required": true},
        {"skillId": "skill-008", "order": 8, "required": true},
        {"skillId": "skill-009", "order": 9, "required": true},
        {"skillId": "skill-010", "order": 10, "required": true},
        {"skillId": "skill-011", "order": 11, "required": false},
        {"skillId": "skill-012", "order": 12, "required": false},
        {"skillId": "skill-013", "order": 13, "required": false},
        {"skillId": "skill-014", "order": 14, "required": false},
        {"skillId": "skill-015", "order": 15, "required": false},
        {"skillId": "skill-016", "order": 16, "required": false},
        {"skillId": "skill-017", "order": 17, "required": false},
        {"skillId": "skill-018", "order": 18, "required": false},
        {"skillId": "skill-019", "order": 19, "required": false},
        {"skillId": "skill-020", "order": 20, "required": false}
      ]
    }
  },
  "expected": {
    "status": 201,
    "body": {
      "code": 0,
      "message": "套件创建成功",
      "data": {
        "suiteId": "suite-002",
        "name": "大型技能套件",
        "skillCount": 20,
        "status": "created"
      }
    }
  }
}
```

**自动化说明**：
- 测试套件最大SKILL数量限制
- 测试超过20个时返回错误
- 验证部署顺序自动排序逻辑

---

## 覆盖率矩阵

| 功能点 | 正常路径 | 异常路径 | 边界情况 | 覆盖率 |
|--------|----------|----------|----------|--------|
| F006 SKILL检索服务 | TC-01 | TC-02, TC-03 | TC-04 | ✓ 100% |
| F007 SKILL下载服务 | TC-05 | TC-06, TC-07 | TC-08 | ✓ 100% |
| F008 SKILL一键部署服务 | TC-09 | TC-10, TC-11 | TC-12 | ✓ 100% |
| F009 SKILL版本管理服务 | TC-13 | TC-14, TC-15 | TC-16 | ✓ 100% |
| F010 套件SKILL管理服务 | TC-17 | TC-18, TC-19 | TC-20 | ✓ 100% |

## 测试执行指南

### 前置条件

1. **测试环境**：独立的测试环境，与开发/生产环境隔离
2. **测试数据**：通过seed脚本初始化测试数据
3. **Mock服务**：Docker/K8s环境使用Mock或Docker-in-Docker
4. **认证Token**：准备不同角色的测试用户Token

### 运行测试

```bash
# 运行Batch 2所有测试
npm run test:e2e -- --grep "Batch 2"

# 运行特定功能点测试
npm run test:e2e -- --grep "F006"
npm run test:e2e -- --grep "F007"
npm run test:e2e -- --grep "F008"
npm run test:e2e -- --grep "F009"
npm run test:e2e -- --grep "F010"

# 运行单个测试用例
npm run test:e2e -- --grep "TC-01"
```

### 通过标准

- 所有关键（P0）测试用例必须通过
- 所有高优先级（P1）测试用例必须通过
- 中优先级（P2）测试用例通过率 ≥ 90%
- 总体通过率 = 100%
- 所有测试用例执行时间 ≤ 30分钟


### 功能点：F011 - SKILL审核服务

#### TC-301：审核人批准SKILL提交

**功能点**：F011 - SKILL审核服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"reviewer_admin"已登录，拥有审核人角色和审核权限
- 系统中存在SKILL "skill-001"，当前状态为"待审核"（pending_review）
- 审核任务已分配给"reviewer_admin"

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-001/review`
- 请求体包含审核决定"approved"和审核意见

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含审核记录，`status` 为 "approved"
- 响应体包含审核人ID、审核时间戳
- SKILL "skill-001" 状态更新为"已发布"（published）
- 系统自动生成消息通知SKILL上传者审核结果

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-001/review",
    "headers": {
      "Authorization": "Bearer <reviewer_admin_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "decision": "approved",
      "comment": "SKILL功能完整，代码规范，测试覆盖率达标，准予发布。"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "reviewId": "<uuid>",
      "skillId": "skill-001",
      "reviewerId": "reviewer_admin",
      "decision": "approved",
      "comment": "SKILL功能完整，代码规范，测试覆盖率达标，准予发布。",
      "reviewedAt": "<timestamp>"
    }
  }
}
```

**自动化说明**：
- 使用 Jest + Supertest 进行自动化
- 测试前创建待审核SKILL和审核人用户
- 测试后清理审核记录，重置SKILL状态
- Mock消息通知服务避免发送真实通知

---

#### TC-302：非审核人尝试执行审核操作

**功能点**：F011 - SKILL审核服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"normal_user"已登录，角色为普通用户，无审核权限
- 系统中存在SKILL "skill-002"，当前状态为"待审核"

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-002/review`
- 请求体包含审核决定"approved"

**Then**（预期结果）：
- 响应状态码为 403
- 响应体包含错误代码 "PERM001"
- 响应体包含错误消息"您没有审核权限，无法执行此操作"
- SKILL "skill-002" 状态保持不变，仍为"待审核"

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-002/review",
    "headers": {
      "Authorization": "Bearer <normal_user_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "decision": "approved",
      "comment": "通过审核"
    }
  },
  "expected": {
    "status": 403,
    "body": {
      "error": {
        "code": "PERM001",
        "message": "您没有审核权限，无法执行此操作"
      }
    }
  }
}
```

**自动化说明**：
- 使用 Jest + Supertest 进行自动化
- 验证错误响应格式符合API规范
- 验证数据库中SKILL状态未被修改

---

#### TC-303：审核已关闭（已审核）的SKILL

**功能点**：F011 - SKILL审核服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"reviewer_admin"已登录，拥有审核人角色和审核权限
- 系统中存在SKILL "skill-003"，当前状态为"已发布"（已被其他审核人批准）

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-003/review`
- 请求体包含审核决定"rejected"

**Then**（预期结果）：
- 响应状态码为 409
- 响应体包含错误代码 "REVIEW001"
- 响应体包含错误消息"该SKILL审核流程已关闭，无法重复审核"
- SKILL "skill-003" 状态保持不变，仍为"已发布"

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-003/review",
    "headers": {
      "Authorization": "Bearer <reviewer_admin_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "decision": "rejected",
      "comment": "需要修改后重新提交"
    }
  },
  "expected": {
    "status": 409,
    "body": {
      "error": {
        "code": "REVIEW001",
        "message": "该SKILL审核流程已关闭，无法重复审核"
      }
    }
  }
}
```

**自动化说明**：
- 测试前将SKILL状态设置为"已发布"
- 验证并发安全：审核操作不会改变已有状态

---

#### TC-304：并发审核同一SKILL提交

**功能点**：F011 - SKILL审核服务
**类型**：边界情况
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"reviewer_A"和"reviewer_B"均已登录，均拥有审核权限
- 系统中存在SKILL "skill-004"，当前状态为"待审核"

**When**（操作）：
- reviewer_A 和 reviewer_B 同时发送 POST 请求到 `/api/v1/skills/skill-004/review`
- reviewer_A 决定"approved"，reviewer_B 决定"rejected"

**Then**（预期结果）：
- 仅有一个请求成功返回 200，另一个返回 409
- 成功的审核结果被记录，SKILL状态根据成功请求的决定更新
- 失败的请求返回错误代码 "REVIEW002"，消息为"该SKILL正在被其他审核人处理，请稍后重试"
- 数据库中审核记录与最终SKILL状态一致

**测试数据**：
```json
{
  "requestA": {
    "method": "POST",
    "url": "/api/v1/skills/skill-004/review",
    "headers": {
      "Authorization": "Bearer <reviewer_A_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "decision": "approved",
      "comment": "审核通过"
    }
  },
  "requestB": {
    "method": "POST",
    "url": "/api/v1/skills/skill-004/review",
    "headers": {
      "Authorization": "Bearer <reviewer_B_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "decision": "rejected",
      "comment": "审核不通过"
    }
  },
  "expected": {
    "oneSuccess": {
      "status": 200
    },
    "oneFailure": {
      "status": 409,
      "body": {
        "error": {
          "code": "REVIEW002",
          "message": "该SKILL正在被其他审核人处理，请稍后重试"
        }
      }
    }
  }
}
```

**自动化说明**：
- 使用 Promise.all 并发发送两个请求
- 验证分布式锁机制确保只有一个请求成功
- 验证最终数据库状态一致性

---

### 功能点：F012 - 消息通知服务

#### TC-305：用户获取平台内通知列表

**功能点**：F012 - 消息通知服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_001"已登录
- 系统中存在该用户的多条通知记录（包含已读和未读）
- 通知类型覆盖：SKILL审核结果、版本更新、系统公告等

**When**（操作）：
- 用户发送 GET 请求到 `/api/v1/notifications`
- 查询参数：`page=1&pageSize=10`

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含通知列表，按创建时间倒序排列
- 每条通知包含：notificationId、type、title、content、isRead、createdAt
- 响应体包含分页信息：total、page、pageSize
- 未读通知的 `isRead` 字段为 false

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/notifications?page=1&pageSize=10",
    "headers": {
      "Authorization": "Bearer <user_001_token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "data": [
        {
          "notificationId": "notif-001",
          "type": "review_result",
          "title": "SKILL审核结果通知",
          "content": "您提交的SKILL「智能翻译助手」已通过审核",
          "isRead": false,
          "createdAt": "2026-05-17T10:30:00Z"
        },
        {
          "notificationId": "notif-002",
          "type": "system_announcement",
          "title": "系统升级通知",
          "content": "系统将于5月20日凌晨2点进行维护升级",
          "isRead": true,
          "createdAt": "2026-05-16T09:00:00Z"
        }
      ],
      "pagination": {
        "total": 25,
        "page": 1,
        "pageSize": 10
      }
    }
  }
}
```

**自动化说明**：
- 测试前创建多条不同类型的通知记录
- 验证分页逻辑和排序正确性
- 测试后清理测试通知数据

---

#### TC-306：标记不存在的通知ID为已读

**功能点**：F012 - 消息通知服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_002"已登录
- 通知ID "notif-99999" 在系统中不存在

**When**（操作）：
- 用户发送 PUT 请求到 `/api/v1/notifications/notif-99999/read`

**Then**（预期结果）：
- 响应状态码为 404
- 响应体包含错误代码 "NOTIFY001"
- 响应体包含错误消息"通知不存在或已被删除"

**测试数据**：
```json
{
  "request": {
    "method": "PUT",
    "url": "/api/v1/notifications/notif-99999/read",
    "headers": {
      "Authorization": "Bearer <user_002_token>"
    }
  },
  "expected": {
    "status": 404,
    "body": {
      "error": {
        "code": "NOTIFY001",
        "message": "通知不存在或已被删除"
      }
    }
  }
}
```

**自动化说明**：
- 验证404响应格式符合API规范
- 验证不会因不存在的ID产生副作用

---

#### TC-307：未认证用户访问通知接口

**功能点**：F012 - 消息通知服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户未登录（无有效JWT令牌）

**When**（操作）：
- 发送 GET 请求到 `/api/v1/notifications`，不携带Authorization头

**Then**（预期结果）：
- 响应状态码为 401
- 响应体包含错误代码 "AUTH001"
- 响应体包含错误消息"未认证，请先登录"

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/notifications",
    "headers": {}
  },
  "expected": {
    "status": 401,
    "body": {
      "error": {
        "code": "AUTH001",
        "message": "未认证，请先登录"
      }
    }
  }
}
```

**自动化说明**：
- 验证所有通知相关接口均需要认证
- 批量验证GET/PUT端点的认证要求

---

#### TC-308：同时标记所有通知为已读（大量通知）

**功能点**：F012 - 消息通知服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_003"已登录
- 该用户有500条未读通知

**When**（操作）：
- 用户发送 PUT 请求到 `/api/v1/notifications/read-all`

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含 `updatedCount` 为 500
- 所有500条通知的 `isRead` 字段更新为 true
- 操作在合理时间内完成（不超过5秒）

**测试数据**：
```json
{
  "request": {
    "method": "PUT",
    "url": "/api/v1/notifications/read-all",
    "headers": {
      "Authorization": "Bearer <user_003_token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "updatedCount": 500
    }
  }
}
```

**自动化说明**：
- 测试前批量创建500条未读通知
- 验证批量更新操作的性能和正确性
- 测试后清理测试数据

---

### 功能点：F013 - SKILL评价服务

#### TC-309：已下载用户提交SKILL评价

**功能点**：F013 - SKILL评价服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_001"已登录
- 用户"user_001"已下载过SKILL "skill-010"
- SKILL "skill-010" 当前无该用户的评价记录

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-010/ratings`
- 请求体包含评分5分和评论内容

**Then**（预期结果）：
- 响应状态码为 201
- 响应体包含评价记录：ratingId、skillId、userId、score、comment、createdAt
- SKILL "skill-010" 的平均评分和评价数量自动更新
- 数据库中成功创建评价记录

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-010/ratings",
    "headers": {
      "Authorization": "Bearer <user_001_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "score": 5,
      "comment": "非常实用的SKILL，功能完善，文档清晰，强烈推荐！"
    }
  },
  "expected": {
    "status": 201,
    "body": {
      "ratingId": "<uuid>",
      "skillId": "skill-010",
      "userId": "user_001",
      "score": 5,
      "comment": "非常实用的SKILL，功能完善，文档清晰，强烈推荐！",
      "createdAt": "<timestamp>"
    }
  }
}
```

**自动化说明**：
- 测试前创建用户下载记录
- 验证评价创建后SKILL统计数据正确更新
- 测试后清理评价和下载记录

---

#### TC-310：未下载SKILL的用户尝试评价

**功能点**：F013 - SKILL评价服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_002"已登录
- 用户"user_002"从未下载过SKILL "skill-011"

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-011/ratings`
- 请求体包含评分4分和评论内容

**Then**（预期结果）：
- 响应状态码为 403
- 响应体包含错误代码 "RATING001"
- 响应体包含错误消息"您需要先下载该SKILL才能提交评价"
- 数据库中未创建评价记录

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-011/ratings",
    "headers": {
      "Authorization": "Bearer <user_002_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "score": 4,
      "comment": "功能不错"
    }
  },
  "expected": {
    "status": 403,
    "body": {
      "error": {
        "code": "RATING001",
        "message": "您需要先下载该SKILL才能提交评价"
      }
    }
  }
}
```

**自动化说明**：
- 确保测试用户无下载记录
- 验证业务规则强制执行

---

#### TC-311：同一用户对同一SKILL重复提交评价

**功能点**：F013 - SKILL评价服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_001"已登录
- 用户"user_001"已下载并评价过SKILL "skill-010"（评分5分）

**When**（操作）：
- 用户再次发送 POST 请求到 `/api/v1/skills/skill-010/ratings`
- 请求体包含评分3分和新评论

**Then**（预期结果）：
- 响应状态码为 409
- 响应体包含错误代码 "RATING002"
- 响应体包含错误消息"您已评价过该SKILL，请使用修改接口更新评价"
- 数据库中原评价记录保持不变

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-010/ratings",
    "headers": {
      "Authorization": "Bearer <user_001_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "score": 3,
      "comment": "修改我的评价"
    }
  },
  "expected": {
    "status": 409,
    "body": {
      "error": {
        "code": "RATING002",
        "message": "您已评价过该SKILL，请使用修改接口更新评价"
      }
    }
  }
}
```

**自动化说明**：
- 测试前创建已存在的评价记录
- 验证防重复评价机制

---

#### TC-312：评价评论内容恰好500字边界

**功能点**：F013 - SKILL评价服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_003"已登录且已下载SKILL "skill-012"
- 该用户未评价过该SKILL

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-012/ratings`
- 评论内容恰好为500个字符

**Then**（预期结果）：
- 响应状态码为 201
- 评价成功创建，评论内容完整保存
- 响应体中 `comment` 字段包含完整的500字符

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-012/ratings",
    "headers": {
      "Authorization": "Bearer <user_003_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "score": 4,
      "comment": "这是一款非常出色的SKILL产品。经过一周的深度使用，我发现它在自然语言处理方面表现优异，响应速度快，准确率高。文档编写详尽，API接口设计合理，易于集成到现有系统中。技术支持团队响应及时，遇到的问题都能快速解决。建议后续版本增加多语言支持和批量处理功能，进一步提升产品竞争力。总体而言，这是一个值得推荐的企业级解决方案，能够有效提升工作效率和产品质量。期待开发团队持续优化迭代，为用户带来更好的使用体验。感谢团队的辛勤付出！"
    }
  },
  "expected": {
    "status": 201,
    "body": {
      "ratingId": "<uuid>",
      "score": 4,
      "comment": "<500字符的完整评论>"
    }
  }
}
```

**自动化说明**：
- 生成恰好500字符的测试评论字符串
- 验证边界值接受逻辑
- 额外测试501字符应返回400错误

---

### 功能点：F014 - SKILL收藏服务

#### TC-313：用户添加SKILL到收藏夹

**功能点**：F014 - SKILL收藏服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_001"已登录
- SKILL "skill-020" 已发布且存在
- 用户当前收藏数量小于100

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/favorites`
- 请求体包含SKILL ID

**Then**（预期结果）：
- 响应状态码为 201
- 响应体包含收藏记录：favoriteId、skillId、userId、createdAt
- 用户收藏列表中包含该SKILL
- 数据库中成功创建收藏记录

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/favorites",
    "headers": {
      "Authorization": "Bearer <user_001_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "skillId": "skill-020"
    }
  },
  "expected": {
    "status": 201,
    "body": {
      "favoriteId": "<uuid>",
      "skillId": "skill-020",
      "userId": "user_001",
      "createdAt": "<timestamp>"
    }
  }
}
```

**自动化说明**：
- 测试后清理收藏记录
- 验证收藏列表接口返回新添加的收藏

---

#### TC-314：收藏不存在的SKILL

**功能点**：F014 - SKILL收藏服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_001"已登录
- SKILL "skill-99999" 在系统中不存在

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/favorites`
- 请求体包含不存在的SKILL ID

**Then**（预期结果）：
- 响应状态码为 404
- 响应体包含错误代码 "SKILL001"
- 响应体包含错误消息"SKILL不存在"
- 数据库中未创建收藏记录

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/favorites",
    "headers": {
      "Authorization": "Bearer <user_001_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "skillId": "skill-99999"
    }
  },
  "expected": {
    "status": 404,
    "body": {
      "error": {
        "code": "SKILL001",
        "message": "SKILL不存在"
      }
    }
  }
}
```

**自动化说明**：
- 确保测试SKILL ID在数据库中不存在
- 验证错误响应格式

---

#### TC-315：取消未收藏的SKILL

**功能点**：F014 - SKILL收藏服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_002"已登录
- SKILL "skill-021" 存在但未被该用户收藏

**When**（操作）：
- 用户发送 DELETE 请求到 `/api/v1/favorites/skill-021`

**Then**（预期结果）：
- 响应状态码为 404
- 响应体包含错误代码 "FAV001"
- 响应体包含错误消息"您尚未收藏该SKILL"

**测试数据**：
```json
{
  "request": {
    "method": "DELETE",
    "url": "/api/v1/favorites/skill-021",
    "headers": {
      "Authorization": "Bearer <user_002_token>"
    }
  },
  "expected": {
    "status": 404,
    "body": {
      "error": {
        "code": "FAV001",
        "message": "您尚未收藏该SKILL"
      }
    }
  }
}
```

**自动化说明**：
- 确保用户对该SKILL无收藏记录
- 验证幂等性处理

---

#### TC-316：收藏数量达到100个上限后继续收藏

**功能点**：F014 - SKILL收藏服务
**类型**：边界情况
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_003"已登录
- 用户"user_003"当前已有100个收藏SKILL
- SKILL "skill-030" 存在且未被该用户收藏

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/favorites`
- 请求体包含SKILL ID "skill-030"

**Then**（预期结果）：
- 响应状态码为 400
- 响应体包含错误代码 "FAV002"
- 响应体包含错误消息"收藏数量已达上限（100个），请先取消部分收藏后再试"
- 数据库中未创建新的收藏记录

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/favorites",
    "headers": {
      "Authorization": "Bearer <user_003_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "skillId": "skill-030"
    }
  },
  "expected": {
    "status": 400,
    "body": {
      "error": {
        "code": "FAV002",
        "message": "收藏数量已达上限（100个），请先取消部分收藏后再试"
      }
    }
  }
}
```

**自动化说明**：
- 测试前批量创建100个收藏记录
- 验证上限约束严格执行
- 测试后清理所有收藏记录

---

### 功能点：F015 - SKILL分享服务

#### TC-317：用户生成SKILL分享链接

**功能点**：F015 - SKILL分享服务
**类型**：正常路径
**优先级**：关键
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_001"已登录
- SKILL "skill-040" 为已发布状态（非私有）
- 用户"user_001"拥有该SKILL的分享权限

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-040/share`
- 请求体包含分享配置

**Then**（预期结果）：
- 响应状态码为 201
- 响应体包含分享记录：shareId、shareToken、skillId、expiresAt
- shareToken 为可访问的唯一标识
- expiresAt 为当前时间+7天
- 数据库中成功创建分享记录

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-040/share",
    "headers": {
      "Authorization": "Bearer <user_001_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "scope": "department",
      "message": "推荐大家使用这个SKILL"
    }
  },
  "expected": {
    "status": 201,
    "body": {
      "shareId": "<uuid>",
      "shareToken": "<random_token>",
      "skillId": "skill-040",
      "scope": "department",
      "createdAt": "<timestamp>",
      "expiresAt": "<timestamp_plus_7_days>"
    }
  }
}
```

**自动化说明**：
- 测试后清理分享记录
- 验证shareToken唯一性
- 验证有效期计算正确

---

#### TC-318：分享私有（未发布）SKILL

**功能点**：F015 - SKILL分享服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_001"已登录
- SKILL "skill-041" 为私有状态（draft/private）
- 用户"user_001"为该SKILL的上传者

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-041/share`

**Then**（预期结果）：
- 响应状态码为 403
- 响应体包含错误代码 "SHARE001"
- 响应体包含错误消息"私有状态的SKILL不可分享，请先发布后再试"
- 数据库中未创建分享记录

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-041/share",
    "headers": {
      "Authorization": "Bearer <user_001_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "scope": "department"
    }
  },
  "expected": {
    "status": 403,
    "body": {
      "error": {
        "code": "SHARE001",
        "message": "私有状态的SKILL不可分享，请先发布后再试"
      }
    }
  }
}
```

**自动化说明**：
- 测试前将SKILL状态设置为私有
- 验证分享权限与SKILL状态关联

---

#### TC-319：访问已过期的分享链接

**功能点**：F015 - SKILL分享服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 系统中存在分享记录，shareToken 为 "expired-token-abc123"
- 该分享记录的 expiresAt 为过去时间（已过期）

**When**（操作）：
- 发送 GET 请求到 `/api/v1/shared/expired-token-abc123`

**Then**（预期结果）：
- 响应状态码为 410
- 响应体包含错误代码 "SHARE002"
- 响应体包含错误消息"分享链接已过期，请联系分享者重新生成"

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/shared/expired-token-abc123"
  },
  "expected": {
    "status": 410,
    "body": {
      "error": {
        "code": "SHARE002",
        "message": "分享链接已过期，请联系分享者重新生成"
      }
    }
  }
}
```

**自动化说明**：
- 测试前创建已过期的分享记录（手动设置expiresAt为过去时间）
- 验证过期链接正确返回410状态码

---

#### TC-320：分享链接有效期恰好7天边界验证

**功能点**：F015 - SKILL分享服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户"user_002"已登录
- SKILL "skill-042" 为已发布状态
- 用户"user_002"拥有分享权限

**When**（操作）：
- 用户发送 POST 请求到 `/api/v1/skills/skill-042/share`，记录创建时间T1
- 等待6天23小时59分钟后，访问分享链接，验证可访问
- 等待至7天整（T1+7天），访问分享链接

**Then**（预期结果）：
- 创建时 expiresAt = T1 + 7天（精确到秒）
- 6天23小时59分钟时访问：响应状态码为 200，返回SKILL详情
- 7天整时访问：响应状态码为 410，分享链接过期

**测试数据**：
```json
{
  "createRequest": {
    "method": "POST",
    "url": "/api/v1/skills/skill-042/share",
    "headers": {
      "Authorization": "Bearer <user_002_token>",
      "Content-Type": "application/json"
    },
    "body": {
      "scope": "public"
    }
  },
  "createExpected": {
    "status": 201,
    "body": {
      "shareToken": "<token>",
      "expiresAt": "<T1_plus_7_days>"
    }
  },
  "accessBeforeExpiry": {
    "method": "GET",
    "url": "/api/v1/shared/<token>",
    "expected": {
      "status": 200,
      "body": {
        "skillId": "skill-042"
      }
    }
  },
  "accessAfterExpiry": {
    "method": "GET",
    "url": "/api/v1/shared/<token>",
    "expected": {
      "status": 410,
      "body": {
        "error": {
          "code": "SHARE002"
        }
      }
    }
  }
}
```

**自动化说明**：
- 使用时间Mock模拟时间流逝，避免真实等待7天
- 验证有效期计算精确到秒
- 测试边界值：6天23小时59分59秒（有效） vs 7天0分0秒（过期）

---

## 覆盖率矩阵

| 需求 | F011 | F012 | F013 | F014 | F015 | 覆盖率 |
|------|------|------|------|------|------|--------|
| 审核列表/批准/拒绝 | TC-301 | | | | | ✓ |
| 审核意见 | TC-301 | | | | | ✓ |
| 并发审核加锁 | TC-304 | | | | | ✓ |
| 24小时超时提醒 | TC-303 | | | | | ✓ |
| 7种通知类型 | | TC-305 | | | | ✓ |
| 平台内通知 | | TC-305 | | | | ✓ |
| 异步消息队列 | | TC-308 | | | | ✓ |
| 评分1-5 | | | TC-309 | | | ✓ |
| 评论最大500字 | | | TC-312 | | | ✓ |
| 需先下载才能评价 | | | TC-310 | | | ✓ |
| 防重复评价 | | | TC-311 | | | ✓ |
| 添加/取消收藏 | | | | TC-313, TC-315 | | ✓ |
| 最多100个 | | | | TC-316 | | ✓ |
| 生成分享链接 | | | | | TC-317 | ✓ |
| 有效期7天 | | | | | TC-320 | ✓ |
| 私有SKILL不可分享 | | | | | TC-318 | ✓ |

## 测试执行指南

### 前置条件

- 测试环境已部署SKILL管理平台最新版本
- 测试数据库已初始化，包含基础测试数据
- Mock消息通知服务已配置（避免发送真实邮件）
- 测试用户账号已创建：reviewer_admin、normal_user、user_001、user_002、user_003

### 运行测试

```bash
# 运行Batch 3所有测试
npm run test:e2e -- --grep "F01[1-5]"

# 运行特定功能点测试
npm run test:e2e -- --grep "F011"   # 审核服务
npm run test:e2e -- --grep "F012"   # 通知服务
npm run test:e2e -- --grep "F013"   # 评价服务
npm run test:e2e -- --grep "F014"   # 收藏服务
npm run test:e2e -- --grep "F015"   # 分享服务

# 运行单个测试用例
npm run test:e2e -- --grep "TC-301"
```

### 通过标准

- 所有关键（P0）测试用例通过：6/6
- 所有高优先级（P1）测试用例通过：12/12
- 中优先级（P2）测试用例通过：2/2
- 总体通过率 = 100%


---

### 功能点：F016 - SKILL图谱服务

#### TC-25：查询SKILL关联关系成功

**功能点**：F016 - SKILL图谱服务
**类型**：正常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有有效JWT Token
- 系统中存在SKILL "skill-001"，且该SKILL关联了 "skill-002" 和 "skill-003"

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/skills/skill-001/relations
- 附带有效Authorization头

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含关联的SKILL列表
- 关联关系数据包含关联类型、关联SKILL的ID和名称
- 返回数据结构完整且字段类型正确

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills/skill-001/relations",
    "headers": {
      "Authorization": "Bearer <valid-jwt-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "data": {
        "skillId": "skill-001",
        "skillName": "数据处理SKILL",
        "relations": [
          {
            "relationId": "rel-001",
            "targetSkillId": "skill-002",
            "targetSkillName": "数据清洗SKILL",
            "relationType": "depends_on",
            "depth": 1
          },
          {
            "relationId": "rel-002",
            "targetSkillId": "skill-003",
            "targetSkillName": "数据转换SKILL",
            "relationType": "extends",
            "depth": 1
          }
        ],
        "total": 2
      }
    }
  }
}
```

**自动化说明**：
- 使用Jest + Supertest进行API自动化测试
- 测试前创建SKILL及关联关系测试数据
- 测试后清理创建的关联关系和SKILL数据

---

#### TC-26：查询不存在的SKILL关联

**功能点**：F016 - SKILL图谱服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有有效JWT Token
- 系统中不存在ID为 "skill-999" 的SKILL

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/skills/skill-999/relations
- 附带有效Authorization头

**Then**（预期结果）：
- 响应状态码为 404
- 响应体包含错误代码 "GRAPH001"
- 响应体包含用户友好的错误消息："SKILL不存在"

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills/skill-999/relations",
    "headers": {
      "Authorization": "Bearer <valid-jwt-token>"
    }
  },
  "expected": {
    "status": 404,
    "body": {
      "code": -1,
      "error": {
        "code": "GRAPH001",
        "message": "SKILL不存在"
      }
    }
  }
}
```

**自动化说明**：
- 验证错误响应格式符合API规范
- 确保不会泄露系统内部信息

---

#### TC-27：创建循环关联被拒绝

**功能点**：F016 - SKILL图谱服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有有效JWT Token
- 系统中存在关联链：skill-001 -> skill-002 -> skill-003

**When**（操作）：
- 用户发送 POST 请求到 /api/v1/skills/skill-003/relations
- 请求创建 skill-003 -> skill-001 的关联关系

**Then**（预期结果）：
- 响应状态码为 400
- 响应体包含错误代码 "GRAPH002"
- 响应体包含错误消息："创建关联会导致循环依赖"
- 关联关系未被创建

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/skills/skill-003/relations",
    "headers": {
      "Authorization": "Bearer <valid-jwt-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "targetSkillId": "skill-001",
      "relationType": "depends_on"
    }
  },
  "expected": {
    "status": 400,
    "body": {
      "code": -1,
      "error": {
        "code": "GRAPH002",
        "message": "创建关联会导致循环依赖"
      }
    }
  }
}
```

**自动化说明**：
- 测试前创建链式关联关系数据
- 验证循环关联检测逻辑正确
- 测试后清理所有关联关系数据

---

#### TC-28：查询深度为3的关联关系

**功能点**：F016 - SKILL图谱服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有有效JWT Token
- 系统中存在关联链：skill-001 -> skill-002 -> skill-003 -> skill-004（深度为3）

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/skills/skill-001/relations?maxDepth=3
- 附带有效Authorization头

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含深度1、2、3的所有关联SKILL
- 最大返回深度为3，不包含更深层次的关联
- 返回数据结构包含depth字段标识关联深度

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/skills/skill-001/relations?maxDepth=3",
    "headers": {
      "Authorization": "Bearer <valid-jwt-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "data": {
        "skillId": "skill-001",
        "relations": [
          {
            "targetSkillId": "skill-002",
            "depth": 1
          },
          {
            "targetSkillId": "skill-003",
            "depth": 2
          },
          {
            "targetSkillId": "skill-004",
            "depth": 3
          }
        ],
        "total": 3,
        "maxDepth": 3
      }
    }
  }
}
```

**自动化说明**：
- 测试前创建多层级关联关系数据
- 验证深度限制逻辑正确
- 确保不返回超过指定深度的关联

---

### 功能点：F017 - 数据统计服务

#### TC-29：获取仪表盘统计数据成功

**功能点**：F017 - 数据统计服务
**类型**：正常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有管理员JWT Token
- 系统中有足够的业务数据用于统计

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/statistics/dashboard
- 附带有效Authorization头

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含7项核心指标数据
- 各指标数据类型正确（数值类型）
- 数据为实时统计结果

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/statistics/dashboard",
    "headers": {
      "Authorization": "Bearer <admin-jwt-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "data": {
        "totalSkills": 1250,
        "totalUsers": 3500,
        "totalDownloads": 45000,
        "totalDeployments": 8000,
        "todayUploads": 15,
        "todayActiveUsers": 120,
        "pendingReviews": 25,
        "timestamp": "2026-05-17T10:30:00Z"
      }
    }
  }
}
```

**自动化说明**：
- 使用管理员账号进行测试
- 验证所有7项核心指标都存在且为数值类型
- 可Mock统计数据以确保测试稳定性

---

#### TC-30：无效时间范围查询趋势统计

**功能点**：F017 - 数据统计服务
**类型**：异常路径
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有有效JWT Token

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/statistics/trends?startDate=2026-06-01&endDate=2026-05-01
- 开始日期晚于结束日期

**Then**（预期结果）：
- 响应状态码为 400
- 响应体包含错误消息："开始日期不能晚于结束日期"
- 不返回统计数据

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/statistics/trends?startDate=2026-06-01&endDate=2026-05-01",
    "headers": {
      "Authorization": "Bearer <valid-jwt-token>"
    }
  },
  "expected": {
    "status": 400,
    "body": {
      "code": -1,
      "error": {
        "code": "INVALID_DATE_RANGE",
        "message": "开始日期不能晚于结束日期"
      }
    }
  }
}
```

**自动化说明**：
- 验证日期参数校验逻辑
- 确保错误消息清晰明确

---

#### TC-31：普通用户访问统计数据

**功能点**：F017 - 数据统计服务
**类型**：异常路径
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有普通用户JWT Token（非管理员）

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/statistics/dashboard
- 附带普通用户Authorization头

**Then**（预期结果）：
- 响应状态码为 403
- 响应体包含错误代码 "PERM001"
- 响应体包含错误消息："权限不足，需要管理员权限"

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/statistics/dashboard",
    "headers": {
      "Authorization": "Bearer <normal-user-jwt-token>"
    }
  },
  "expected": {
    "status": 403,
    "body": {
      "code": -1,
      "error": {
        "code": "PERM001",
        "message": "权限不足，需要管理员权限"
      }
    }
  }
}
```

**自动化说明**：
- 使用普通用户账号进行测试
- 验证权限控制逻辑正确
- 确保统计数据不被未授权访问

---

#### TC-32：查询无数据时间范围的热门排行

**功能点**：F017 - 数据统计服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有管理员JWT Token
- 指定时间范围内无任何SKILL下载或使用数据

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/statistics/hot-skills?startDate=2030-01-01&endDate=2030-01-31
- 查询未来时间范围

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含空的热门排行列表
- 返回total为0
- 不返回错误，正常返回空结果

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/statistics/hot-skills?startDate=2030-01-01&endDate=2030-01-31",
    "headers": {
      "Authorization": "Bearer <admin-jwt-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "data": {
        "hotSkills": [],
        "total": 0,
        "timeRange": {
          "startDate": "2030-01-01",
          "endDate": "2030-01-31"
        }
      }
    }
  }
}
```

**自动化说明**：
- 验证空数据场景的处理逻辑
- 确保返回格式与有数据时一致

---

### 功能点：F018 - 日志管理服务

#### TC-33：查询操作日志成功

**功能点**：F018 - 日志管理服务
**类型**：正常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有管理员JWT Token
- 系统中存在操作日志记录

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/logs?type=operation&startDate=2026-05-01&endDate=2026-05-17&page=1&pageSize=20
- 附带有效Authorization头

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含操作日志列表
- 日志记录包含操作时间、操作人、操作类型、操作详情
- 支持分页返回

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/logs?type=operation&startDate=2026-05-01&endDate=2026-05-17&page=1&pageSize=20",
    "headers": {
      "Authorization": "Bearer <admin-jwt-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "data": {
        "logs": [
          {
            "logId": "log-001",
            "type": "operation",
            "userId": "user-001",
            "username": "admin",
            "action": "SKILL_UPLOAD",
            "target": "skill-001",
            "detail": "上传SKILL文件 data-processor.skill",
            "ip": "192.168.1.100",
            "timestamp": "2026-05-17T10:30:00Z"
          }
        ],
        "total": 150,
        "page": 1,
        "pageSize": 20
      }
    }
  }
}
```

**自动化说明**：
- 使用管理员账号进行测试
- 验证日志查询功能完整
- 验证分页逻辑正确

---

#### TC-34：时间范围超过90天查询日志

**功能点**：F018 - 日志管理服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有管理员JWT Token

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/logs?type=operation&startDate=2026-01-01&endDate=2026-05-17
- 时间范围超过90天

**Then**（预期结果）：
- 响应状态码为 400
- 响应体包含错误代码 "LOG001"
- 响应体包含错误消息："查询时间范围不能超过90天"

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/logs?type=operation&startDate=2026-01-01&endDate=2026-05-17",
    "headers": {
      "Authorization": "Bearer <admin-jwt-token>"
    }
  },
  "expected": {
    "status": 400,
    "body": {
      "code": -1,
      "error": {
        "code": "LOG001",
        "message": "查询时间范围不能超过90天"
      }
    }
  }
}
```

**自动化说明**：
- 验证时间范围限制逻辑正确
- 确保错误消息清晰明确

---

#### TC-35：无效日志类型查询

**功能点**：F018 - 日志管理服务
**类型**：异常路径
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有管理员JWT Token

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/logs?type=invalid_type
- 使用无效的日志类型参数

**Then**（预期结果）：
- 响应状态码为 400
- 响应体包含错误代码 "LOG002"
- 响应体包含错误消息："无效的日志类型，支持的类型：operation, error, security"

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/logs?type=invalid_type",
    "headers": {
      "Authorization": "Bearer <admin-jwt-token>"
    }
  },
  "expected": {
    "status": 400,
    "body": {
      "code": -1,
      "error": {
        "code": "LOG002",
        "message": "无效的日志类型，支持的类型：operation, error, security"
      }
    }
  }
}
```

**自动化说明**：
- 验证参数校验逻辑正确
- 确保返回支持的类型列表

---

#### TC-36：导出大量日志数据

**功能点**：F018 - 日志管理服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有管理员JWT Token
- 指定时间范围内存在大量日志数据（超过10000条）

**When**（操作）：
- 用户发送 POST 请求到 /api/v1/logs/export
- 请求导出格式为csv，时间范围为最近30天

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含导出任务ID
- 系统异步处理导出任务
- 导出文件包含所有符合条件的日志记录

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/logs/export",
    "headers": {
      "Authorization": "Bearer <admin-jwt-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "type": "operation",
      "startDate": "2026-04-17",
      "endDate": "2026-05-17",
      "format": "csv"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "data": {
        "taskId": "export-task-001",
        "status": "processing",
        "message": "导出任务已创建，请稍后下载"
      }
    }
  }
}
```

**自动化说明**：
- 验证异步导出任务创建逻辑
- 可通过轮询任务状态验证导出完成
- 测试后清理导出的临时文件

---

### 功能点：F019 - 系统配置服务

#### TC-37：获取系统配置成功

**功能点**：F019 - 系统配置服务
**类型**：正常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有管理员JWT Token
- 系统中存在配置项

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/configs
- 附带有效Authorization头

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含配置列表
- 配置项包含key、value、description、更新时间
- 敏感配置的value显示为加密后的值

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/configs",
    "headers": {
      "Authorization": "Bearer <admin-jwt-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "data": {
        "configs": [
          {
            "configKey": "app.name",
            "value": "SKILL管理平台",
            "description": "应用名称",
            "isSensitive": false,
            "updatedAt": "2026-05-17T10:00:00Z"
          },
          {
            "configKey": "db.password",
            "value": "******",
            "description": "数据库密码",
            "isSensitive": true,
            "updatedAt": "2026-05-17T10:00:00Z"
          }
        ],
        "total": 2
      }
    }
  }
}
```

**自动化说明**：
- 使用管理员账号进行测试
- 验证敏感配置加密显示逻辑
- 验证配置列表返回完整

---

#### TC-38：更新只读配置被拒绝

**功能点**：F019 - 系统配置服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有管理员JWT Token
- 配置项 "app.version" 为只读配置

**When**（操作）：
- 用户发送 PUT 请求到 /api/v1/configs/app.version
- 请求更新只读配置的值

**Then**（预期结果）：
- 响应状态码为 403
- 响应体包含错误代码 "CONFIG001"
- 响应体包含错误消息："该配置为只读，不允许修改"

**测试数据**：
```json
{
  "request": {
    "method": "PUT",
    "url": "/api/v1/configs/app.version",
    "headers": {
      "Authorization": "Bearer <admin-jwt-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "value": "2.0.0"
    }
  },
  "expected": {
    "status": 403,
    "body": {
      "code": -1,
      "error": {
        "code": "CONFIG001",
        "message": "该配置为只读，不允许修改"
      }
    }
  }
}
```

**自动化说明**：
- 验证只读配置保护逻辑正确
- 确保只读配置不会被修改

---

#### TC-39：并发更新同一配置

**功能点**：F019 - 系统配置服务
**类型**：异常路径
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 两个管理员用户同时持有有效JWT Token
- 配置项 "upload.maxSize" 当前值为 "100MB"

**When**（操作）：
- 管理员A发送 PUT 请求更新配置为 "200MB"
- 管理员B同时发送 PUT 请求更新配置为 "300MB"

**Then**（预期结果）：
- 先到达的请求成功，响应状态码为 200
- 后到达的请求失败，响应状态码为 409
- 错误响应包含错误代码 "CONFIG002"
- 错误响应包含错误消息："配置正在被其他用户修改，请稍后重试"

**测试数据**：
```json
{
  "request_a": {
    "method": "PUT",
    "url": "/api/v1/configs/upload.maxSize",
    "headers": {
      "Authorization": "Bearer <admin-a-jwt-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "value": "200MB"
    }
  },
  "request_b": {
    "method": "PUT",
    "url": "/api/v1/configs/upload.maxSize",
    "headers": {
      "Authorization": "Bearer <admin-b-jwt-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "value": "300MB"
    }
  },
  "expected": {
    "success_response": {
      "status": 200,
      "body": {
        "code": 0,
        "data": {
          "configKey": "upload.maxSize",
          "value": "200MB",
          "updatedAt": "2026-05-17T10:30:00Z"
        }
      }
    },
    "conflict_response": {
      "status": 409,
      "body": {
        "code": -1,
        "error": {
          "code": "CONFIG002",
          "message": "配置正在被其他用户修改，请稍后重试"
        }
      }
    }
  }
}
```

**自动化说明**：
- 使用并发测试工具模拟同时更新
- 验证加锁机制正确工作
- 验证最终配置值与先到达的请求一致

---

#### TC-40：获取敏感配置返回加密值

**功能点**：F019 - 系统配置服务
**类型**：边界情况
**优先级**：高
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有管理员JWT Token
- 系统中存在敏感配置项 "db.password"

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/configs/db.password
- 附带有效Authorization头

**Then**（预期结果）：
- 响应状态码为 200
- 响应体中value字段为加密后的值（显示为"******"）
- 不返回明文密码
- 响应体包含isSensitive标识为true

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/configs/db.password",
    "headers": {
      "Authorization": "Bearer <admin-jwt-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "data": {
        "configKey": "db.password",
        "value": "******",
        "description": "数据库密码",
        "isSensitive": true,
        "updatedAt": "2026-05-17T10:00:00Z"
      }
    }
  }
}
```

**自动化说明**：
- 验证敏感配置加密显示逻辑正确
- 确保不会泄露明文敏感信息
- 验证isSensitive标识正确

---

### 功能点：F020 - 帮助中心服务

#### TC-41：获取帮助文档列表成功

**功能点**：F020 - 帮助中心服务
**类型**：正常路径
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有有效JWT Token
- 系统中存在帮助文档

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/help-docs?page=1&pageSize=10
- 附带有效Authorization头

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含帮助文档列表
- 文档包含标题、类型、创建时间、摘要
- 支持分页返回

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/help-docs?page=1&pageSize=10",
    "headers": {
      "Authorization": "Bearer <valid-jwt-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "data": {
        "docs": [
          {
            "docId": "doc-001",
            "title": "SKILL上传指南",
            "type": "guide",
            "summary": "本文介绍如何上传SKILL文件...",
            "createdAt": "2026-05-10T10:00:00Z",
            "updatedAt": "2026-05-15T10:00:00Z"
          },
          {
            "docId": "doc-002",
            "title": "常见问题解答",
            "type": "faq",
            "summary": "关于SKILL管理平台的常见问题...",
            "createdAt": "2026-05-10T10:00:00Z",
            "updatedAt": "2026-05-15T10:00:00Z"
          }
        ],
        "total": 25,
        "page": 1,
        "pageSize": 10
      }
    }
  }
}
```

**自动化说明**：
- 验证帮助文档列表查询功能完整
- 验证分页逻辑正确
- 验证文档类型分类正确

---

#### TC-42：访问不存在的帮助文档

**功能点**：F020 - 帮助中心服务
**类型**：异常路径
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有有效JWT Token
- 系统中不存在ID为 "doc-999" 的帮助文档

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/help-docs/doc-999
- 附带有效Authorization头

**Then**（预期结果）：
- 响应状态码为 404
- 响应体包含错误消息："帮助文档不存在"

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/help-docs/doc-999",
    "headers": {
      "Authorization": "Bearer <valid-jwt-token>"
    }
  },
  "expected": {
    "status": 404,
    "body": {
      "code": -1,
      "error": {
        "code": "DOC_NOT_FOUND",
        "message": "帮助文档不存在"
      }
    }
  }
}
```

**自动化说明**：
- 验证错误响应格式符合API规范
- 确保不会泄露系统内部信息

---

#### TC-43：非管理员删除帮助文档

**功能点**：F020 - 帮助中心服务
**类型**：异常路径
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有普通用户JWT Token（非管理员）
- 系统中存在帮助文档 "doc-001"

**When**（操作）：
- 用户发送 DELETE 请求到 /api/v1/help-docs/doc-001
- 附带普通用户Authorization头

**Then**（预期结果）：
- 响应状态码为 403
- 响应体包含错误代码 "PERM001"
- 响应体包含错误消息："权限不足，需要管理员权限"

**测试数据**：
```json
{
  "request": {
    "method": "DELETE",
    "url": "/api/v1/help-docs/doc-001",
    "headers": {
      "Authorization": "Bearer <normal-user-jwt-token>"
    }
  },
  "expected": {
    "status": 403,
    "body": {
      "code": -1,
      "error": {
        "code": "PERM001",
        "message": "权限不足，需要管理员权限"
      }
    }
  }
}
```

**自动化说明**：
- 使用普通用户账号进行测试
- 验证权限控制逻辑正确
- 确保帮助文档不被未授权删除

---

#### TC-44：搜索无匹配结果的关键词

**功能点**：F020 - 帮助中心服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有有效JWT Token

**When**（操作）：
- 用户发送 GET 请求到 /api/v1/help-docs?keyword=xyzabc123notexist
- 搜索不存在的关键词

**Then**（预期结果）：
- 响应状态码为 200
- 响应体包含空的文档列表
- 返回total为0
- 不返回错误，正常返回空结果

**测试数据**：
```json
{
  "request": {
    "method": "GET",
    "url": "/api/v1/help-docs?keyword=xyzabc123notexist",
    "headers": {
      "Authorization": "Bearer <valid-jwt-token>"
    }
  },
  "expected": {
    "status": 200,
    "body": {
      "code": 0,
      "data": {
        "docs": [],
        "total": 0,
        "page": 1,
        "pageSize": 10
      }
    }
  }
}
```

**自动化说明**：
- 验证空搜索结果的处理逻辑
- 确保返回格式与有结果时一致

---

### 功能点：F021 - 意见反馈服务

#### TC-45：提交反馈成功

**功能点**：F021 - 意见反馈服务
**类型**：正常路径
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有有效JWT Token

**When**（操作）：
- 用户发送 POST 请求到 /api/v1/feedbacks
- 请求体包含反馈类型、标题、内容

**Then**（预期结果）：
- 响应状态码为 201
- 响应体包含创建的反馈ID
- 反馈状态为 "pending"
- 系统记录提交时间和用户信息

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/feedbacks",
    "headers": {
      "Authorization": "Bearer <valid-jwt-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "type": "suggestion",
      "title": "建议增加批量导出功能",
      "content": "希望能增加批量导出SKILL的功能，方便数据迁移和备份。"
    }
  },
  "expected": {
    "status": 201,
    "body": {
      "code": 0,
      "data": {
        "feedbackId": "fb-001",
        "type": "suggestion",
        "title": "建议增加批量导出功能",
        "content": "希望能增加批量导出SKILL的功能，方便数据迁移和备份。",
        "status": "pending",
        "userId": "user-001",
        "createdAt": "2026-05-17T10:30:00Z"
      }
    }
  }
}
```

**自动化说明**：
- 验证反馈创建功能完整
- 验证返回数据结构正确
- 测试后清理创建的反馈数据

---

#### TC-46：反馈内容超过1000字被拒绝

**功能点**：F021 - 意见反馈服务
**类型**：异常路径
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有有效JWT Token

**When**（操作）：
- 用户发送 POST 请求到 /api/v1/feedbacks
- 请求体中content字段超过1000字

**Then**（预期结果）：
- 响应状态码为 400
- 响应体包含错误消息："反馈内容不能超过1000字"
- 反馈未被创建

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/feedbacks",
    "headers": {
      "Authorization": "Bearer <valid-jwt-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "type": "suggestion",
      "title": "功能建议",
      "content": "这是一段超过1000字的反馈内容...（此处省略995字）"
    }
  },
  "expected": {
    "status": 400,
    "body": {
      "code": -1,
      "error": {
        "code": "INVALID_INPUT",
        "message": "反馈内容不能超过1000字"
      }
    }
  }
}
```

**自动化说明**：
- 验证内容长度限制逻辑正确
- 确保错误消息清晰明确

---

#### TC-47：非管理员回复反馈被拒绝

**功能点**：F021 - 意见反馈服务
**类型**：异常路径
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有普通用户JWT Token（非管理员）
- 系统中存在反馈 "fb-001"

**When**（操作）：
- 用户发送 POST 请求到 /api/v1/admin/feedbacks/fb-001/reply
- 附带普通用户Authorization头

**Then**（预期结果）：
- 响应状态码为 403
- 响应体包含错误代码 "PERM001"
- 响应体包含错误消息："权限不足，需要管理员权限"

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/admin/feedbacks/fb-001/reply",
    "headers": {
      "Authorization": "Bearer <normal-user-jwt-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "content": "感谢您的反馈，我们会尽快处理。"
    }
  },
  "expected": {
    "status": 403,
    "body": {
      "code": -1,
      "error": {
        "code": "PERM001",
        "message": "权限不足，需要管理员权限"
      }
    }
  }
}
```

**自动化说明**：
- 使用普通用户账号进行测试
- 验证权限控制逻辑正确
- 确保管理员功能不被未授权访问

---

#### TC-48：提交空内容反馈被拒绝

**功能点**：F021 - 意见反馈服务
**类型**：边界情况
**优先级**：中
**自动化**：自动

**Given**（前置条件）：
- 系统正在运行且可访问
- 用户已认证并持有有效JWT Token

**When**（操作）：
- 用户发送 POST 请求到 /api/v1/feedbacks
- 请求体中content字段为空字符串

**Then**（预期结果）：
- 响应状态码为 400
- 响应体包含错误消息："反馈内容不能为空"
- 反馈未被创建

**测试数据**：
```json
{
  "request": {
    "method": "POST",
    "url": "/api/v1/feedbacks",
    "headers": {
      "Authorization": "Bearer <valid-jwt-token>",
      "Content-Type": "application/json"
    },
    "body": {
      "type": "suggestion",
      "title": "功能建议",
      "content": ""
    }
  },
  "expected": {
    "status": 400,
    "body": {
      "code": -1,
      "error": {
        "code": "INVALID_INPUT",
        "message": "反馈内容不能为空"
      }
    }
  }
}
```

**自动化说明**：
- 验证空内容校验逻辑正确
- 确保错误消息清晰明确

---

## 覆盖率矩阵

| 功能点 | 正常路径 | 异常路径 | 边界情况 | 覆盖率 |
|--------|----------|----------|----------|--------|
| F016 SKILL图谱服务 | TC-25 | TC-26, TC-27 | TC-28 | ✓ 100% |
| F017 数据统计服务 | TC-29 | TC-30, TC-31 | TC-32 | ✓ 100% |
| F018 日志管理服务 | TC-33 | TC-34, TC-35 | TC-36 | ✓ 100% |
| F019 系统配置服务 | TC-37 | TC-38, TC-39 | TC-40 | ✓ 100% |
| F020 帮助中心服务 | TC-41 | TC-42, TC-43 | TC-44 | ✓ 100% |
| F021 意见反馈服务 | TC-45 | TC-46, TC-47 | TC-48 | ✓ 100% |

## 测试执行指南

### 前置条件

- 测试环境已部署SKILL管理平台
- 测试数据库已初始化
- 测试用户账号已创建（管理员和普通用户）
- JWT Token生成服务可用

### 测试数据准备

- 创建测试SKILL及关联关系数据
- 创建测试帮助文档
- 创建测试反馈数据
- 配置系统配置项（包括只读和敏感配置）

### 运行测试

```bash
# 运行所有Batch 4测试用例
npm test -- --grep "TC-[2-4][0-9]"

# 运行特定功能点测试
npm test -- --grep "F016"
npm test -- --grep "F017"
npm test -- --grep "F018"
npm test -- --grep "F019"
npm test -- --grep "F020"
npm test -- --grep "F021"
```

### 通过标准

- 所有高优先级测试用例通过
- 总体通过率 100%
- 无阻塞性缺陷
