# 多课题组（多租户）项目管理系统

本仓库已新增：
- `backend/`：Spring Boot 3.5 + Java 17 + MySQL8 + MyBatis-Plus + Flyway + JWT + Swagger
- `frontend/`：Vue3 + Vite + Router + Pinia + Axios + Element Plus

## 1. 后端启动

### 1.1 环境变量（可选）
```bash
export DB_URL='jdbc:mysql://localhost:3306/lab_collab?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai'
export DB_USERNAME='root'
export DB_PASSWORD='root'
export JWT_SECRET='LabCollabJwtSecretKeyForDevOnly_ChangeMe1234567890'
```

### 1.2 建库
```sql
CREATE DATABASE IF NOT EXISTS lab_collab DEFAULT CHARACTER SET utf8mb4;
```

### 1.3 启动后端
```bash
cd backend
mvn spring-boot:run
```

Flyway 会自动建表。首次使用请先自行插入用户（密码使用 BCrypt），例如：
```sql
INSERT INTO sys_user(username, password_hash, enabled)
VALUES ('admin', '$2b$12$CJmFKzWJAOb9QL24PeX2FecTSzCUy6EFmsz7YSzY7PVRv0sSOZy5S', 1);
```
对应示例密码为：`123456`

Swagger 地址：`http://localhost:8080/swagger-ui.html`

## 2. 前端启动

```bash
cd frontend
npm install
npm run dev
```

默认访问：`http://localhost:5173`

## 3. Postman 快速测试

1. `POST /api/auth/login` 获取 token
2. `POST /api/orgs` 创建课题组（自动 OWNER）
3. `GET /api/orgs/mine` 获取可切换组织
4. 后续请求都带：
   - `Authorization: Bearer <token>`
   - `X-Org-Id: <orgId>`
5. 测试：
   - `GET/POST/DELETE /api/orgs/{orgId}/members`
   - `CRUD /api/projects`
   - `CRUD /api/projects/{projectId}/topics`
   - `CRUD /api/topics/{topicId}/tasks`

## 4. 异常与响应格式

统一响应：
```json
{"code":0,"message":"ok","data":{}}
```

全局异常处理：
- 400：参数校验错误、`X-Org-Id` 不合法、`X-Org-Id mismatch with path orgId`
- 401：未认证
- 403：无权限或非组织成员
