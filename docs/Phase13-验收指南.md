# Phase 13 验收指南 — 巡检数据统计分析 + AI 接口

> 📌 本文档用于验证 Phase 13 所有新增功能是否正确工作。

---

## 一、前置条件

### 1.1 环境要求

- Docker Desktop 已启动
- MySQL 容器运行中（`docker compose up -d mysql`）
- 后端已启动（`cd backend && mvnw spring-boot:run`）
- 使用 **系统管理员 Token** 进行 API 测试

### 1.2 获取管理员 Token

```bash
# 使用 init.sql 预置的管理员 token
curl -H "Authorization: Bearer admin-setup-token" \
  http://localhost:8080/api/v1/statistics/overview
```

---

## 二、导入测试数据

### 2.1 导入

```bash
# Windows（cmd 或 PowerShell，使用 < 重定向，避免 PowerShell 管道 UTF-16 转换）
docker exec -i inspection-mysql mysql -u inspection -pinspection_pwd_2026 --default-character-set=utf8mb4 inspection_db < database\test-data.sql

# Linux / macOS
docker exec -i inspection-mysql mysql -u inspection -pinspection_pwd_2026 --default-character-set=utf8mb4 inspection_db < database/test-data.sql
```

### 2.2 验证导入

```bash
docker exec -i inspection-mysql mysql -u inspection -pinspection_pwd_2026 inspection_db -e "
SELECT 'departments' as tbl, COUNT(*) as cnt FROM sys_department WHERE id >= 10
UNION ALL
SELECT 'users', COUNT(*) FROM sys_user WHERE id >= 10
UNION ALL
SELECT 'records', COUNT(*) FROM inspection_record WHERE user_id >= 10;
"
```

**预期**：departments≥5, users≥10, records≥100

### 2.3 清理测试数据

```bash
docker exec -i inspection-mysql mysql -u inspection -pinspection_pwd_2026 inspection_db < database/test-data-clean.sql
```

---

## 三、API 验收

所有 API 需携带 Header：`Authorization: Bearer admin-setup-token`

### 3.1 验证 Overview（统计总览）

**请求：**
```bash
curl -H "Authorization: Bearer admin-setup-token" \
  "http://localhost:8080/api/v1/statistics/overview"
```

**预期响应：**
```json
{
  "code": 200,
  "data": {
    "totalRecords": 130,
    "normalCount": 90,
    "faultCount": 24,
    "hiddenCount": 16,
    "anomalyRate": 30.77
  }
}
```

> 数值允许 ±10 偏差（取决于数据是否被重复执行）

**核验点：**
- [ ] `totalRecords` = `normalCount + faultCount + hiddenCount`
- [ ] `anomalyRate` = `(faultCount + hiddenCount) / totalRecords * 100`，保留两位小数
- [ ] `code` 为 200

---

### 3.2 验证 Area（区域统计）

**请求：**
```bash
curl -H "Authorization: Bearer admin-setup-token" \
  "http://localhost:8080/api/v1/statistics/area"
```

**预期响应：** 返回数组，按 `anomalyRate` 降序排列。

```json
{
  "code": 200,
  "data": [
    {
      "area": "D区",
      "totalCount": 12,
      "normalCount": 5,
      "faultCount": 4,
      "hiddenCount": 3,
      "anomalyRate": 58.33
    },
    ...
  ]
}
```

**核验点：**
- [ ] 返回数组，每项含 `area`、`totalCount`、`normalCount`、`faultCount`、`hiddenCount`、`anomalyRate`
- [ ] 按 `anomalyRate` 降序排列（异常率最高的在最前）
- [ ] 支持时间范围过滤：`?startDate=2026-07-01T00:00:00&endDate=2026-07-15T23:59:59`

---

### 3.3 验证 Device（设备统计）

**请求：**
```bash
curl -H "Authorization: Bearer admin-setup-token" \
  "http://localhost:8080/api/v1/statistics/device"
```

**预期响应：**
```json
{
  "code": 200,
  "data": {
    "highFrequencyDevices": [
      { "deviceName": "变压器T-001", "count": 15 },
      ...
    ],
    "faultDevices": [
      { "deviceName": "变压器T-001", "faultCount": 3, "totalCount": 15, "faultRate": 20.0 },
      ...
    ],
    "riskDevices": [
      { "deviceName": "变压器T-002", "hiddenCount": 2, "totalCount": 12, "hiddenRate": 16.67 },
      ...
    ]
  }
}
```

**核验点：**
- [ ] `highFrequencyDevices` 最多 10 项，按巡检次数降序
- [ ] `faultDevices` 最多 10 项，按故障率降序
- [ ] `riskDevices` 最多 10 项，按隐患率降序
- [ ] 各设备项含正确的 `deviceName` 和统计数值

---

### 3.4 验证 Trend（趋势统计）

**请求：**
```bash
curl -H "Authorization: Bearer admin-setup-token" \
  "http://localhost:8080/api/v1/statistics/trend"
```

**预期响应：** 返回数组，每项为一天的统计数据。

```json
{
  "code": 200,
  "data": [
    {
      "date": "2026-07-28",
      "totalCount": 6,
      "normalCount": 4,
      "faultCount": 1,
      "hiddenCount": 1,
      "anomalyRate": 33.33
    },
    ...
  ]
}
```

**核验点：**
- [ ] 数据按日期降序排列（最新的在前）
- [ ] 每项含 `date`、`totalCount`、`normalCount`、`faultCount`、`hiddenCount`、`anomalyRate`
- [ ] `totalCount` = `normalCount + faultCount + hiddenCount`

---

### 3.5 验证已有 `/statistics` 兼容性

**请求：**
```bash
curl -H "Authorization: Bearer admin-setup-token" \
  "http://localhost:8080/api/v1/statistics"
```

