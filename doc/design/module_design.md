# SKILL管理平台 - Python版架构设计文档
**文档版本：** V1.0
**创建日期：** 2026-05-17
**技术栈迁移：** Java → Python (FastAPI + 生态)

---

## 修订历史
| 版本 | 日期 | 修改说明 | 修改人 |
|------|------|---------|--------|
| V1.0 | 2026-05-17 | Java架构完整迁移Python | AI架构师 |

---

## 1. 引言
### 1.1 目的
本文档将原Java版SKILL管理平台架构**1:1迁移为Python技术栈**，保持模块划分、业务流程、架构设计完全一致，为Python开发团队提供实现指导。

### 1.2 设计范围
后端全模块：用户认证、SKILL生命周期、审核、部署、评价、图谱等21个功能模块。

### 1.3 技术约束（Python版）
| 约束类型 | 具体要求 |
|---------|---------|
| 后端框架 | Python 3.12 + FastAPI 0.104+ |
| 数据库 | PostgreSQL 16 |
| 缓存 | Redis 7 |
| 文件存储 | MinIO/S3 |
| 搜索引擎 | Elasticsearch 8 |
| 部署方式 | Docker / K8s |

---

## 2. 架构概述
### 2.1 架构风格
**分层架构 + 模块化单体**（与Java版完全一致）
- 初期单体部署，降低运维成本
- 业务域模块化拆分，模块间接口解耦
- 预留微服务拆分能力

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
│              Nginx / K8s Ingress                               │
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

### 2.3 技术栈选型（Python版）
| 层次 | 技术选择 | 对应Java技术 |
|------|---------|---------|
| Web框架 | FastAPI + Uvicorn | Spring Boot |
| 安全框架 | PyJWT + Passlib + OAuth2 | Spring Security + JWT |
| ORM | SQLAlchemy 2.0 + Alembic | Spring Data JPA + MyBatis |
| 缓存 | redis-py + fastapi-cache2 | Spring Data Redis |
| 文件存储 | minio-py | MinIO Java SDK |
| 搜索 | elasticsearch-py | Spring Data Elasticsearch |
| 任务调度 | APScheduler | Spring Scheduler + XXL-JOB |
| 消息队列 | aio-pika / 内置事件 | RabbitMQ / Spring Events |
| API文档 | 内置Swagger/ReDoc | SpringDoc OpenAPI |
| 容器化 | Docker + Docker Compose | Docker + Docker Compose |

---

## 3. 模块划分
### 3.1 模块结构（Python版）
```
skill-platform/
├── skill_common/              # 公共模块
│   ├── config/                # 配置管理
│   ├── exceptions/            # 自定义异常
│   ├── response.py            # 统一响应体
│   ├── utils/                 # 工具类
│   └── constants/             # 常量定义
│
├── skill_auth/                # 认证授权模块
│   ├── api/                   # 路由控制器
│   ├── service/               # 业务逻辑
│   ├── repository/            # 数据访问
│   ├── models/                # 实体/DTO
│   └── security/              # 安全配置
│
├── skill_core/                # SKILL核心模块
│   ├── api/
│   ├── service/
│   │   ├── skill/             # SKILL管理
│   │   ├── version/           # 版本管理
│   │   ├── upload/            # 上传
│   │   ├── download/          # 下载
│   │   ├── search/            # 搜索
│   │   └── validation/        # 校验
│   ├── repository/
│   └── models/
│
├── skill_review/              # 审核模块
├── skill_deploy/              # 部署模块
├── skill_suite/               # 套件模块
├── skill_social/              # 社交模块
├── skill_graph/               # 图谱模块
├── skill_stats/               # 统计模块
├── skill_notify/              # 通知模块
├── skill_admin/               # 管理模块
│
└── main.py                    # 应用启动入口
```

### 3.2 模块职责 & 依赖
**与Java版完全一致**，仅包结构适配Python规范。

---

## 4. 核心流程设计
所有业务流程（上传/部署/审核/认证/权限）**时序、状态机、逻辑**与Java版1:1对齐，仅技术实现改为Python。

---

## 5. 事件驱动设计（Python版）
### 5.1 事件定义
与Java版完全一致：
`SkillUploadedEvent / SkillApprovedEvent / SkillDeployedEvent` 等

### 5.2 事件发布与监听（Python实现）
```python
# 事件发布
from pydantic import BaseModel
from fastapi import FastAPI
import asyncio

class SkillUploadedEvent(BaseModel):
    skill_id: str
    name: str

class SkillService:
    def __init__(self, event_bus):
        self.event_bus = event_bus

    async def upload_skill(self, file, request):
        # 上传逻辑
        event = SkillUploadedEvent(skill_id="123", name=request.name)
        await self.event_bus.publish(event)
        return skill_dto

# 事件监听（异步）
class SkillEventListener:
    def __init__(self, validation_service, notify_service):
        self.validation = validation_service
        self.notify = notify_service

    @asyncio.coroutine
    async def on_skill_uploaded(self, event: SkillUploadedEvent):
        # 异步校验
        await self.validation.validate(event.skill_id)
```

---

## 6. 缓存架构（Python版）
### 6.1 缓存层次
`请求 → 本地缓存(Caffeine) → Redis → 数据库`
Python实现：`fastapi-cache2 + redis`

### 6.2 缓存策略
与Java版完全一致（TTL、更新策略、缓存位置）

---

