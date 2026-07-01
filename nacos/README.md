# ============================================
# Nacos 配置中心 - 需要推送到 Nacos 的配置项
# ============================================

# seataServer.properties 配置
# Data ID: seataServer.properties
# Group: SEATA_GROUP
# 内容见：./seata/seataServer.properties
# 
# 启动后执行：
# 1. 登录 Nacos Console: http://localhost:8848/nacos
# 2. 进入"配置管理" -> "配置列表"
# 3. 选择 public 命名空间
# 4. 点击 "+" 新建配置
#    - Data ID: seataServer.properties
#    - Group: SEATA_GROUP
#    - 配置格式: Properties
#    - 配置内容: 粘贴 seata/seataServer.properties 的文件内容
# 5. 发布
