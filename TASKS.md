# 前端任务清单

说明：`[ ]` 未完成，`[x]` 已完成。

## 基础工程与配置
- [x] 配置 `.env.development/.env.production` 的 `VITE_API_BASE_URL`、`VITE_API_TIMEOUT` 并补齐 `env.d.ts` 类型声明。
- [x] 配置 Vite 开发代理 `/api` -> `VITE_API_PROXY_TARGET`（默认 `http://localhost:8080`）。
- [x] 封装请求库 `src/utils/http.ts`（超时、统一错误、query 序列化、自动 Bearer token）。
- [x] 封装 token 工具 `src/utils/auth.ts`（get/set/clear）。
- [x] 按接口文档与已实现后端生成 `src/api/*` 封装（博客/分类/评论/点赞标注 `TODO: 后端待实现`）。
- [x] 新建 `src/api/index.ts` 统一导出所有 API 模块，便于集中引用。
- [x] 在 `src/assets/main.css` 定义全局 CSS 变量（主色、背景、文本、边框、阴影、圆角）并设置基础排版。

## 路由与布局
- [x] 创建 `src/layouts/AppLayout.vue`，包含顶部导航、搜索入口、用户区（登录/头像/退出）、页脚。
- [x] 创建 `src/layouts/AdminLayout.vue`，包含侧边栏与管理导航。
- [x] 配置 `src/router/index.ts` 的页面路由与懒加载：首页、详情、登录、注册、发布/编辑、我的博客、个人中心、管理后台、404。
- [x] 增加 `src/router/guards.ts`，实现登录校验、管理员角色校验与未登录回跳 `redirect`。
- [x] 增加 `scrollBehavior`，路由切换自动回顶部。

## 状态管理与会话
- [x] 创建 `src/stores/auth.ts`，管理 token、当前用户、角色、登录/退出、`/users/me` 拉取。
- [x] 在应用启动时自动恢复登录态并刷新用户信息（失败则清 token）。
- [x] 统一处理 401/403：弹出提示并跳转登录或返回上一页。

## 公共组件（可复用）
- [x] 创建 `src/components/base/BaseButton.vue`（支持 loading/disabled/variant）。
- [x] 创建 `src/components/base/BaseInput.vue` 与 `BaseTextarea.vue`（支持 label、error、clear）。
- [x] 创建 `src/components/base/BaseSelect.vue` 与 `BaseTag.vue`（多选标签展示）。
- [x] 创建 `src/components/base/BaseModal.vue`（用于删除确认、提示）。
- [x] 创建 `src/components/base/BasePagination.vue`（支持总数/页码/尺寸）。
- [x] 创建 `src/components/base/EmptyState.vue` 与 `LoadingState.vue`（列表空态/加载态）。
- [x] 创建 `src/components/ToastProvider.vue`，用于全局提示（成功/失败/警告）。

## 认证与用户
- [x] 创建 `src/views/LoginView.vue`，实现登录表单校验、调用 `login`、写入 token、跳转来源页。
- [x] 创建 `src/views/RegisterView.vue`，实现注册表单校验、调用 `register`、自动登录。
- [x] 创建 `src/views/ProfileView.vue`，展示当前用户信息（从 `getMe` 读取）。
- [x] （后端待实现）补齐“更新个人资料”能力并接入 `/users/me` 更新接口。

## 博客浏览与搜索（公开）
- [x] 改造 `src/views/HomeView.vue` 为博客列表页，使用 `listBlogs` 渲染卡片列表。
- [x] 创建 `src/components/BlogCard.vue`，展示标题、作者、分类、标签、时间、浏览/点赞。
- [x] 增加分类与标签筛选（调用 `listCategories`、`listTags`），条件同步到 URL query。
- [x] 增加关键词搜索与排序（`keyword`、`sort`），与分页参数一并驱动请求。

## 博客详情与互动
- [x] 创建 `src/views/BlogDetailView.vue`，加载 `getBlogDetail` 并展示元信息。
- [x] 使用 `MyQuillEditor` 的只读模式渲染 Delta 内容。
- [x] 增加点赞按钮（`likeBlog`/`unlikeBlog`），支持乐观更新与错误回滚。

## 博客发布与编辑
- [x] 创建 `src/views/BlogEditorView.vue`，包含标题、分类选择、标签输入、内容编辑器。
- [x] 新建博客：调用 `createBlog`，成功后跳转详情。
- [x] 编辑博客：根据路由参数加载详情并回填，调用 `updateBlog`。
- [x] 增加草稿自动保存与恢复（localStorage），避免编辑内容丢失。

## 我的博客管理
- [x] 创建 `src/views/MyBlogsView.vue`，调用 `listMyBlogs` 显示当前用户博客。
- [x] 支持编辑与删除（`updateBlog`/`deleteBlog`），删除需二次确认。

## 评论系统
- [x] 创建 `src/components/CommentList.vue` 与 `CommentItem.vue`，支持楼中楼展示。
- [x] 在详情页调用 `listBlogComments` 并分页加载。
- [x] 实现发表评论与回复（`createBlogComment`、`replyComment`），未登录给出提示。
- [x] 实现删除评论（`deleteComment`），仅对作者/博主/管理员显示删除入口。
- [x] 实现评论点赞（`likeComment`/`unlikeComment`）并同步计数。

## 分类与标签
- [x] 创建 `src/views/AdminCategoriesView.vue`（列表/新增/编辑/删除分类）。
- [x] 创建 `src/views/AdminTagsView.vue`（列表/新增/编辑/删除标签）。
- [x] 在博客编辑页支持选择分类与多标签输入，标签可手动录入或从列表选择。

## 管理后台
- [x] 创建 `src/views/AdminDashboardView.vue` 作为管理入口页（统计/快捷入口）。
- [x] 创建 `src/views/AdminBlogListView.vue`，调用 `listAdminBlogs` 支持筛选/编辑/删除。
- [x] 创建 `src/views/AdminUsersView.vue`，调用 `listUsers`/`createUser`/`updateUser`/`deleteUser` 管理用户。
- [x] 为 `/admin/**` 路由添加角色校验，仅 ADMIN 可访问。

## 体验与可选扩展
- [ ] 增加全局请求错误提示与加载遮罩（基于 `ApiError` 分类处理）。
- [ ] 增加移动端适配（导航折叠、表单与列表响应式布局）。
- [ ] 增加搜索条件持久化（刷新后保留 query 过滤条件）。
- [ ] （可选）富文本图片上传与媒体管理（需后端支持上传接口）。

## 联调与验收
- [ ] 与后端确认真实 API 前缀（`/api` vs 文档 `/auth`），必要时调整 `VITE_API_BASE_URL` 或路径映射。
- [ ] 手动走通关键流程：注册/登录 -> 发布 -> 浏览 -> 评论 -> 点赞 -> 删除。
- [ ] 使用 `pnpm build` 做一次生产构建验证。
