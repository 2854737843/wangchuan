# 数据库设计

核心表：
- `sys_user`：用户
- `org`：课题组
- `org_member`：成员关系与角色（OWNER/TEACHER/STUDENT）
- `project`：项目（按 `org_id` 隔离）
- `topic`：课题（按 `org_id` 隔离，关联 `project_id`）
- `task`：任务（按 `org_id` 隔离，关联 `topic_id`）

迁移脚本见：`backend/src/main/resources/db/migration/V1__init.sql`。
