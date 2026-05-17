# AGENTS

**始终用中文回答用户**

## 仓库结构
- `blog_system_frontend/` — Vue 3 + Vite SPA（Pinia + Vue Router），包管理器 `pnpm`
- `blog_system_backend/` — Spring Boot 4.0.5 API（Gradle，Java 17 toolchain）
- `TASKS.md` — 前端任务清单（需同步进度）

## 前端（blog_system_frontend）

### 环境与命令
- 使用 **pnpm**（lockfile = `pnpm-lock.yaml`），Node 引擎 `^20.19.0 || >=22.12.0`
- `pnpm dev` — 启动 Vite 开发服务器
- `pnpm build` — 依次执行 `vue-tsc --build` + `vite build`（type-check 不可跳过）
- `pnpm type-check` — 单独跑类型检查
- Vite alias `@` 映射到 `blog_system_frontend/src`

### API 代理与环境变量
- `.env.development` / `.env.production` 设置 `VITE_API_BASE_URL=/api`、`VITE_API_TIMEOUT=15000`
- Vite 开发代理将 `/api/*` 转发到后端，默认目标 `http://localhost:8084`（通过 `VITE_API_PROXY_TARGET` 覆盖）
  - 注意：`.env` 中 `SERVER_PORT=8084`，所以默认代理端口是 **8084**，不是 8080

### 关键约定
- 博客编辑器 `MyQuillEditor.vue` 使用 Quill **Delta** 格式（非 HTML），通过 `v-model:content` 双向绑定
- API 封装在 `src/api/*`，通过 `src/api/index.ts` 统一导出
- `src/api/blogs.ts` / `comments.ts` / `categories.ts` / `likes.ts` 中的 `TODO: 后端待实现` 注释已过时——后端已全部实现对应接口，仅前端注释未清理
- HTTP 客户端 `src/utils/http.ts` 自动附加 JWT Bearer token，401/403 触发 `CustomEvent`（`auth:unauthorized` / `auth:forbidden`）
- Token 存在 `localStorage`，key 为 `blog_system_token`

### 应用启动流程（main.ts）
```
createApp → use(pinia) → use(router) → setupRouterGuards → setupAuthEvents → restoreSession → mount
```

### 路由守卫（src/router/guards.ts）
- `requiresAuth` meta → 未登录重定向到 `/login?redirect=...`
- `requiresAdmin` meta → 调用 `fetchMe()` 检查 `role === 'ADMIN'`
- 前端只对 `/admin/**` 路由做了管理员角色校验，其他需要认证的路由不区分角色

## 后端（blog_system_backend）

### 环境与命令
- Java 17 toolchain，使用 Gradle wrapper
- `./gradlew bootRun` — 启动后端
- `./gradlew test` — 运行测试
- `./gradlew build` — 完整构建
- 测试仅一个冒烟测试 `contextLoads()`，无其他集成测试
- 配置入口 `src/main/resources/application.yaml`（不是 `.properties`）
- 通过 `spring.config.import: optional:file:./.env[.properties]` 加载 `blog_system_backend/.env`

### 必填环境变量（blog_system_backend/.env）
- `SERVER_IP` — MySQL 主机 IP
- `DB_USERNAME`、`DB_PASSWORD` — MySQL 凭证（数据库名 `blog_system`，端口 3306）
- `SERVER_PORT` — 默认 8080（实际部署常设为 8084）
- `JWT_SECRET` — 默认值不安全，生产需覆盖（>=32 字符）
- `JWT_EXPIRATION_MS` — 默认 86400000（24 小时）
- `ADMIN_USERNAME` + `ADMIN_PASSWORD` — 设值后启动时自动创建管理员账号（`AdminInitializer`），密码用 BCrypt 加密

### 数据库
- JPA `ddl-auto: update`，启动时自动创建/更新表结构
- 实体表：`users`、`blogs`、`categories`、`tags`、`blog_tag`（多对多连接表）、`blog_likes`、`comments`、`comment_likes`
- 唯一约束：`blog_likes(user_id, blog_id)`、`comment_likes(user_id, comment_id)`

### 安全配置（SecurityConfig）— 按优先级从高到低
| 路径模式 | 访问要求 |
|---|---|
| `/api/admin/**` | 仅 ADMIN 角色 |
| `/api/auth/**` | 公开 |
| `/api/tags/**` | 公开 |
| `/api/categories/**` | 公开 |
| `/api/blogs/**` | 公开 |
| `/api/comments/**` | 公开 |
| `/api/users/me` 及 `/api/users/me/**` | 需认证 |
| 其他所有请求 | 需认证 |

- 无状态 JWT，无 CSRF，`JwtAuthenticationFilter` 在 `UsernamePasswordAuthenticationFilter` 之前
- `AuthResponse.role` 是大写枚举 `USER` / `ADMIN`（`Role.name()`），不要用小写
- 博客/评论/分类/标签的认证逻辑在 Service 层处理（通过 `Authentication` 参数），Spring Security 层面这些路径是公开的

### API 控制器路径汇总
| 控制器 | 基础路径 |
|---|---|
| AuthController | `/api/auth` |
| BlogController | `/api/blogs` |
| AdminController | `/api/admin` |
| CategoryController | `/api/categories` |
| TagController | `/api/tags` |
| CommentController | `/api/comments` |
| UserController | `/api/users` |

### AdminController 端点
- `GET /api/admin/ping`、`GET /api/admin/blogs`、`PUT /api/admin/blogs/{id}`、`DELETE /api/admin/blogs/{id}`
- `GET /api/admin/comments`、`DELETE /api/admin/comments/{id}`

### BlogController 端点
- `GET /api/blogs`（支持 `page`、`size`、`categoryId`、`tagId`、`keyword`、`sort` 查询参数）
- `POST /api/blogs`、`GET /api/blogs/{id}`、`PUT /api/blogs/{id}`、`DELETE /api/blogs/{id}`
- `GET /api/blogs/{blogId}/comments`、`POST /api/blogs/{blogId}/comments`
- `POST /api/blogs/{blogId}/like`、`GET /api/blogs/{blogId}/isLiked`

### 评论回复
- 评论支持楼中楼：`POST /api/comments/{commentId}/replies` 对已有评论回复
- 顶级评论通过 `GET /api/blogs/{blogId}/comments` 获取
- 评论和回复的点赞通过 `POST /api/comments/{commentId}/like` 和 `GET /api/comments/{commentId}/isLiked`

## API 联调注意事项
- 前端 API 封装层代码已完成，但 `src/api/blogs.ts`、`src/api/categories.ts`、`src/api/users.ts` 中存在 **已过时的 `TODO: 后端待实现` 注释**（共 13 处）——后端已全部实现对应接口
- 前端 `page` 参数会做 zero-index 转换（`Math.max(0, pageValue - 1)`），确保与后端 Spring Data 的 Pageable 匹配
- 前端请求路径不带 `/api` 前缀（dev 时由 Vite 代理补全，prod 时 Nginx 补全）
