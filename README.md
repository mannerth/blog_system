# Blog System（潮汐 Blue Green Blog）

前后端分离的博客系统。前端 Vue 3 + Vite SPA，后端 Spring Boot 4.0.5 API。

## 环境要求

- **前端**：Node.js `^20.19.0` 或 `>=22.12.0`，pnpm
- **后端**：JDK 17，Gradle（已含 wrapper，无需手动安装）
- **数据库**：MySQL（数据库名 `blog_system`，端口 3306）

## 快速开始

### 1. 克隆仓库

```bash
git clone <repo-url> && cd blog_system
```

### 2. 配置后端环境变量

在 `blog_system_backend/.env` 中填写：

```env
SERVER_IP=你的MySQL主机IP
DB_USERNAME=数据库用户名
DB_PASSWORD=数据库密码
SERVER_PORT=8084
JWT_SECRET=至少32字符的密钥
ADMIN_USERNAME=admin
ADMIN_PASSWORD=123456
```

设值 `ADMIN_USERNAME` + `ADMIN_PASSWORD` 后，首次启动会自动创建管理员账号。

### 3. 启动后端

```bash
cd blog_system_backend
./gradlew bootRun
```

服务运行在 `http://localhost:8084`，数据库表结构由 JPA 自动创建（`ddl-auto: update`）。

### 4. 启动前端

```bash
cd blog_system_frontend
pnpm install
pnpm dev
```

开发服务器运行在 `http://localhost:5173`，API 请求自动代理到后端 8084 端口。

## 项目结构

```
blog_system/
├── AGENTS.md                     # AI 助手指令文件
├── TASKS.md                      # 前端任务清单
├── blog_system_frontend/         # 前端项目
│   ├── src/
│   │   ├── api/                  # API 封装（blogs / auth / comments / categories / tags / likes / users）
│   │   ├── assets/               # 全局样式与资源
│   │   ├── components/           # 组件
│   │   │   ├── base/             # 基础 UI 组件（Button / Input / Modal / Pagination 等）
│   │   │   └── *.vue             # 业务组件（BlogCard / CommentList / MyQuillEditor 等）
│   │   ├── layouts/              # 布局（AppLayout 前台 / AdminLayout 后台）
│   │   ├── router/               # 路由配置 + 守卫
│   │   ├── stores/               # Pinia 状态管理（auth）
│   │   ├── utils/                # 工具（http 客户端 / auth token / auth 事件）
│   │   └── views/                # 页面视图
│   ├── env.d.ts                  # 环境变量类型声明
│   └── vite.config.ts            # Vite 配置（含 /api 代理）
└── blog_system_backend/          # 后端项目
    ├── .env                      # 环境变量（DB / JWT / 管理员）
    ├── build.gradle              # Gradle 构建（Spring Boot 4.0.5 + Java 17）
    └── src/main/java/com/example/blog_system_backend/
        ├── admin/                # 管理端控制器与服务
        ├── auth/                 # 认证（登录/注册/JWT）
        ├── blog/                 # 博客实体、控制器、服务
        ├── category/             # 分类实体、控制器、服务
        ├── comment/              # 评论实体、控制器、服务（支持楼中楼）
        ├── common/               # 全局异常处理与错误响应
        ├── config/               # SecurityConfig + AdminInitializer
        ├── security/             # JWT Filter / Service / UserDetailsService
        ├── tag/                  # 标签实体、控制器、服务
        └── user/                 # 用户实体、控制器、服务
```

## 常用命令

### 后端

| 命令 | 说明 |
|---|---|
| `./gradlew bootRun` | 启动开发服务器 |
| `./gradlew test` | 运行测试 |
| `./gradlew build` | 完整构建 |

后端启动后 Spring Data REST 自动暴露 API，应用日志中可查看接口路径。

### 前端

| 命令 | 说明 |
|---|---|
| `pnpm dev` | 启动 Vite 开发服务器 |
| `pnpm build` | 类型检查 + 生产构建 |
| `pnpm type-check` | 仅运行类型检查（vue-tsc） |

## 环境变量

### 后端（`blog_system_backend/.env`）

| 变量 | 说明 | 默认值 |
|---|---|---|
| `SERVER_IP` | MySQL 主机 IP | 无（必填） |
| `DB_USERNAME` | 数据库用户名 | 无（必填） |
| `DB_PASSWORD` | 数据库密码 | 无（必填） |
| `SERVER_PORT` | 后端服务端口 | `8080` |
| `JWT_SECRET` | JWT 签名密钥 | 不安全默认值 |
| `JWT_EXPIRATION_MS` | JWT 过期时间（毫秒） | `86400000`（24h） |
| `ADMIN_USERNAME` | 初始管理员用户名 | 无 |
| `ADMIN_PASSWORD` | 初始管理员密码 | 无 |

### 前端（`.env.development` / `.env.production`）

| 变量 | 说明 | 默认值 |
|---|---|---|
| `VITE_API_BASE_URL` | API 基础路径 | `/api` |
| `VITE_API_TIMEOUT` | 请求超时时间（毫秒） | `15000` |
| `VITE_API_PROXY_TARGET` | Vite 开发代理目标 | `http://localhost:8084` |

## 技术栈

### 前端
- Vue 3（Composition API + `<script setup>`）
- Vite 8 + TypeScript 6
- Vue Router 5 + Pinia 3
- Quill 2（Delta 格式富文本编辑器）
- highlight.js（代码高亮）

### 后端
- Spring Boot 4.0.5 + Spring Security
- Spring Data JPA（Hibernate）
- MySQL + HikariCP 连接池
- jjwt 0.12.6（JWT 认证）
- BCrypt 密码加密

## API 概览

所有 API 路径以 `/api` 开头。完整端点列表见 `AGENTS.md`。

| 控制器 | 路径 | 权限 |
|---|---|---|
| AuthController | `/api/auth` | 公开 |
| BlogController | `/api/blogs` | 公开（写操作需认证） |
| CategoryController | `/api/categories` | 公开（写操作需认证） |
| TagController | `/api/tags` | 公开（写操作需认证） |
| CommentController | `/api/comments` | 公开（写操作需认证） |
| UserController | `/api/users` | 个人中心需认证 |
| AdminController | `/api/admin` | 仅 ADMIN 角色 |

## 开发注意事项

- 博客编辑器使用 Quill **Delta** 格式（非 HTML），通过 `v-model:content` 双向绑定
- 前端 `page` 参数做了 zero-index 转换（`page - 1`），与后端 Spring Data Pageable 匹配
- 前端请求路径不带 `/api` 前缀（dev 时由 Vite 代理补全，prod 时 Nginx 补全）
- Token 存在 `localStorage`，key 为 `blog_system_token`
- `AuthResponse.role` 是大写枚举 `USER` / `ADMIN`
- 管理后台路由通过前端路由守卫校验 ADMIN 角色，后端仅 `/api/admin/**` 需要 ADMIN
