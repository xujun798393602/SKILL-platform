# 模块依赖图

## 模块依赖关系

```
skill-common    --> (无依赖，基础工具层)
skill-auth      --> skill-common
skill-core      --> skill-common, skill-auth
skill-review    --> skill-common, skill-auth, skill-core, skill-social
skill-deploy    --> skill-common, skill-auth, skill-core
skill-suite     --> skill-common, skill-auth, skill-core, skill-graph
skill-social    --> skill-common, skill-auth, skill-core
skill-graph     --> skill-common, skill-auth
skill-stats     --> skill-common, skill-auth
skill-notify    --> skill-common, skill-auth
skill-admin     --> skill-common, skill-auth
skill-app       --> 所有模块（组装层）
```

## 共享实体清单（单一来源原则）

| 实体 | 定义模块 | 完整类名 | 引用模块 |
|------|---------|---------|---------|
| User | skill-auth | com.skill.platform.auth.model.User | skill-core, skill-social, skill-deploy, skill-admin, skill-notify, skill-suite |
| Role | skill-auth | com.skill.platform.auth.model.Role | (内部) |
| Permission | skill-auth | com.skill.platform.auth.model.Permission | (内部) |
| UserRole | skill-auth | com.skill.platform.auth.model.UserRole | (内部) |
| RolePermission | skill-auth | com.skill.platform.auth.model.RolePermission | (内部) |
| Skill | skill-core | com.skill.platform.core.model.Skill | skill-social, skill-deploy, skill-graph, skill-suite |
| Tag | skill-core | com.skill.platform.core.model.Tag | (内部) |
| SkillTag | skill-core | com.skill.platform.core.model.SkillTag | (内部) |
| SkillFile | skill-core | com.skill.platform.core.model.SkillFile | (内部) |
| SkillVersion | skill-core | com.skill.platform.core.model.SkillVersion | (内部) |
| DownloadLog | skill-core | com.skill.platform.core.model.DownloadLog | (内部) |
| SkillReview | skill-social | com.skill.platform.social.model.SkillReview | (内部) |
| SkillRating | skill-social | com.skill.platform.social.model.SkillRating | (内部) |
| SkillComment | skill-social | com.skill.platform.social.model.SkillComment | (内部) |
| SkillFavorite | skill-social | com.skill.platform.social.model.SkillFavorite | (内部) |
| SkillShare | skill-social | com.skill.platform.social.model.SkillShare | (内部) |
| Suite | skill-suite | com.skill.platform.suite.model.Suite | (内部) |
| SuiteSkill | skill-suite | com.skill.platform.suite.model.SuiteSkill | (内部) |
| Deployment | skill-deploy | com.skill.platform.deploy.model.Deployment | (内部) |
| SkillRelation | skill-graph | com.skill.platform.graph.model.SkillRelation | (内部) |
| PositionSkill | skill-graph | com.skill.platform.graph.model.PositionSkill | (内部) |
| Notification | skill-notify | com.skill.platform.notify.model.Notification | (内部) |
| OperationLog | skill-admin | com.skill.platform.admin.model.OperationLog | (内部) |
| HelpDoc | skill-admin | com.skill.platform.admin.model.HelpDoc | (内部) |
| Feedback | skill-admin | com.skill.platform.admin.model.Feedback | (内部) |
| SystemConfig | skill-admin | com.skill.platform.admin.model.SystemConfig | (内部) |

## 共享服务清单

| 服务 | 定义模块 | 完整类名 |
|------|---------|---------|
| FileStorageService | skill-common | com.skill.platform.common.service.FileStorageService |
| UserContext | skill-common | com.skill.platform.common.util.UserContext |
| ApiResponse | skill-common | com.skill.platform.common.response.ApiResponse |
| PageResponse | skill-common | com.skill.platform.common.response.PageResponse |
| BusinessException | skill-common | com.skill.platform.common.exception.BusinessException |
| ErrorCode | skill-common | com.skill.platform.common.exception.ErrorCode |
| GlobalExceptionHandler | skill-common | com.skill.platform.common.exception.GlobalExceptionHandler |
| JwtService | skill-auth | com.skill.platform.auth.security.JwtService |
| PasswordService | skill-auth | com.skill.platform.auth.security.PasswordService |
| PermissionService | skill-auth | com.skill.platform.auth.security.PermissionService |

## 关键规则

1. **User 实体只能在 skill-auth 中定义**，其他模块通过 `import com.skill.platform.auth.model.User` 引用
2. **Skill 实体只能在 skill-core 中定义**，其他模块通过 `import com.skill.platform.core.model.Skill` 引用
3. **不允许跨模块重复定义 @Entity 类**
4. **import 路径必须使用上表中的完整类名**

## 已完成模块状态

| 模块 | Model | Repository | Service | Controller | 单测 |
|------|-------|-----------|---------|------------|------|
| skill-common | - | - | FileStorageService | - | - |
| skill-auth | 5 | 5 | AuthService | AuthController | 0 |
| skill-core | 7 | 6 | Upload/Download | Upload/Download | 0 |
| skill-review | 0 | 0 | 0 | 0 | 0 |
| skill-deploy | 1 | 1 | 0 | 0 | 0 |
| skill-suite | 2 | 2 | 0 | 0 | 0 |
| skill-social | 5 | 5 | 0 | 0 | 0 |
| skill-graph | 2 | 2 | 0 | 0 | 0 |
| skill-stats | 0 | 0 | 0 | 0 | 0 |
| skill-notify | 1 | 1 | 0 | 0 | 0 |
| skill-admin | 4 | 4 | 0 | 0 | 0 |
| skill-app | - | - | - | - | 3(base) |
