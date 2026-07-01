# ============================================
# 在线商城系统 - RESTful API 文档
# Person B 负责部分: order-service, payment-service, gateway
# ============================================

---

## API 统一返回格式

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

状态码说明:
| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权/Token无效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 429 | 限流 |
| 500 | 服务器内部错误 |
| 503 | 服务降级 |

---

## 鉴权说明

除白名单路径外，所有请求需在 Header 中携带:
```
Authorization: Bearer <JWT_TOKEN>
```

Gateway 白名单路径:
- `POST /api/user/login`
- `POST /api/user/register`
- `GET /api/product/list`
- `GET /api/product/{id}`

---

## 一、order-service（订单服务）— 端口 8083

### 1.1 创建订单（核心跨服务流程）

```
POST /api/order
```

**流程**: Gateway鉴权 → order-service → (Feign) 验证用户 → (Feign) 扣库存 → 创建订单

**幂等性**: 通过 orderToken 防重复提交

**分布式事务**: Seata AT 模式保证库存扣减与订单创建一致性

**请求头**:
| Header | 值 | 说明 |
|--------|-----|------|
| Authorization | Bearer xxx | JWT Token |
| Content-Type | application/json | - |

**请求体**:
```json
{
  "orderToken": "550e8400-e29b-41d4-a716-446655440000",
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "receiverAddress": "广东省深圳市南山区科技园",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 3,
      "quantity": 1
    }
  ]
}
```

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "orderId": 1001,
    "orderNo": "17000000012345678",
    "totalAmount": 299.00,
    "status": "PENDING",
    "items": [
      {
        "id": 2001,
        "orderNo": "17000000012345678",
        "productId": 1,
        "productName": "iPhone 15",
        "productPrice": 99.00,
        "quantity": 2,
        "totalPrice": 198.00
      },
      {
        "id": 2002,
        "orderNo": "17000000012345678",
        "productId": 3,
        "productName": "AirPods Pro",
        "productPrice": 101.00,
        "quantity": 1,
        "totalPrice": 101.00
      }
    ]
  }
}
```

**幂等性冲突响应**:
```json
{
  "code": 400,
  "message": "订单已提交或Token无效，请勿重复提交",
  "data": null
}
```

**降级响应**（商品服务宕机时）:
```json
{
  "code": 503,
  "message": "商品服务繁忙，请稍后重试",
  "data": null
}
```

### 1.2 查询订单列表

```
GET /api/order/list?page=1&size=10&status=PENDING
```

**请求头**: `Authorization: Bearer xxx`

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 25,
    "records": [
      {
        "id": 1001,
        "orderNo": "17000000012345678",
        "userId": 1,
        "totalAmount": 299.00,
        "status": "PENDING",
        "receiverName": "张三",
        "receiverPhone": "13800138000",
        "receiverAddress": "广东省深圳市南山区科技园",
        "createTime": "2026-07-10T14:30:00",
        "updateTime": "2026-07-10T14:30:00"
      }
    ]
  }
}
```

### 1.3 查询订单详情

```
GET /api/order/{id}
```

**成功响应**: 包含订单基本信息 + 订单项列表

### 1.4 取消订单（回滚库存）

```
PUT /api/order/{id}/cancel
```

**前置条件**: 仅 PENDING 状态可取消

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 二、payment-service（支付服务）— 端口 8084

### 2.1 创建支付

```
POST /api/payment?orderNo=xxx&payMethod=WECHAT
```

**请求头**: `Authorization: Bearer xxx`

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "paymentId": 1,
    "paymentNo": "PAY1700000001001",
    "status": "UNPAID"
  }
}
```

### 2.2 支付回调（模拟支付成功）

```
POST /api/payment/callback?paymentNo=PAY1700000001001
```

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

### 2.3 退款处理

```
POST /api/payment/{id}/refund
```

**前置条件**: 仅 PAID 状态可退款

---

## 三、gateway-service（API网关）— 端口 8080

### 路由表

| 路由前缀 | 目标服务 | 负载均衡 |
|----------|----------|----------|
| `/api/user/**` | user-service:8081 | lb://user-service |
| `/api/product/**` | product-service:8082 | lb://product-service |
| `/api/order/**` | order-service:8083 | lb://order-service（含限流）|
| `/api/payment/**` | payment-service:8084 | lb://payment-service |

### 过滤器链

| 过滤器 | 优先级 | 作用 |
|--------|--------|------|
| JwtAuthFilter | -100 | JWT校验，白名单放行，解析userId/username并透传 |
| CorsWebFilter | — | 跨域支持 |
| RequestRateLimiter | — | order路由 QPS 限流（10/s）|
| Sentinel Gateway | — | 流量防护，超限返回429 |

---

## 数据库 ER 图

### order_db（订单数据库）

```
┌─────────────────────┐     ┌─────────────────────┐
│       t_order       │     │     t_order_item     │
├─────────────────────┤     ├─────────────────────┤
│ id (PK)             │────<│ order_id (FK)        │
│ order_no (UNIQUE)   │     │ order_no             │
│ user_id             │     │ product_id           │
│ total_amount        │     │ product_name         │
│ status              │     │ product_price        │
│ receiver_name       │     │ quantity             │
│ receiver_phone      │     │ total_price          │
│ receiver_address    │     │ create_time          │
│ create_time         │     └─────────────────────┘
│ update_time         │
└─────────────────────┘

┌─────────────────────┐
│    t_order_token    │
├─────────────────────┤
│ id (PK)             │
│ token (UNIQUE)      │
│ user_id             │
│ order_no            │
│ used                │
│ create_time         │
│ expire_time         │
└─────────────────────┘
```

### payment_db（支付数据库）

```
┌─────────────────────┐
│      t_payment      │
├─────────────────────┤
│ id (PK)             │
│ payment_no (UNIQUE) │
│ order_no            │
│ user_id             │
│ amount              │
│ status              │
│ pay_method          │
│ pay_time            │
│ refund_time         │
│ remark              │
│ create_time         │
│ update_time         │
└─────────────────────┘
```
