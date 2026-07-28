# TODO

## 已完成（Phase 1-9）

- [x] Phase 1：项目初始化（目录结构、Git、.gitignore、README、docs）
- [x] Phase 2：数据库设计（inspection_record + inspection_image + init.sql）
- [x] Phase 3：Spring Boot 初始化（Maven Wrapper、包结构、yml 配置、启动验证）
- [x] Phase 4：后端业务 + 小程序（Entity → Mapper → Service → Controller → 上报/列表/详情 + 图片上传/显示 + 环境隔离）
- [x] Phase 5：数据库用户体系设计（sys_user + sys_role + sys_department + sys_token + inspection_record.user_id）
- [x] Phase 6：用户基础模块（User/Role/Department Entity + Mapper + UserService + 集成测试 3/3）
- [x] Phase 7：认证框架（AuthInterceptor + TokenService + UserContext + TokenMapper + 集成测试 4/4）
- [x] Phase 8：巡检业务接入用户体系（submitReport/queryList/getById 加 userId + 数据隔离 + 集成测试 4/4）
- [x] Phase 9：微信小程序登录接入（WechatService + LoginController + request/upload Token 注入 + app.ts 登录流程 + 集成测试 4/4）

## 下一步（Phase 10）

- [ ] Phase 10：Vue3 管理后台（列表、分页、筛选、统计图）

## 后续规划（Phase 11-12）

- [ ] Phase 11：AI 分析（Python 脚本、日报、月报、AI 建议）
- [ ] Phase 12：Docker 部署（Dockerfile、docker-compose、启动脚本）

## 运维事项

- [ ] 已有数据库实例中执行 `ALTER TABLE inspection_record ADD COLUMN user_id`（Step 25.1）
- [ ] 配置微信 AppID/Secret 环境变量 `WX_APP_ID`、`WX_APP_SECRET`

## 后续优化

- [ ] 图片 `/uploads/**` 公开访问 → 需结合权限控制
- [ ] 日志持久化到文件
- [ ] 接口文档 Swagger/Knife4j
- [ ] N+1 图片查询优化
- [ ] 手机号正则校验加强
- [ ] 性能压测