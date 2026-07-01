# 在线商城系统 - Online Mall

> **软件服务工程期末大作业**  
> 基于微服务架构的在线商城系统  
> 技术栈: Vue 3 + Spring Cloud + Nacos + Sentinel + Seata + Docker

---

## 项目结构

```
online-mall/
├── frontend/                # Vue 3 前端
├── user-service/            # 用户服务（注册/登录/JWT）
├── product-service/         # 商品服务（CRUD/库存管理）
├── order-service/           # 订单服务（跨服务调用/分布式事务/幂等性）★
├── payment-service/         # 支付服务（支付/退款）★
├── gateway-service/         # API网关（路由/鉴权/限流）★
├── sql/                     # 数据库初始化脚本
├── seata/                   # Seata 配置
├── sentinel/                # Sentinel 规则
├── nacos/                   # Nacos 配置说明
├── docker-compose.yml       # 容器编排
├── 测试方案.md               # 故障测试方案
└── API文档-PersonB.md       # API 文档
```

★ = Person B 负责

---

## 快速启动

### 前置条件

- Docker >= 20.10
- Docker Compose >= 2.0
- JDK 11+（本地开发）

### 一键部署

```bash
# 1. 克隆项目
git clone <repo-url>
cd online-mall

# 2. 启动所有服务（首次启动需构建镜像，约5-10分钟）
docker-compose up -d

# 3. 查看服务状态
docker-compose ps

# 4. 查看日志
docker-compose logs -f gateway-service
```

### 访问地址

| 服务 | 地址 |
|------|------|
| API 网关 | http://localhost:8080 |
| Nacos 控制台 | http://localhost:8848/nacos (nacos/nacos) |
| Sentinel 控制台 | http://localhost:8858 (sentinel/sentinel) |
| Seata 控制台 | http://localhost:7091 (seata/seata) |
| 前端页面 | http://localhost |

---

## 微服务架构

```
                    ┌─────────────────┐
                    │  Vue 3 前端      │
                    └────────┬────────┘
                             │ HTTP
                    ┌────────▼────────┐
                    │  API Gateway     │  ← JWT鉴权 / 限流
                    │  (8080)          │
                    └──┬──┬──┬──┬─────┘
                       │  │  │  │
          ┌────────────┼──┼──┼──┼────────────┐
          ▼            ▼  ▼  ▼  ▼            ▼
    ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐
    │ 用户服务  │ │ 商品服务  │ │ 订单服务  │ │ 支付服务  │
    │ (8081)   │ │ (8082)   │ │ (8083)   │ │ (8084)   │
    └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘
         │            │            │            │
         └────────────┴─────┬──────┴────────────┘
                            │
              ┌─────────────┼─────────────┐
              ▼             ▼             ▼
        ┌─────────┐  ┌──────────┐  ┌─────────┐
        │  Nacos  │  │ Sentinel │  │  Seata  │
        │ 注册中心 │  │  容错限流 │  │ 分布式事务│
        └─────────┘  └──────────┘  └─────────┘
```

---

## 核心业务流程（Person B 负责）

### 下单流程（跨服务）

```
Client → Gateway → order-service
                      │
                      ├─(Feign)→ user-service    [验证用户]
                      ├─(Feign)→ product-service  [查询商品信息]
                      ├─(Seata AT)────────────────[分布式事务]
                      │   ├─ 创建订单 & 订单项
                      │   └─ (Feign)→ product-service.deductStock()
                      │
                      └─ 返回订单信息

失败补偿: Seata 自动回滚 → product-service.rollbackStock()
幂等保证: orderToken → t_order_token 去重
降级保护: Sentinel Fallback → "商品服务繁忙"
```

### 取消订单流程

```
order-service.cancelOrder()
  ├─ 校验订单状态 (PENDING)
  ├─ (Feign)→ product-service.rollbackStock()  [回滚库存]
  └─ 更新订单状态 → CANCELLED
```

---

## 故障测试

详见 [测试方案.md](./测试方案.md)

| 测试场景 | 预期结果 |
|----------|----------|
| 关停 product-service → 下单 | Sentinel 降级，返回 "商品服务繁忙" |
| 重复提交相同 orderToken | 幂等拦截，返回 "请勿重复提交" |
| 模拟异常 → 下单 | Seata 回滚，库存恢复 |

---

## 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.7.18 |
| 微服务框架 | Spring Cloud | 2021.0.9 |
| 注册发现/配置 | Nacos | 2.2.3 |
| 服务调用 | OpenFeign + LoadBalancer | - |
| 网关 | Spring Cloud Gateway | - |
| 容错限流 | Sentinel | 1.8.6 |
| 分布式事务 | Seata (AT模式) | 1.7.1 |
| 数据库 | MySQL | 8.0 |
| ORM | MyBatis-Plus | 3.5.3.1 |
| 容器化 | Docker + Docker Compose | - |
| 鉴权 | JWT (jjwt) | 0.11.5 |
| 前端 | Vue 3 + Element Plus | - |

---

## 团队分工

| 成员 | 职责 |
|------|------|
| **Person A** | 前端(Vue3) + user-service + product-service |
| **Person B** | order-service + payment-service + gateway + DevOps |
