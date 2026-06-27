/*
 Navicat Premium Data Transfer

 Source Server         : bs
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : 47.108.213.220:3306
 Source Schema         : cloud-test

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 09/02/2026 14:22:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admins
-- ----------------------------
DROP TABLE IF EXISTS `admins`;
CREATE TABLE `admins`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `admin_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员编号，100+随机数10位，唯一',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admins
-- ----------------------------
INSERT INTO `admins` VALUES (3, 'admin', '123', '2026-02-09 11:01:11', '10060607052871');

-- ----------------------------
-- Table structure for merchant_address
-- ----------------------------
DROP TABLE IF EXISTS `merchant_address`;
CREATE TABLE `merchant_address`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `merchant_id` int NOT NULL COMMENT '商家id',
  `province` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `created_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `city` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `district` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地区',
  `detail_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细位置',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of merchant_address
-- ----------------------------
INSERT INTO `merchant_address` VALUES (1, 1, '广东省', '2026-02-05 17:27:53', '深圳市', '南山区', '科技园南区高新南一道创维大厦A座10层1008室（近地铁2号线科苑站B口）', '2026-02-05 17:27:53');

-- ----------------------------
-- Table structure for merchants
-- ----------------------------
DROP TABLE IF EXISTS `merchants`;
CREATE TABLE `merchants`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` tinyint NULL DEFAULT 2,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `merchant_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商家编号，200+随机数10位，唯一',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of merchants
-- ----------------------------
INSERT INTO `merchants` VALUES (3, '商家', '123', 0, '2026-02-09 11:03:18', '20060619802946');

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_id` int NOT NULL,
  `product_id` int NOT NULL,
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `price` decimal(10, 2) NOT NULL COMMENT '商品单价',
  `quantity` int NOT NULL COMMENT '购买数量',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '总价格',
  `merchant_id` int NOT NULL,
  `user_id` int NOT NULL,
  `user_address` json NOT NULL,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单号',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order_id`(`order_id` ASC) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` VALUES (11, 8, 3, '复古水洗直筒牛仔裤', 159.00, 1, 159.00, 1, 1, '{\"city\": \"深圳市\", \"district\": \"南山区\", \"province\": \"广东省\", \"isDefault\": 0, \"receiverName\": \"张三\", \"detailAddress\": \"科技园路1号腾讯大厦10楼\", \"receiverPhone\": \"111\"}', '9593971574');
INSERT INTO `order_items` VALUES (12, 8, 4, '儿童卡通印花短袖套装', 59.90, 1, 59.90, 1, 1, '{\"city\": \"深圳市\", \"district\": \"南山区\", \"province\": \"广东省\", \"isDefault\": 0, \"receiverName\": \"张三\", \"detailAddress\": \"科技园路1号腾讯大厦10楼\", \"receiverPhone\": \"111\"}', '9593971574');
INSERT INTO `order_items` VALUES (13, 9, 4, '儿童卡通印花短袖套装', 59.90, 1, 59.90, 1, 1, '{\"city\": \"深圳市\", \"district\": \"南山区\", \"province\": \"广东省\", \"isDefault\": 0, \"receiverName\": \"张三\", \"detailAddress\": \"科技园路1号腾讯大厦10楼\", \"receiverPhone\": \"111\"}', '0071902846');
INSERT INTO `order_items` VALUES (14, 9, 5, '家用全自动迷你洗衣机', 399.00, 1, 399.00, 1, 1, '{\"city\": \"深圳市\", \"district\": \"南山区\", \"province\": \"广东省\", \"isDefault\": 0, \"receiverName\": \"张三\", \"detailAddress\": \"科技园路1号腾讯大厦10楼\", \"receiverPhone\": \"111\"}', '0071902846');
INSERT INTO `order_items` VALUES (15, 9, 3, '复古水洗直筒牛仔裤', 159.00, 1, 159.00, 1, 1, '{\"city\": \"深圳市\", \"district\": \"南山区\", \"province\": \"广东省\", \"isDefault\": 0, \"receiverName\": \"张三\", \"detailAddress\": \"科技园路1号腾讯大厦10楼\", \"receiverPhone\": \"111\"}', '0071902846');
INSERT INTO `order_items` VALUES (18, 12, 4, '儿童卡通印花短袖套装', 59.90, 1, 59.90, 1, 1, '{\"city\": \"深圳市\", \"district\": \"南山区\", \"province\": \"广东省\", \"isDefault\": 0, \"receiverName\": \"张三\", \"detailAddress\": \"科技园路1号腾讯大厦10楼\", \"receiverPhone\": \"111\"}', '0921983932');
INSERT INTO `order_items` VALUES (19, 13, 4, '儿童卡通印花短袖套装', 59.90, 1, 59.90, 1, 1, '{\"city\": \"深圳市\", \"district\": \"南山区\", \"province\": \"广东省\", \"isDefault\": 0, \"receiverName\": \"张三\", \"detailAddress\": \"科技园路1号腾讯大厦10楼\", \"receiverPhone\": \"111\"}', '3390361821');
INSERT INTO `order_items` VALUES (20, 14, 3, '复古水洗直筒牛仔裤', 159.00, 1, 159.00, 1, 1, '{\"city\": \"深圳市\", \"district\": \"南山区\", \"province\": \"广东省\", \"isDefault\": 0, \"receiverName\": \"张三\", \"detailAddress\": \"科技园路1号腾讯大厦10楼\", \"receiverPhone\": \"111\"}', '3902164482');
INSERT INTO `order_items` VALUES (21, 15, 4, '儿童卡通印花短袖套装', 59.90, 1, 59.90, 1, 1, '{\"city\": \"深圳市\", \"district\": \"南山区\", \"province\": \"广东省\", \"isDefault\": 0, \"receiverName\": \"张三\", \"detailAddress\": \"科技园路1号腾讯大厦10楼\", \"receiverPhone\": \"111\"}', '0711815594');
INSERT INTO `order_items` VALUES (22, 15, 3, '复古水洗直筒牛仔裤', 159.00, 1, 159.00, 1, 1, '{\"city\": \"深圳市\", \"district\": \"南山区\", \"province\": \"广东省\", \"isDefault\": 0, \"receiverName\": \"张三\", \"detailAddress\": \"科技园路1号腾讯大厦10楼\", \"receiverPhone\": \"111\"}', '0711815594');
INSERT INTO `order_items` VALUES (24, 17, 3, '复古水洗直筒牛仔裤', 159.00, 1, 159.00, 1, 1, '{\"city\": \"深圳市\", \"district\": \"南山区\", \"province\": \"广东省\", \"isDefault\": 0, \"receiverName\": \"张三\", \"detailAddress\": \"科技园路1号腾讯大厦10楼\", \"receiverPhone\": \"111\"}', '2922429865');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` int NOT NULL,
  `total_price` decimal(10, 2) NOT NULL COMMENT '订单总价',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态（0-待支付，1-已支付，2-已发货，3-已完成，4-已取消）',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cancel_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单取消原因',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (8, '9593971574', 1, 218.90, 3, '微信支付', '2025-09-22 21:15:59', '2025-09-23 22:04:18', NULL, NULL);
INSERT INTO `orders` VALUES (9, '0071902846', 1, 617.90, 2, '支付宝支付', '2025-09-22 21:16:47', '2025-09-23 22:03:05', NULL, NULL);
INSERT INTO `orders` VALUES (12, '0921983932', 1, 59.90, 1, '支付宝支付', '2025-09-22 22:41:32', '2025-09-23 22:01:21', NULL, NULL);
INSERT INTO `orders` VALUES (13, '3390361821', 1, 59.90, 4, NULL, '2025-09-22 22:45:39', '2025-09-22 22:46:36', NULL, NULL);
INSERT INTO `orders` VALUES (14, '3902164482', 1, 159.00, 4, NULL, '2025-09-23 14:03:10', '2025-09-23 14:03:32', NULL, NULL);
INSERT INTO `orders` VALUES (15, '0711815594', 1, 218.90, 4, NULL, '2025-09-23 21:27:51', '2025-09-25 21:22:18', '商家取消', NULL);
INSERT INTO `orders` VALUES (17, '2922429865', 1, 159.00, 4, NULL, '2025-09-23 21:31:32', '2025-09-23 21:46:59', '商家取消', '备注测试');

-- ----------------------------
-- Table structure for product_details
-- ----------------------------
DROP TABLE IF EXISTS `product_details`;
CREATE TABLE `product_details`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `product_id` int NOT NULL COMMENT '商品id',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `specification` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品参数规格',
  `image_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品图片',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of product_details
-- ----------------------------
INSERT INTO `product_details` VALUES (1, 1, '精选新疆长绒棉面料，透气吸汗不闷汗，经典圆领设计适配各种身形，多色可选（白色/黑色/浅灰），尺码覆盖S-XXL，日常通勤、休闲外出均适用，机洗不变形不褪色', '面料成分：100%棉；洗涤方式：30℃以下轻柔机洗，不可漂白，悬挂晾干；尺码参数：S码（肩宽42cm/胸围96cm）、M码（肩宽44cm/胸围100cm）、L码（肩宽46cm/胸围104cm）', '图片11111', '2025-09-25 21:09:44');
INSERT INTO `product_details` VALUES (2, 2, '采用冰感冰丝面料，接触皮肤瞬间降温，UPF50+防晒指数，有效阻隔99%紫外线，轻薄透气不闷热，长款版型可覆盖臀部，连帽设计兼顾头部防晒，颜色有浅紫、薄荷绿、奶白三色，适合日常通勤、户外游玩', '面料成分：100%锦纶；洗涤方式：手洗或轻柔机洗，水温不超过30℃，不可熨烫；尺码参数：均码（肩宽38cm/衣长85cm/胸围100cm，适合80-130斤）', '图片', '2025-09-13 16:08:14');
INSERT INTO `product_details` VALUES (3, 3, '采用10盎司新疆棉牛仔布，经过三次水洗工艺，呈现自然复古做旧感，直筒版型修饰腿型，不挑身材，腰头内置弹力织带，久坐不勒腰，裤脚可翻折穿着，适配休闲、街头等多种风格', '面料成分：98%棉+2%氨纶；洗涤方式：反面冷水机洗，避免长时间浸泡，不可暴晒；尺码参数：28码（腰围72cm/裤长102cm）、29码（腰围74cm/裤长103cm）、30码（腰围76cm/裤长104cm）', '图片', '2025-09-13 16:09:31');
INSERT INTO `product_details` VALUES (4, 4, '专为3-8岁儿童设计，上衣采用精梳棉面料，柔软亲肤无刺激，裤子为棉弹材质，弹力大不束缚活动，印有小熊、汽车、恐龙等卡通图案，色彩鲜艳不掉色，套装搭配省心，适合居家、外出游玩穿着', '面料成分：上衣100%棉，裤子95%棉+5%氨纶；洗涤方式：常温机洗，可与浅色衣物同洗，不可漂白；尺码参数：100码（身高95-105cm）、110码（身高105-115cm）、120码（身高115-125cm）', '图片', '2025-09-13 16:09:52');
INSERT INTO `product_details` VALUES (5, 5, '容量3.5kg，适合单人、母婴或内衣袜子单独清洗，支持快洗、蒸煮洗、脱水功能，蒸煮洗可高温杀菌除螨，去除率达99.9%，机身小巧不占地，支持APP远程控制，预约清洗，省水省电，噪音低于50分贝', '产品尺寸：42cm×40cm×70cm；额定功率：洗涤300W，脱水200W；适用电压：220V/50Hz；功能配置：LED显示屏、儿童锁、自动断电保护', '图片', '2025-09-13 16:10:08');
INSERT INTO `product_details` VALUES (6, 6, '半入耳式设计，佩戴舒适不易掉，单次续航5小时，搭配充电盒总续航24小时，支持蓝牙5.3，连接稳定无延迟，游戏模式延迟低至60ms，支持高清通话降噪，可单耳/双耳使用，适配安卓、苹果全系列设备', '耳机重量：单只4.5g；充电接口：Type-C；防水等级：IPX4（防泼溅）；音效配置：13mm动圈单元，支持 bass 增强模式', '图片', '2025-09-13 16:10:20');
INSERT INTO `product_details` VALUES (7, 7, '采用进口松木材质，环保清漆涂装，无异味甲醛，五层设计，每层承重15kg，可放置书籍、摆件、绿植等，书架侧面带加固横梁，防止摇晃变形，组装简单，附带详细说明书和工具，颜色有原木色、白色两种', '产品尺寸：80cm（长）×30cm（深）×120cm（高）；板材厚度：1.8cm；配件包含：螺丝包、扳手、组装图纸；适用场景：客厅、书房、卧室', '图片', '2025-09-13 16:10:30');
INSERT INTO `product_details` VALUES (8, 8, '采用600D牛津布面料，耐磨抗撕裂，支架为航空铝合金材质，承重可达150kg，折叠后体积仅为40cm×15cm×10cm，重量1.2kg，方便收纳携带，靠背105°倾斜设计，贴合人体工学，久坐不累，适合露营、野餐、钓鱼、户外演出', '展开尺寸：65cm（宽）×60cm（深）×80cm（高）；折叠尺寸：40cm×15cm×10cm；面料：600D牛津布+PU防水涂层；支架：航空铝合金', '图片', '2025-09-13 16:10:45');
INSERT INTO `product_details` VALUES (10, 10, '电脑', '512g+8g', '图片', '2025-09-25 21:07:21');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `price` float NOT NULL,
  `stock` int NULL DEFAULT 0 COMMENT '商品存货',
  `merchant_id` int NOT NULL COMMENT '商家id',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态（0-上架，1-下架',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品分类',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `merchant_id`(`merchant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, '2024夏季纯棉短袖T恤', 0, 200, 1, 1, '男装-上衣-T恤', '2025-09-13 15:08:02');
INSERT INTO `products` VALUES (2, '女士冰丝防晒开衫', 79.9, 180, 1, 0, '女装-外套-防晒衣', '2025-09-13 16:08:14');
INSERT INTO `products` VALUES (3, '复古水洗直筒牛仔裤', 159, 150, 1, 0, '男装-下装-牛仔裤', '2025-09-13 16:09:31');
INSERT INTO `products` VALUES (4, '儿童卡通印花短袖套装', 59.9, 220, 1, 0, '童装-套装-短袖套装', '2025-09-13 16:09:52');
INSERT INTO `products` VALUES (5, '家用全自动迷你洗衣机', 399, 80, 1, 0, '家电-洗衣机-迷你洗衣机', '2025-09-13 16:10:08');
INSERT INTO `products` VALUES (6, '无线蓝牙耳机', 129, 300, 1, 0, '数码-耳机-无线耳机', '2025-09-13 16:10:20');
INSERT INTO `products` VALUES (7, '实木多层书架', 259, 60, 1, 0, '家具-书架-多层书架', '2025-09-13 16:10:30');
INSERT INTO `products` VALUES (8, '便携折叠露营椅', 89, 120, 1, 0, '户外-露营装备-露营椅', '2025-09-13 16:10:45');
INSERT INTO `products` VALUES (10, '联想拯救者y7000p', 7999.99, 180, 2, 0, '电脑', '2025-09-25 21:07:21');

-- ----------------------------
-- Table structure for sys_page_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_page_permission`;
CREATE TABLE `sys_page_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `page_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '页面权限编码（鉴权核心标识，如：user_page）',
  `page_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '页面跳转路径（如：/pages/user.html）',
  `page_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '页面名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_page_code`(`page_code` ASC) USING BTREE COMMENT '权限编码唯一，避免重复'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '极简版Sa-Token页面权限表（仅用于权限-页面路径映射）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_page_permission
-- ----------------------------

-- ----------------------------
-- Table structure for user_address
-- ----------------------------
DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（逻辑关联用户表user.id）',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人手机号',
  `province` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省份',
  `city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `district` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区/县',
  `detail_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '是否默认地址：0-是，1-否',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引，优化用户地址查询效率'
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户地址表：存储用户收货地址，逻辑关联用户表，无物理外键' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_address
-- ----------------------------
INSERT INTO `user_address` VALUES (1, 1, '张三', '111', '广东省', '深圳市', '南山区', '科技园路1号腾讯大厦10楼', 0, '2025-09-17 22:15:51', '2025-09-17 23:00:54');
INSERT INTO `user_address` VALUES (2, 1, '李四', '13900139000', '江苏省', '南京市', '玄武区', '中山路18号德基广场25楼', 1, '2025-09-17 22:16:51', '2025-09-17 22:16:51');
INSERT INTO `user_address` VALUES (3, 1, '王五', '13700137000', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城30楼', 1, '2025-09-17 22:16:59', '2025-09-17 22:43:44');
INSERT INTO `user_address` VALUES (5, 2, '张三', '111', '广东省', '深圳市', '南山区', '科技园南区10栋501室', 0, '2025-09-25 20:55:32', '2025-09-25 20:58:30');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` tinyint NULL DEFAULT 0 COMMENT '0表示开启，1表示封禁',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `user_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户编号，300+随机数10位，唯一',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (3, '张三', '123', '18888888888', 0, '2026-02-09 11:02:50', '30060616973144');

SET FOREIGN_KEY_CHECKS = 1;