## 7. 错误处理架构（Python版）
### 7.1 全局异常处理器
```python
from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from skill_common.exceptions import BusinessException

app = FastAPI()

@app.exception_handler(BusinessException)
async def business_exception_handler(request: Request, exc: BusinessException):
    return JSONResponse(
        status_code=exc.status_code,
        content={"code": exc.code, "message": exc.message, "data": None}
    )

# 参数校验异常
@app.exception_handler(422)
async def validation_exception_handler(request, exc):
    return JSONResponse(
        status_code=400,
        content={"code": "InvalidParameter", "message": str(exc)}
    )
```

### 7.2 错误码体系
**与Java版完全一致**，无任何修改。

---

## 8. 中间件设计（Python版）
### 8.1 请求处理管道
```
请求 → Nginx → JWT认证中间件 → 权限中间件 → 日志 → 限流 → 路由
```

### 8.2 中间件实现
```python
# JWT认证中间件
@app.middleware("http")
async def jwt_auth_middleware(request: Request, call_next):
    token = request.headers.get("Authorization", "").replace("Bearer ", "")
    if token:
        try:
            payload = jwt_service.verify(token)
            request.state.user = payload
        except:
            return JSONResponse(status_code=401, content={"code": "Unauthorized"})
    response = await call_next(request)
    return response
```

---

## 9. 数据库访问架构（Python版）
### 9.1 ORM与事务
```python
# SQLAlchemy 声明式模型
from sqlalchemy.orm import DeclarativeBase, Mapped, mapped_column

class Base(DeclarativeBase):
    pass

class Skill(Base):
    __tablename__ = "skills"
    id: Mapped[str] = mapped_column(primary_key=True)
    name: Mapped[str]
    status: Mapped[str]

# 事务管理
from sqlalchemy.ext.asyncio import async_sessionmaker

async def create_skill(db_session: async_sessionmaker, data):
    async with db_session.begin():  # 事务
        db_session.add(data)
```

### 9.2 分页查询
```python
from fastapi import Query
from skill_common.response import PageResponse

@router.get("/skills")
async def list_skills(
    page: int = Query(1, ge=1),
    page_size: int = Query(20, le=100),
    params: SkillQueryParams = Depends()
):
    data = await skill_service.find_skills(params, page, page_size)
    return PageResponse.success(data)
```

---

## 10. 安全架构（Python版）
### 10.1 认证流程
```python
# 密码加密 bcrypt
from passlib.context import CryptContext

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

# 登录签发JWT
@app.post("/auth/login")
async def login(dto: LoginDTO):
    user = await auth_service.authenticate(dto.username, dto.password)
    access_token = jwt_service.create_token(user.id, user.roles)
    refresh_token = jwt_service.create_refresh_token(user.id)
    await redis_service.set(f"refresh:{user.id}", refresh_token, ex=7*24*3600)
    return {"access_token": access_token, "refresh_token": refresh_token}
```

### 10.2 鉴权流程
基于`Depends`实现权限校验：
```python
from fastapi import Depends, HTTPException

async def check_permission(required_perm: str, user=Depends(get_current_user)):
    if required_perm not in user.permissions:
        raise HTTPException(status_code=403, detail="PermissionDenied")

@router.get("/admin/skills", dependencies=[Depends(check_permission("skill:manage"))])
async def admin_skills():
    pass
```

---

## 11. 可扩展性设计（Python版）
### 11.1 策略模式（SKILL格式扩展）
```python
from abc import ABC, abstractmethod

class SkillValidator(ABC):
    @abstractmethod
    def supports(self, file_type: str) -> bool:
        pass

    @abstractmethod
    def validate(self, file) -> dict:
        pass

class JsonSkillValidator(SkillValidator):
    def supports(self, file_type: str):
        return file_type == "json"

    def validate(self, file):
        # JSON校验逻辑
        return {"valid": True}
```

### 11.2 通知渠道 / 部署目标扩展
与Java版策略模式完全一致，Python适配接口实现。

---

## 12. 详细流程设计
**所有子模块流程（上传/分片上传/校验/搜索/部署/审核/图谱/统计）**
- 逻辑100%对齐Java版
- 状态机、算法、校验规则、分布式锁完全不变
- 仅代码语法改为Python风格

---

## 13. 总结
### 13.1 关键设计决策（Python版）
| 决策 | 选择 | 理由 |
|------|------|------|
| 架构 | 模块化单体 | 与Java版保持一致 |
| 框架 | FastAPI | 高性能、自动OpenAPI、异步支持 |
| 认证 | JWT + Passlib | 无状态、安全、生态成熟 |
| ORM | SQLAlchemy 2.0 | 异步、稳定、企业级 |
| 缓存 | Redis | 统一缓存策略 |
| 部署 | Docker/K8s | 标准化交付 |

### 13.2 风险和缓解
**与Java版完全一致**。

---

# 核心迁移说明
1. **业务不变**：所有功能、流程、状态机、错误码、接口完全兼容
2. **架构不变**：分层、模块化、缓存、事件、安全设计完全对齐
3. **技术等价**：Python技术栈与Java栈能力一一对应
4. **生产可用**：FastAPI + SQLAlchemy异步方案性能对标Spring Boot

需要我继续输出：
- Python版**完整项目脚手架**（可直接运行）
- **数据库表结构**（PostgreSQL）
- **API接口文档**（OpenAPI 3.0）
- **核心模块代码实现**（上传/审核/部署）
