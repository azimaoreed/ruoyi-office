# BPM 最小化部署指南（只上线 `yudao-module-bpm` 工作流能力）

本文基于当前仓库 `ruoyi-office` 的实际结构（多模块 Maven + 既支持单体 `yudao-server`、也支持微服务 `yudao-gateway`），给出“**只想上线工作流 BPM**”时的**最小化部署方案**，并明确模块依赖边界。

> 结论先行：在本项目里，“只部署 BPM”通常指 **只对外提供 BPM 相关功能**，但后端运行时仍需要最小依赖模块：`system`（鉴权/用户/权限）+ `infra`（基础能力）+ `bpm`。

---

## 1. 项目整体结构（与 BPM 的关系）

仓库的后端主要有两种组合方式：

1. **单体容器：`yudao-server`（推荐做最小化部署）**  
   `yudao-server` 本身是“空壳容器”，通过在 `yudao-server/pom.xml` 引入哪些 `yudao-module-xxx-server` 来决定启用哪些业务模块。
   - 单体模式下，`yudao-server` 默认禁用 Nacos（见 `yudao-server/src/main/resources/application.yaml`），因此不强依赖注册/配置中心。

2. **微服务：`yudao-gateway` + 多个 `xxx-server`**  
   例如 `system-server`、`infra-server`、`bpm-server` 等，走 Nacos 注册发现与配置中心（见各模块 `application.yaml` / `application-local.yaml`）。

---

## 2. “只部署 BPM”为什么仍需要其它模块（依赖说明）

### 2.1 BPM 模块的代码级依赖

从 `yudao-module-bpm/yudao-module-bpm-server/pom.xml` 可以看到 BPM 的关键依赖：

- **必需（强依赖）**
  - `yudao-module-system-api`：BPM 需要用户、部门、角色等基础数据与权限体系（否则无法正常登录/分配审批人/组织架构策略等）
  - MyBatis / Redis / Security / RPC 等基础 starter
  - Flowable 引擎：`flowable-spring-boot-starter-process`
- **可选（能力依赖/场景依赖）**
  - `yudao-module-oa-api`：注释写明用于 Feign 通知业务服务；不部署 OA 时，相关联动/通知能力可能不可用或需要关闭
  - `yudao-spring-boot-starter-mq`：消息队列通知能力；不启用时可不准备 MQ

### 2.2 最小“运行模块集合”（建议）

如果你的目标是“只上线工作流中心（模型/表单/待办/已办/审批等）”，建议最小后端模块集合为：

- `yudao-module-system-server`（必选）
- `yudao-module-infra-server`（必选，作为基础能力底座）
- `yudao-module-bpm-server`（目标模块）

> 说明：理论上你可以尝试只带 `system + bpm`，但在本仓库的实际工程组织里，`infra` 基本作为通用能力底座默认共用，最小化方案里建议保留它，避免运行期缺少通用能力导致的隐性报错。

---

## 3. 最小外部中间件依赖（BPM 只跑核心功能）

### 3.1 必需

- **MySQL 8.x**（业务库 + Flowable 引擎表）
- **Redis 7.x**（缓存、Token、会话等）

### 3.2 可选（不做可不部署）

- MQ（RocketMQ / RabbitMQ / Kafka）：仅在你确实启用了 MQ 通知/异步任务时需要
- XXL-JOB：默认可关闭（项目里也常见 `xxl.job.enabled: false`）
- Nacos：仅微服务模式需要；单体 `yudao-server` 默认禁用

---

## 4. 方案 A（推荐）：单体 `yudao-server` 按需打包，只启用 System + Infra + BPM

这是“最小化上线 BPM”最省心的方式：**只部署一个后端应用**（外加 MySQL、Redis）。

### 4.1 调整 `yudao-server` 的模块依赖

编辑 `yudao-server/pom.xml`，将依赖控制成：

- 保留：
  - `yudao-module-system-server`
  - `yudao-module-infra-server`
  - `yudao-module-bpm-server`
- 注释掉其它模块（例如当前默认引入的 `yudao-module-oa-server`、`yudao-module-hrm-server` 等）

### 4.2 构建单体 Jar

