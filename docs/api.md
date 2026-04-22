# API 文档

## 认证
- `POST /api/auth/login`
- `GET /api/auth/me`

## 组织
- `POST /api/orgs`
- `GET /api/orgs/mine`

## 成员管理（需 OWNER/TEACHER）
- `GET /api/orgs/{orgId}/members`
- `POST /api/orgs/{orgId}/members`
- `DELETE /api/orgs/{orgId}/members/{userId}`

## 项目/课题/任务
- `CRUD /api/projects`
- `CRUD /api/projects/{projectId}/topics`
- `CRUD /api/topics/{topicId}/tasks`

所有受组织隔离接口需 Header：`X-Org-Id`。