**预期响应：** 与 Phase 10.3 结构完全一致。

```json
{
  "code": 200,
  "data": {
    "totalRecords": 130,
    "byResult": { "normal": 90, "fault": 24, "hidden": 16 },
    "byDepartment": [ { "department": "运维一部", "count": 40 }, ... ],
    "byDate": [ { "date": "2026-07-28", "count": 6 }, ... ]
  }
}
```

**核验点：**
- [ ] 返回结构包含 `totalRecords`、`byResult`、`byDepartment`、`byDate`
- [ ] Admin Dashboard (`/dashboard`) 页面正常显示数据卡片和图表

---

### 3.6 验证 AI Report（AI 分析接口）

**请求：**
```bash
curl -X POST -H "Authorization: Bearer admin-setup-token" \
  -H "Content-Type: application/json" \
  -d '{"startDate":"2026-07-01T00:00:00","endDate":"2026-07-31T23:59:59","reportType":"MONTHLY"}' \
  http://localhost:8080/api/v1/ai/report
```

**预期响应：**
```json
{
  "code": 200,
  "data": {
    "reportId": "uuid-string",
    "generatedAt": "2026-07-29T...",
    "period": {
      "start": "2026-07-01T00:00:00",
      "end": "2026-07-31T23:59:59",
      "type": "MONTHLY"
    },
    "summary": {
      "totalRecords": 130,
      "anomalyRate": 30.77,
      "trend": "上升"  // 或 "下降" / "持平"
    },
    "topAreas": [ ... up to 5 ... ],
    "topDevices": [ ... up to 10 ... ],
    "trendData": [ ... ],
    "aiAnalysis": null,
    "aiPrompt": "请根据以下设备巡检数据生成一份专业的巡检分析报告..."
  }
}
```

**核验点：**
- [ ] `aiAnalysis` 为 `null`（Phase 13 不调用 AI）
- [ ] `aiPrompt` 包含中文文本，提及巡检数据、区域排行、设备排行
- [ ] `summary.trend` 为 "上升" / "下降" / "持平" 之一
- [ ] `reportId` 为有效 UUID

---

## 四、Admin 页面验收

### 4.1 访问统计分析页面

1. 启动 Admin 前端：`cd admin && npm run dev`
2. 浏览器打开 `http://localhost:5173`
3. 使用管理员 Token 登录（`admin-setup-token`）
4. 点击侧边栏 **"统计分析"** 菜单

**核验点：**
- [ ] 侧边栏出现"统计分析"菜单项（在 Dashboard 和 巡检记录 之后）
- [ ] 页面显示 5 张概览卡片（总巡检数、正常、故障、隐患、异常率）
- [ ] 时间范围选择器可以筛选日期
- [ ] 区域输入框可以过滤区域
- [ ] 趋势折线图渲染（4 条线：总数/正常/故障/隐患）
- [ ] 区域异常率柱状图渲染
- [ ] 高频设备横向条形图渲染
- [ ] 故障设备表格显示（设备名/故障数/故障率）
- [ ] 隐患设备表格显示（设备名/隐患数/隐患率）

### 4.2 验证 Dashboard 未受影响

1. 点击"Dashboard"菜单
2. 确认原有的 4 张卡片 + 2 张图表正常显示

**核验点：**
- [ ] Dashboard 页面数据正常
- [ ] 卡片数值正确
- [ ] 趋势折线图渲染正常
- [ ] 部门饼图渲染正常

---

## 五、权限验收

使用不同角色 Token 验证数据隔离：

| 测试 | Token 角色 | 端点 | 预期 |
|---|---|---|---|
| TC-P1 | `admin-setup-token` (ADMIN) | `/statistics/overview` | 返回全部数据（totalRecords ≥ 100） |
| TC-P2 | DEPT_ADMIN（需要先登录获取 Token） | `/statistics/overview` | 仅返回本部门数据 |
| TC-P3 | EMPLOYEE（需要先登录获取 Token） | `/statistics/overview` | 仅返回自己的数据（不 403） |

> 注：DEPT_ADMIN / EMPLOYEE Token 需要通过 `POST /api/v1/login` 登录获取。

---

## 六、Docker 兼容性验收

```bash
# 1. 确认 docker-compose.yml 未修改（git diff 无变化）
git diff docker-compose.yml

# 2. 确认 init.sql 未修改
git diff database/init.sql

# 3. 重启全部服务
docker compose down
docker compose up -d

# 4. 访问健康检查
curl http://localhost:8080/api/v1/statistics/overview
```

**核验点：**
- [ ] `docker compose up -d` 正常启动
- [ ] 新接口可访问
- [ ] 数据库无异常

---

## 七、验收清单

| 序号 | 验收项 | 状态 |
|---|---|---|
| 1 | `GET /statistics/overview` 返回总览数据 | ⬜ |
| 2 | `GET /statistics/area` 返回区域排行 | ⬜ |
| 3 | `GET /statistics/device` 返回设备统计 | ⬜ |
| 4 | `GET /statistics/trend` 返回趋势数据 | ⬜ |
| 5 | `GET /statistics`（旧）兼容不变 | ⬜ |
| 6 | `POST /ai/report` 返回摘要+Prompt | ⬜ |
| 7 | Admin 统计分析页面显示完整 | ⬜ |
| 8 | Admin Dashboard 页面正常 | ⬜ |
| 9 | ADMIN 角色可查看全部数据 | ⬜ |
| 10 | DEPT_ADMIN 仅查看本部门 | ⬜ |
| 11 | EMPLOYEE 仅查看自己数据 | ⬜ |
| 12 | Docker 部署无影响 | ⬜ |
| 13 | `mvnw compile` 通过 | ⬜ |
| 14 | `npm run build` 通过 | ⬜ |