# 企业巡检管理系统

面向企业设备巡检场景的一体化管理系统。员工可通过微信小程序上报巡检结果与现场图片，管理人员可在 Web 后台完成记录查询、权限管理、数据统计、Excel 导出，并生成日报、周报或月报所需的 AI 分析提示词。

## 核心能力

- 微信小程序：Token 登录、巡检上报、图片上传、记录列表与详情
- 管理后台：巡检记录筛选、分页、详情查看、Excel 导出、用户与角色管理
- 数据分析：总览、区域排行、设备风险、趋势图和当日异常看板
- 权限隔离：系统管理员、部门管理员、普通员工三级数据权限
- AI 报告准备：根据统计结果生成日报、周报、月报 Prompt（暂未接入外部大模型）
- 运维能力：操作日志、健康检查、Docker Compose 一键部署

## 系统架构

```text
微信小程序 ─┐
            ├─ Nginx ─ Spring Boot REST API ─ MySQL 8
Vue 3 后台 ─┘                 │
                              └─ 本地图片存储 / AI Prompt
```

| 模块 | 技术栈 |
|---|---|
| 微信小程序 | 原生框架、TypeScript |
| 管理后台 | Vue 3、Vite、TypeScript、Element Plus、ECharts |
| 后端服务 | Java 21、Spring Boot 3、MyBatis |
| 数据库 | MySQL 8 |
| 部署 | Docker Compose、Nginx |

## 快速开始

### 环境要求

- Docker Desktop（包含 Docker Compose v2）
- 建议至少 4 GB 可用内存

### 1. 配置环境变量

```bash
cp docker/.env.example .env
```

编辑 `.env`，至少设置 `MYSQL_ROOT_PASSWORD`、`MYSQL_USER`、`MYSQL_PASSWORD`、`SPRING_DATASOURCE_USERNAME` 和 `SPRING_DATASOURCE_PASSWORD`。应用数据库用户名与密码应保持一致。

Windows PowerShell 可使用：

```powershell
Copy-Item docker/.env.example .env
```

### 2. 启动服务

```bash
docker compose up -d --build
docker compose ps
```

首次构建需要下载依赖，通常需要数分钟。所有服务启动后访问：

- 管理后台：<http://localhost>
- 健康检查：<http://localhost/api/v1/ping>
- 初始管理员 Token：`admin-setup-token`

> 初始 Token 仅用于本地部署和验收。对外部署前请替换默认凭据，并完善正式认证方案。

### 3. 导入验收数据（可选）

```bash
docker compose exec -T mysql mysql --default-character-set=utf8mb4 -u inspection -p inspection_db < database/test-data.sql
```

完整验收步骤见 [Phase 13 验收指南](docs/Phase13-验收指南.md)。

### 常用命令

```bash
docker compose logs -f       # 查看日志
docker compose restart       # 重启服务
docker compose down          # 停止服务并保留数据卷
```

## 本地开发

### 后端

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

开发配置请从 `backend/src/main/resources/application-dev.yml.example` 复制，并通过本地配置或环境变量填写敏感信息；不要提交真实密码。

### 管理后台

```bash
cd admin
npm install
npm run dev
```

### 微信小程序

先创建本机项目配置：

```powershell
Copy-Item project.config.example.json project.config.json
```

在本地 `project.config.json` 中填写自己的微信小程序 App ID，再使用微信开发者工具导入仓库根目录。`project.config.json` 和 `project.private.config.json` 均已被 Git 忽略，禁止提交真实 App ID 或 AppSecret。

## 目录结构

```text
.
├─ miniprogram/       微信小程序源码
├─ admin/             Vue 3 管理后台
├─ backend/           Spring Boot 后端
├─ database/          初始化、测试与编码修复脚本
├─ docker/            后端和 Nginx 镜像配置
├─ docs/              架构、API、权限、部署与开发记录
├─ typings/           小程序 TypeScript 类型定义
├─ docker-compose.yml 服务编排
├─ start.bat          Windows 启动脚本
└─ stop.bat           Windows 停止脚本
```

## 权限模型

| 角色 | 数据范围 | 主要能力 |
|---|---|---|
| `ADMIN` | 全部数据 | 用户管理、全局统计、记录查询与导出 |
| `DEPT_ADMIN` | 本部门数据 | 部门统计、记录查询与导出 |
| `EMPLOYEE` | 本人数据 | 巡检上报、查看本人记录与统计 |

详细端点权限见 [权限矩阵](docs/权限矩阵.md)。权限由后端统一校验，前端菜单隐藏不作为安全边界。

## 文档导航

| 文档 | 内容 |
|---|---|
| [架构设计](docs/架构设计.md) | 系统分层与关键设计 |
| [数据库设计](docs/数据库设计.md) | 表结构和字段说明 |
| [API 文档](docs/API.md) | REST API 概览 |
| [权限矩阵](docs/权限矩阵.md) | 角色与接口访问范围 |
| [开发路线](docs/开发路线.md) | 阶段规划与完成状态 |
| [开发日志](docs/开发日志.md) | 按阶段记录的实现过程 |
| [新环境部署说明](docs/新环境部署与访问说明.md) | 部署、访问与排障 |
| [Docker 注意事项](docs/Docker-部署注意事项.md) | Docker 与字符集问题 |

## 当前状态与后续计划

当前版本已完成小程序上报、后台管理、统计分析、AI Prompt 生成和 Docker 部署闭环。后续计划包括企业微信通知、正式 JWT/SSO 认证、真实大模型接入、风险预测和数据大屏。

## Git 规范

提交信息遵循 [Conventional Commits](https://www.conventionalcommits.org/)；`main` 保持可部署，功能开发建议使用 `feature/*` 分支。任何 `.env`、生产配置、密钥和本机私有配置都不得提交。
