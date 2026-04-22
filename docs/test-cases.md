# 测试用例

1. 登录成功/失败
2. 创建组织后自动成为 OWNER
3. `GET /api/orgs/mine` 返回当前用户组织列表
4. 非成员访问组织数据返回 403
5. `X-Org-Id` 与路径 orgId 不一致返回 400（不再 500）
6. OWNER/TEACHER 可以管理成员，STUDENT 不可
7. 在不同 `X-Org-Id` 下，项目/课题/任务数据隔离
8. 统一响应结构校验（`code/message/data`）
