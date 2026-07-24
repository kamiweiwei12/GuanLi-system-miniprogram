# TODO

## 已完成

- [x] Phase 1：项目初始化（目录结构、Git、.gitignore、README、docs）
- [x] Phase 2：数据库设计（inspection_record + inspection_image + init.sql）
- [x] Phase 3：Spring Boot 初始化（Maven Wrapper、包结构、yml 配置、启动验证）
- [x] Step 11：创建 Entity + Mapper（InspectionRecord、InspectionImage + XML）
- [x] Step 12：创建 Service + DTO + BusinessException
- [x] Step 13：创建 Controller + Result + GlobalExceptionHandler
- [x] Step 14：查询接口（GET /report + 分页 + 筛选）
- [x] Step 15：图片上传接口（POST /upload/image）
- [x] Step 16：微信小程序基础工程（TypeScript + 环境配置 + 网络封装 + API模块）
- [x] Step 17：微信小程序巡检上报（UI + 图片上传 + 提交 + 完整闭环）
- [x] Step 18：巡检记录列表页面（分页 + 下拉刷新 + 状态颜色）

## 当前进行中

- [ ] Phase 5：微信小程序开发（基础功能完成）

## 下一步

- [ ] Phase 6：Vue3 管理后台
- [ ] Phase 7：AI 分析（Python）
- [ ] Phase 8：Docker 部署

## 后续优化

- [ ] 图片 `/uploads/**` 公开访问 → 需结合权限控制
- [ ] 日志持久化到文件
- [ ] 接口文档 Swagger/Knife4j
- [ ] 单元测试完善
- [ ] 性能压测
- [ ] N+1 图片查询优化
- [ ] 手机号正则校验加强
