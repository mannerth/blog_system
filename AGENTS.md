# AGENTS

**始终用中文回答用户**

## Repo layout
- `blog_system_frontend/` is the Vue 3 + Vite SPA (Pinia + Vue Router).
- `blog_system_backend/` is the Spring Boot API (Gradle, Java 17 toolchain).
- `TASKS.md` tracks the frontend task checklist (use it to mark progress).

## Frontend (blog_system_frontend)
- Use `pnpm` (lockfile is `pnpm-lock.yaml`); `pnpm dev` and `pnpm build` are the canonical scripts.
- `pnpm build` runs `vue-tsc --build` before `vite build` (don't skip type-check when validating).
- Vite alias `@` maps to `blog_system_frontend/src`.
- Node engine is `^20.19.0 || >=22.12.0` (see `blog_system_frontend/package.json`).
- API base/timeout are set in `.env.*` via `VITE_API_BASE_URL` and `VITE_API_TIMEOUT`; dev proxy uses `VITE_API_PROXY_TARGET` (default `http://localhost:8080`).
- Blog editor is wrapped in `blog_system_frontend/src/components/MyQuillEditor.vue` and expects Quill `Delta` content (not HTML).
- API wrappers live in `blog_system_frontend/src/api`; blog/comment/category/like wrappers are placeholders marked `TODO: 后端待实现`.

## Backend (blog_system_backend)
- Config loads `./.env` from the backend working dir (`spring.config.import: optional:file:./.env[.properties]`); put env vars in `blog_system_backend/.env`.
- Required envs for DB: `SERVER_IP`, `DB_USERNAME`, `DB_PASSWORD` (MySQL `blog_system` at port 3306); `SERVER_PORT` defaults to 8080.
- JWT defaults exist but should be overridden: `JWT_SECRET` (>=32 chars), `JWT_EXPIRATION_MS`.
- Set `ADMIN_USERNAME` + `ADMIN_PASSWORD` to auto-seed an admin on startup.

## API notes
- Backend controllers are under `/api/...` (e.g. `AuthController` -> `/api/auth`), while `blog_system_frontend/doc/API-v1.openapi.json` currently lists `/auth/...` paths; double-check prefix when wiring clients.
- `AuthResponse.role` is uppercase enum `USER` or `ADMIN` (`Role.name()`); API spec lists lowercase enums, so follow backend.
- `SecurityConfig` permits `/api/auth/**` and `/api/tags/**`, requires auth for most other endpoints, and restricts `/api/admin/**` + `/api/users/**` to `ADMIN` except `/api/users/me`.
