# 企业微信巡检系统（Inspection Demo）

## Docker 快速部署（推荐）

### 环境要求

- Docker Desktop（含 Docker Compose）

### 三步启动

```bash
# 1. 创建环境变量文件
copy docker\.env.example .env

# 2. 编辑 .env 文件，设置 MySQL 密码
# MYSQL_ROOT_PASSWORD=你的密码
# MYSQL_USER=inspection
# MYSQL_PASSWORD=你的密码
# SPRING_DATASOURCE_USERNAME=inspection
# SPRING_DATASOURCE_PASSWORD=你的密码

# 3. 启动所有服务
docker compose up -d
```

### 访问

- 管理后台：http://localhost
- 默认管理员 Token：`admin-setup-token`

### Windows 一键启动

双击 `start.bat`，自动检查 Docker 状态并启动所有服务。

### 日常操作

```bash
docker compose up -d      # 启动
docker compose down       # 停止（保留数据）
docker compose down -v    # 停止并删除数据卷（⚠ 数据会丢失）
docker compose logs -f    # 查看日志
```

### 常见问题

| 问题 | 解决方案 |
|------|----------|
| 3306 端口被占用 | 停止本地 MySQL 服务，或修改 docker-compose.yml 中端口映射 |
| Docker 未启动 | 先启动 Docker Desktop |
| 首次构建慢 | 首次需下载镜像并编译，约 5-10 分钟 |
| 页面访问 404 | 等待所有容器状态为 `Up` 后再访问 |

---

## 项目简介

一个企业内部巡检管理系统，支持：

- **普通员工**：通过微信小程序提交巡检记录（部门、姓名、区域、设备、图片、描述等）
- **管理员**：通过 Web 后台查看、筛选、统计所有巡检记录
- **AI 分析**：Python 脚本自动生成日报、月报、分析建议

---

## 项目背景

传统巡检依赖纸质表单或 Excel 记录，存在以下痛点：

- 数据分散，难以汇总分析
- 图片无法与记录关联
- 无法实时查看巡检进度
- 管理层缺乏数据驱动的决策依据

本系统通过"小程序录入 + Web 后台管理 + AI 自动分析"三层架构，解决上述问题。

---

## 项目目标

| 目标 | 说明 |
|---|---|
| ✔ 企业可维护 | 标准化目录结构、统一代码规范、完整文档 |
| ✔ 企业可部署 | Docker 一键部署，环境隔离 |
| ✔ 企业可交接 | README + 文档齐全，新人可快速上手 |
| ✔ 企业可扩展 | 高内聚低耦合，模块独立可替换 |
| ✔ 企业可运行 | 每一步都是可运行状态，不遗留"半成品"代码 |

---

## 总体架构图

```
┌─────────────────────────────────────────────────┐
│                    用户层                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐      │
│  │ 微信小程序 │  │ Vue3 Web │  │ AI Agent │      │
│  │ (员工端)  │  │ (管理端)  │  │ (分析端)  │      │
│  └─────┬────┘  └────┬─────┘  └────┬─────┘      │
│        │            │             │             │
├────────┼────────────┼─────────────┼─────────────┤
│        ▼            ▼             ▼             │
│  ┌─────────────────────────────────────────┐    │
│  │          RESTful API (Spring Boot)       │    │
│  └─────────────────┬───────────────────────┘    │
│                    │                             │
│                    ▼                             │
│  ┌─────────────────────────────────────────┐    │
│  │              MySQL 8                     │    │
│  └─────────────────────────────────────────┘    │
│                  数据层                          │
└─────────────────────────────────────────────────┘
```

---

## 项目目录结构

```
miniprogram-1/
├── miniprogram/          # 微信小程序端（TypeScript）
│   ├── app.json          #   小程序配置
│   ├── app.ts            #   小程序入口
│   ├── pages/            #   页面目录
│   └── utils/            #   工具函数
├── backend/              # Spring Boot 后端（Java 21）
├── admin/                # Vue3 管理后台
├── ai-analysis/          # Python AI 分析模块
├── database/             # 数据库脚本
├── docker/               # Docker 配置
│   ├── .env.example      #   环境变量模板
│   ├── backend/          #   Spring Boot Dockerfile + Maven 镜像
│   └── nginx/            #   Nginx Dockerfile + 配置
├── docker-compose.yml    # Docker Compose 编排
├── start.bat             # Windows 一键启动
├── stop.bat              # Windows 停止
├── docs/                 # 项目文档
│   ├── API.md            #   API 接口文档
│   ├── 数据库设计.md      #   数据库表结构设计
│   └── 架构设计.md        #   系统架构设计说明
├── typings/              # TypeScript 类型定义
├── .gitignore            # Git 忽略规则
└── README.md             # 本文件
```

---

## 技术选型

| 层级 | 技术 | 版本 |
|---|---|---|
| 后端框架 | Spring Boot | 3.x |
| Java | JDK | 21 LTS |
| 构建工具 | Maven | 3.9+ |
| 数据库 | MySQL | 8.0+ |
| 管理后台 | Vue 3 | 3.x |
| 小程序 | 微信原生 + TypeScript | - |
| AI 分析 | Python | 3.11+ |
| 容器化 | Docker | - |

---

## 开发规范

### Git Commit 规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/)：

```
<type>: <description>

feat:     新功能
fix:      Bug 修复
docs:     文档变更
style:    代码格式（不影响功能）
refactor: 重构（既非新功能也非 Bug 修复）
test:     测试相关
chore:    构建/工具/依赖变更
```

示例：

```
feat: 新增巡检记录提交接口 POST /report
docs: 创建数据库设计文档
chore: 项目初始化（目录结构 + Git 仓库 + .gitignore）
```

### 代码规范

- **Java**：遵循阿里巴巴 Java 开发手册
- **TypeScript**：`tsconfig.json` 已启用 strict 模式
- **Python**：遵循 PEP 8

### 分支策略

- `master`：稳定版本，随时可部署
- `dev`：开发分支
- `feature/xxx`：功能分支

---

## 开发路线（Roadmap）

| 阶段 | 内容 | 状态 |
|---|---|---|
| 第一阶段 | 项目初始化（目录结构、Git、.gitignore、README、docs） | ✅ 已完成 |
| 第二阶段 | 数据库设计（业务分析 → 表设计 → init.sql） | ✅ 已完成 |
| 第三阶段 | Spring Boot 初始化（项目骨架、多环境配置、启动验证） | ✅ 已完成 |
| 第四阶段 | 后端业务开发（Entity → Mapper → Service → Controller → 查询 + 图片上传） | ✅ 已完成 |
| 第五阶段 | 微信小程序开发（巡检上报 + 列表 + 详情） | ✅ 已完成 |
| 第六阶段 | Vue3 管理后台（列表、分页、筛选、统计图） | ✅ 已完成 |
| 第七阶段 | AI 分析（Python 脚本、日报、月报、AI 建议） | ⬜ 待开始 |
| 第八阶段 | Docker 部署（Dockerfile、docker-compose、启动脚本） | ✅ 已完成 |

---

## 参与开发

本项目遵循"最小变更 + 先计划再执行"的开发原则。

每条 Commit 必须是独立、可运行、可回滚的最小单元。