# Docker 部署注意事项

> 📌 本文档记录项目 Docker 部署过程中的关键注意事项，尤其是中文编码相关的问题。
> 面向：新环境部署人员、接手项目的开发者。

---

## 1. MySQL 中文编码要求

项目数据库统一使用：

- **字符集**：`utf8mb4`
- **排序规则**：`utf8mb4_unicode_ci`

### 原因

MySQL 客户端连接字符集与服务端不一致时，可能导致中文数据在写入时发生编码转换，造成**永久性乱码**。一旦乱码写入磁盘，即使后续修复连接字符集也无法恢复，必须重新导入数据。

### 当前配置

`docker-compose.yml` 已配置（v2.0）：

```yaml
mysql:
  environment:
    MYSQL_CHARSET: utf8mb4
    MYSQL_COLLATION: utf8mb4_unicode_ci
```

所有表 DDL 均指定：

```sql
DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
```

---

## 2. 新环境部署流程

### 首次部署

```bash
# 1. 配置环境变量
cp docker/.env.example .env
# 编辑 .env，填入 MySQL 密码和微信配置

# 2. 启动全部服务
docker compose up -d

# 3. 等待 MySQL 健康检查通过（约30秒）
docker compose ps    # STATUS 应显示 healthy

# 4. 验证数据库字符集
docker exec -i inspection-mysql mysql -u inspection -p inspection_db -e "SHOW VARIABLES LIKE 'character_set%';"
```

**预期**：`character_set_server` 应为 `utf8mb4`。

### 验证 SQL 表结构

```bash
docker exec -i inspection-mysql mysql -u inspection -p inspection_db -e "SHOW CREATE TABLE sys_role\G"
```

**预期**：应包含 `DEFAULT CHARSET=utf8mb4`。

---

## 3. Windows 导入 SQL 注意事项 ⚠️

### ❌ 禁止方式

```powershell
# 绝对不要使用以下方式导入 SQL 文件！
Get-Content database\test-data.sql | docker exec -i inspection-mysql mysql -u inspection -pxxxxxxxx inspection_db
```

### 原因

PowerShell 管道导入会导致编码转换链：

```
UTF-8 SQL 文件
    ↓
Get-Content 读取
    ↓
PowerShell 内部自动转为 UTF-16LE
    ↓
管道传给 docker exec
    ↓
MySQL 接收到错误编码字节流
    ↓
中文保存为乱码（如：æ™®é€šå'˜å·¥）
```

### ✅ 推荐方式

**Windows（cmd 或 PowerShell 均可）：**

```cmd
docker exec -i inspection-mysql mysql --default-character-set=utf8mb4 -u 用户名 -p密码 数据库名 < database\test-data.sql
```

或者使用 cmd 包装：

```cmd
cmd /c "docker exec -i inspection-mysql mysql --default-character-set=utf8mb4 -u inspection -pinspection_pwd_2026 inspection_db < database\test-data.sql"
```

**Linux / macOS：**

```bash
docker exec -i inspection-mysql mysql --default-character-set=utf8mb4 -u 用户名 -p密码 数据库名 < database/test-data.sql
```

### 关键参数说明

| 参数 | 作用 |
|---|---|
| `<` 文件重定向 | 绕过 Shell 管道编码转换，直接以原始字节流传给 docker |
| `--default-character-set=utf8mb4` | 强制 MySQL 客户端以 UTF-8 编码通信 |

---

## 4. 中文乱码排查步骤

如果发现中文显示为 `????` 或类似乱码：

### Step 1：检查 SQL 文件编码

```bash
# Windows
certutil -hashfile database\test-data.sql MD5

# Linux/macOS
file -i database/test-data.sql
# 应输出：text/plain; charset=utf-8
```

### Step 2：检查数据库字段字符集

```sql
SHOW CREATE TABLE sys_role;
```

确认输出包含 `DEFAULT CHARSET=utf8mb4`。

### Step 3：检查数据实际存储

```sql
SELECT id, role_name, HEX(role_name) FROM sys_role WHERE id = 1;
```

- **HEX 为正常 UTF-8 编码**（如 `E699AEE9809AE59198E5B7A5`）：数据库正常，可能是客户端显示问题
- **HEX 为乱码编码**（如 `3F3F3F3F3F` 大量重复字节）：数据已被污染，需要重新导入

### Step 4：修复乱码数据

如果数据已被污染：

```bash
# 1. 使用正确方式重新导入
docker exec -i inspection-mysql mysql --default-character-set=utf8mb4 -u inspection -pinspection_pwd_2026 inspection_db < database\test-data-clean.sql
docker exec -i inspection-mysql mysql --default-character-set=utf8mb4 -u inspection -pinspection_pwd_2026 inspection_db < database\test-data.sql

# 2. 或执行修复脚本（仅修复 sys_role）
docker exec -i inspection-mysql mysql --default-character-set=utf8mb4 -u inspection -pinspection_pwd_2026 inspection_db < database\fix-encoding.sql
```

---

## 5. 当前项目历史问题说明

### Phase 13 已知问题

在 Phase 13 测试数据导入阶段，曾因 Windows PowerShell 管道导入导致以下数据出现乱码：

| 原数据 | 乱码形式 |
|---|---|
| 普通员工 | æ™®é€šå'˜å·¥ |
| 部门管理员 | éƒ¨é—¨ç®¡ç†å'˜ |
| 系统管理员 | ç³»ç»Ÿç®¡ç†å'˜ |

### 已实施的修复措施

| 措施 | 文件/位置 |
|---|---|
| docker-compose.yml 增加 `MYSQL_CHARSET` + `MYSQL_COLLATION` | `docker-compose.yml` |
| 所有导入命令统一使用 `--default-character-set=utf8mb4` | `docs/Phase13-验收指南.md` |
| 新增编码修复脚本 | `database/fix-encoding.sql` |
| 新增本文档 | `docs/Docker-部署注意事项.md` |

### 给未来部署人员的建议

1. **永远不要使用 `Get-Content | docker exec` 管道导入 SQL**
2. 始终使用 `<` 文件重定向方式
3. 导入 SQL 时加上 `--default-character-set=utf8mb4`
4. 首次导入后执行 `SELECT` 验证中文是否正常
5. 定期备份数据库，避免因编码问题丢失数据

---

## 6. 文档索引

| 文档 | 用途 |
|---|---|
| `docs/Docker-部署注意事项.md` | 本文档 — 部署编码避坑指南 |
| `docs/Phase13-验收指南.md` | Phase 13 功能验收步骤 |
| `database/fix-encoding.sql` | sys_role 编码修复脚本 |
| `database/test-data.sql` | Phase 13 测试数据 |
| `database/test-data-clean.sql` | 测试数据清理脚本 |

---

> ⚠️ 中文编码问题一旦发生，数据修复成本极高。请务必在首次部署时遵循本文档的导入方式。
