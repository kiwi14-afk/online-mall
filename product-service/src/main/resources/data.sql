-- Seed data: categories and products (runs on H2 startup)
MERGE INTO t_category (id, name, create_time) VALUES (1, '电子产品', NOW());
MERGE INTO t_category (id, name, create_time) VALUES (2, '图书', NOW());
MERGE INTO t_category (id, name, create_time) VALUES (3, '服饰', NOW());
MERGE INTO t_category (id, name, create_time) VALUES (4, '食品', NOW());
MERGE INTO t_category (id, name, create_time) VALUES (5, '游戏', NOW());
MERGE INTO t_category (id, name, create_time) VALUES (6, '美妆', NOW());

MERGE INTO t_product (id, name, description, price, stock, image_url, category_id, status, create_time, update_time)
VALUES (1, '机械键盘 Cherry MX', 'Cherry MX 青轴，铝合金机身，RGB 背光，87 键紧凑布局', 899.00, 45, NULL, 1, 1, NOW(), NOW());

MERGE INTO t_product (id, name, description, price, stock, image_url, category_id, status, create_time, update_time)
VALUES (2, '极简台灯', 'LED 护眼台灯，三档色温调节，金属灯臂，触控开关', 299.00, 28, NULL, 1, 1, NOW(), NOW());

MERGE INTO t_product (id, name, description, price, stock, image_url, category_id, status, create_time, update_time)
VALUES (3, '算法导论 第四版', 'Thomas Cormen 经典著作，计算机科学必读教材，1312 页精装', 128.00, 50, NULL, 2, 1, NOW(), NOW());

MERGE INTO t_product (id, name, description, price, stock, image_url, category_id, status, create_time, update_time)
VALUES (4, '纯棉短袖 T 恤', '100% 精梳棉，宽松版型，多色可选，夏季基础款', 89.00, 120, NULL, 3, 1, NOW(), NOW());

MERGE INTO t_product (id, name, description, price, stock, image_url, category_id, status, create_time, update_time)
VALUES (5, '挂耳咖啡 30 袋装', '埃塞俄比亚耶加雪菲，中浅烘焙，花果香气，每袋 10g', 69.00, 200, NULL, 4, 1, NOW(), NOW());

MERGE INTO t_product (id, name, description, price, stock, image_url, category_id, status, create_time, update_time)
VALUES (6, '无线游戏手柄', '兼容 Switch/PC/iOS，霍尔摇杆，陀螺仪体感，20 小时续航', 259.00, 35, NULL, 5, 1, NOW(), NOW());

MERGE INTO t_product (id, name, description, price, stock, image_url, category_id, status, create_time, update_time)
VALUES (7, '面霜 50g', '神经酰胺修护面霜，适合敏感肌，无香精，零刺激', 159.00, 60, NULL, 6, 1, NOW(), NOW());

MERGE INTO t_product (id, name, description, price, stock, image_url, category_id, status, create_time, update_time)
VALUES (8, '蓝牙降噪耳机', '主动降噪 42dB，LDAC 高清传输，60 小时续航，折叠便携', 599.00, 18, NULL, 1, 1, NOW(), NOW());