在仓库根目录执行（推荐使用 `boot` profile）：

```bash
mvn -Pboot -pl yudao-server -am clean package -DskipTests
```

产物：

- `yudao-server/target/yudao-server.jar`

### 4.3 初始化数据库

单体部署推荐直接沿用仓库提供的初始化脚本（包含 system/infra 的表结构与初始化数据，同时包含大量字典、菜单等数据）：

- `sql/mysql/ruoyi-vue-pro.sql`
- `sql/mysql/quartz.sql`

Flowable 引擎表默认会在首次启动时自动建表（相关配置在 `yudao-server/src/main/resources/application.yaml` / 各模块 `application.yaml` 的 `flowable.database-schema-update: true`）。

### 4.4 启动（Jar 方式）

```bash
java -jar yudao-server/target/yudao-server.jar --spring.profiles.active=prod
```

生产环境数据库与 Redis 通常通过环境变量覆盖（见 `yudao-server/src/main/resources/application-prod.yaml`），常用变量：

- `MYSQL_URL` / `MYSQL_USERNAME` / `MYSQL_PASSWORD`
- `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASSWORD`

### 4.5 启动（Docker Compose 方式）

仓库自带单体编排文件：

- `yudao-server/docker-compose.yml`（MySQL + Redis + yudao-server）

构建镜像（在打包 Jar 之后）：

```bash
docker build -t yudao-server:latest ./yudao-server
```

更完整的“把初始化 SQL 放进 `/data/mysql/init`、一键启动”的落地流程可参考：

- `docs/后端部署指南.md`

---

## 5. 菜单最小化：只保留“系统管理 / 基础设施 / 工作流程”

`sql/mysql/ruoyi-vue-pro.sql` 往往会初始化很多业务模块菜单。你虽然不部署它们，但前端仍可能看到入口，点击后报错。

推荐做法：**软删除未部署模块的菜单**（`system_menu.deleted = b'1'`），只保留：

- `系统管理`
- `基础设施`
- `工作流程`（BPM 顶级菜单在该 SQL 中常见名为“工作流程”，path 为 `/bpm`）

示例步骤（先查再改）：

```sql
-- 1) 查看顶级菜单
SELECT id, name, path
FROM system_menu
WHERE parent_id = 0 AND deleted = b'0'
ORDER BY sort;

-- 2) 找出你要保留的顶级菜单 id（建议手动确认）
--    例如：系统管理、基础设施、工作流程

-- 3) 软删除其它顶级菜单（示例：按 name 排除保留项）
UPDATE system_menu
SET deleted = b'1'
WHERE parent_id = 0
  AND name NOT IN ('系统管理', '基础设施', '工作流程');
```

> 如果你希望把“被删除顶级菜单的所有子菜单”一并软删除，需要做递归处理；建议在执行前先备份数据库或在测试库验证。

---

## 6. 方案 B（可选）：微服务最小集合（Gateway + System + Infra + BPM）

当你明确要微服务形态时，最小服务集合通常是：

- `gateway-server`（`yudao-gateway`）
- `system-server`（`yudao-module-system-server`）
- `infra-server`（`yudao-module-infra-server`）
- `bpm-server`（`yudao-module-bpm-server`）
- 以及 Nacos + MySQL + Redis

构建示例：

```bash
mvn -Pcloud -pl \
  yudao-gateway,\
  yudao-module-system/yudao-module-system-server,\
  yudao-module-infra/yudao-module-infra-server,\
  yudao-module-bpm/yudao-module-bpm-server \
  -am clean package -DskipTests
```

注意事项：

- 需要正确配置 Nacos（见各模块 `application-local.yaml`）
- 若在同一台宿主机多进程启动，可能遇到端口冲突；可用 `--server.port=xxxxx` 覆盖
- 网关路由可按需精简：`yudao-gateway/src/main/resources/application.yaml`

---

## 7. 最小化验收清单

- `yudao-server`（单体）健康检查：`/actuator/health`
- Swagger：`/swagger-ui` 或 `/v3/api-docs`
- 登录后是否能看到“工作流程”菜单（`/bpm`）
- 流程模型/表单/待办/已办/我的申请/审批中心等核心页面是否可用

