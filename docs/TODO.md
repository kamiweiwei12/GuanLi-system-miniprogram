# TODO

## 已完成

- [x] Phase 1：项目初始化（目录结构、Git、.gitignore、README、docs）
- [x] Phase 2：数据库设计（inspection_record + inspection_image + init.sql）
- [x] Phase 3：Spring Boot 初始化（Maven Wrapper、包结构、yml 配置、启动验证）

## 当前进行中

- [ ] Phase 4：基础业务开发
  - [x] Step 11：创建 Entity + Mapper（InspectionRecord、InspectionImage + XML）
  - [x] Step 12：创建 Service + DTO + BusinessException
  - [ ] Step 13：创建 Controller（POST /report）
  - [ ] Step 14：创建 DTO / VO
  - [ ] Step 15：创建 Controller（POST /report）
  - [ ] Step 16：统一返回结构 Result<T>
  - [ ] Step 17：统一异常处理
  - [ ] Step 18：健康检查接口 GET /ping

## 下一步

- [ ] Phase 5：查询接口（分页、筛选）
- [ ] Phase 6：微信小程序
- [ ] Phase 7：Vue3 管理后台
- [ ] Phase 8：AI 分析
- [ ] Phase 9：Docker 部署

## 后续优化

- [ ] 日志持久化到文件
- [ ] 接口文档 Swagger/Knife4j
- [ ] 单元测试完善
- [ ] 性能压测