/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80039
 Source Host           : localhost:3306
 Source Schema         : sale

 Target Server Type    : MySQL
 Target Server Version : 80039
 File Encoding         : 65001

 Date: 11/04/2025 16:41:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_base_place
-- ----------------------------
DROP TABLE IF EXISTS `sys_base_place`;
CREATE TABLE `sys_base_place`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产地名称',
  `user_id` bigint NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_base_place
-- ----------------------------
INSERT INTO `sys_base_place` VALUES (1, '北京', 2);
INSERT INTO `sys_base_place` VALUES (2, '上海', 3);
INSERT INTO `sys_base_place` VALUES (4, '广东', 3);
INSERT INTO `sys_base_place` VALUES (5, '北京', 24);

-- ----------------------------
-- Table structure for sys_base_price_level
-- ----------------------------
DROP TABLE IF EXISTS `sys_base_price_level`;
CREATE TABLE `sys_base_price_level`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `level_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '等级名称（A级、B级等）',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '说明',
  `user_id` bigint NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_base_price_level
-- ----------------------------
INSERT INTO `sys_base_price_level` VALUES (1, 'A', '品质优', 2);
INSERT INTO `sys_base_price_level` VALUES (2, 'B', '品质良', 2);
INSERT INTO `sys_base_price_level` VALUES (3, 'A', '极品', 3);
INSERT INTO `sys_base_price_level` VALUES (4, 'B', '良好', 3);
INSERT INTO `sys_base_price_level` VALUES (5, 'A', '好好好', 24);

-- ----------------------------
-- Table structure for sys_file
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件名称',
  `url` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '下载链接',
  `type` varchar(256) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件类型',
  `md5` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '文件md5',
  `size` bigint NULL DEFAULT NULL COMMENT '文件大小',
  `enable` tinyint NULL DEFAULT 1 COMMENT '是否禁用(1-启用, 1-禁用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除(0-未删, 1-已删)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 240 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci COMMENT = '文件上传的列表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_file
-- ----------------------------
INSERT INTO `sys_file` VALUES (185, 'haha.png', 'http://localhost:9091/file/e9361944d9ab4722b50e382087c068eb.png', 'png', 'bdd105b70cfff4e65ee42125f33f8ea0', 5, 1, '2025-04-08 16:08:55', '2025-04-08 16:08:55', 0);
INSERT INTO `sys_file` VALUES (221, '987f380d7297fe7659721cc2c95b6fe13c6300ff1e4e6-o9oQT6_fw1200.jpg', 'http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg', 'jpg', 'babea7efc6ae9ae0ed3c49ef9ec8d057', 123, 1, '2025-04-10 16:50:39', '2025-04-10 16:50:39', 0);

-- ----------------------------
-- Table structure for sys_order
-- ----------------------------
DROP TABLE IF EXISTS `sys_order`;
CREATE TABLE `sys_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `buyer_id` bigint NOT NULL COMMENT '买家ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID（种植户）',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '订单状态（pending待付款、paid已付款、shipped已发货、completed已完成、cancelled已取消、refunded已退款、refunding退款中，returned已退货、returning退货中）',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `inventory_id` bigint NOT NULL COMMENT '库存id',
  `quantity` int NOT NULL COMMENT '数量',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '单价',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `buyer_id`(`buyer_id` ASC) USING BTREE,
  INDEX `seller_id`(`seller_id` ASC) USING BTREE,
  CONSTRAINT `sys_order_ibfk_1` FOREIGN KEY (`buyer_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_order_ibfk_2` FOREIGN KEY (`seller_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_order
-- ----------------------------
INSERT INTO `sys_order` VALUES (2, '1744269576976lut4r41v50p', 3, 2, 40000.00, 'cancelled', 1, 1, 100, 400.00, '2025-04-10 15:19:37', '2025-04-10 15:22:07');
INSERT INTO `sys_order` VALUES (3, '1744272175750i2zhy30wx98', 3, 2, 350000.00, 'cancelled', 1, 2, 1000, 350.00, '2025-04-10 16:02:55', '2025-04-10 16:05:04');
INSERT INTO `sys_order` VALUES (4, '17442723185269zlgz655ch', 3, 2, 350000.00, 'refunded', 1, 2, 1000, 350.00, '2025-04-10 16:05:18', '2025-04-10 16:15:14');
INSERT INTO `sys_order` VALUES (5, '1744272938739qdd1c5zxu7', 3, 2, 350000.00, 'returned', 1, 2, 1000, 350.00, '2025-04-10 16:15:38', '2025-04-10 16:17:37');
INSERT INTO `sys_order` VALUES (6, '1744354933835qwudzw1hlqc', 24, 2, 35000.00, 'returned', 1, 2, 100, 350.00, '2025-04-11 15:02:13', '2025-04-11 15:40:49');
INSERT INTO `sys_order` VALUES (7, '1744355096543fsxab6h2s9c', 24, 2, 314650.00, 'returned', 1, 2, 899, 350.00, '2025-04-11 15:04:56', '2025-04-11 15:40:47');
INSERT INTO `sys_order` VALUES (8, '17443557024190ci2xiu4wsd', 3, 2, 400.00, 'refunded', 1, 1, 1, 400.00, '2025-04-11 15:15:02', '2025-04-11 15:39:30');
INSERT INTO `sys_order` VALUES (9, '1744355707538bqjvu9lgzu6', 3, 2, 400.00, 'refunded', 1, 1, 1, 400.00, '2025-04-11 15:15:07', '2025-04-11 15:39:29');
INSERT INTO `sys_order` VALUES (10, '17443557245980liqiuqxps1h', 3, 2, 40000.00, 'refunded', 1, 1, 100, 400.00, '2025-04-11 15:15:24', '2025-04-11 15:39:28');
INSERT INTO `sys_order` VALUES (11, '1744355735425aybipdn3x26', 3, 2, 400.00, 'refunded', 1, 1, 1, 400.00, '2025-04-11 15:15:35', '2025-04-11 15:39:26');
INSERT INTO `sys_order` VALUES (12, '1744355956020m1ro75q2zok', 3, 2, 400.00, 'refunded', 1, 1, 1, 400.00, '2025-04-11 15:19:16', '2025-04-11 15:39:25');
INSERT INTO `sys_order` VALUES (13, '1744356058813t9fegppzd3m', 3, 2, 400.00, 'refunded', 1, 1, 1, 400.00, '2025-04-11 15:20:58', '2025-04-11 15:39:24');
INSERT INTO `sys_order` VALUES (14, '1744356154328bdi1iujq5oj', 3, 2, 16000.00, 'refunded', 1, 1, 40, 400.00, '2025-04-11 15:22:34', '2025-04-11 15:39:14');
INSERT INTO `sys_order` VALUES (15, '1744356194884iac47nztphb', 3, 2, 400.00, 'refunded', 1, 1, 1, 400.00, '2025-04-11 15:24:36', '2025-04-11 15:39:13');
INSERT INTO `sys_order` VALUES (16, '1744356289544ck0vg1qko3u', 3, 2, 400.00, 'cancelled', 1, 1, 1, 400.00, '2025-04-11 15:24:49', '2025-04-11 15:38:12');
INSERT INTO `sys_order` VALUES (17, '1744356420360qfqpupmgrd', 3, 2, 400.00, 'refunded', 1, 1, 1, 400.00, '2025-04-11 15:27:00', '2025-04-11 15:39:12');
INSERT INTO `sys_order` VALUES (18, '1744356949765al195fe32gs', 3, 2, 400.00, 'cancelled', 1, 1, 1, 400.00, '2025-04-11 15:35:49', '2025-04-11 15:38:09');
INSERT INTO `sys_order` VALUES (19, '1744356983953aap5rg5g86q', 3, 2, 400.00, 'cancelled', 1, 1, 1, 400.00, '2025-04-11 15:36:23', '2025-04-11 15:38:08');
INSERT INTO `sys_order` VALUES (20, '1744357053097x2jylnxzvli', 3, 2, 4000.00, 'refunded', 1, 1, 10, 400.00, '2025-04-11 15:37:33', '2025-04-11 15:39:10');
INSERT INTO `sys_order` VALUES (21, '1744357065162vfwf5tvcsmk', 3, 2, 5600.00, 'refunded', 1, 1, 14, 400.00, '2025-04-11 15:37:45', '2025-04-11 15:39:08');
INSERT INTO `sys_order` VALUES (22, '17443570707715vu32hpw1g9', 3, 2, 350.00, 'refunded', 1, 2, 1, 350.00, '2025-04-11 15:37:50', '2025-04-11 15:39:07');
INSERT INTO `sys_order` VALUES (23, '1744357075211orazwg8me3k', 3, 2, 400.00, 'cancelled', 1, 1, 1, 400.00, '2025-04-11 15:37:55', '2025-04-11 15:38:02');
INSERT INTO `sys_order` VALUES (24, '1744357226445m0o3jxbq3ka', 24, 2, 40000.00, 'paid', 1, 1, 100, 400.00, '2025-04-11 15:40:26', '2025-04-11 15:41:30');
INSERT INTO `sys_order` VALUES (25, '1744357312527rzgx1swrsp', 24, 2, 400.00, 'paid', 1, 1, 1, 400.00, '2025-04-11 15:41:52', '2025-04-11 15:41:54');
INSERT INTO `sys_order` VALUES (26, '17443573245046r5xq3r8ln5', 24, 2, 400.00, 'paid', 1, 1, 1, 400.00, '2025-04-11 15:42:04', '2025-04-11 15:42:10');

-- ----------------------------
-- Table structure for sys_order_p
-- ----------------------------
DROP TABLE IF EXISTS `sys_order_p`;
CREATE TABLE `sys_order_p`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `buyer_id` bigint NOT NULL COMMENT '买家ID',
  `seller_id` bigint NOT NULL COMMENT '卖家ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '订单状态（pending待付款、paid已付款、shipped已发货、completed已完成、cancelled已取消、refunded已退款、refunding退款中，returned已退货、returning退货中）',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `inventory_id` bigint NOT NULL COMMENT '库存id',
  `quantity` int NOT NULL COMMENT '数量',
  `unit_price` decimal(10, 2) NOT NULL COMMENT '单价',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `buyer_id`(`buyer_id` ASC) USING BTREE,
  INDEX `seller_id`(`seller_id` ASC) USING BTREE,
  CONSTRAINT `sys_order_p_ibfk_1` FOREIGN KEY (`buyer_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_order_p_ibfk_2` FOREIGN KEY (`seller_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_order_p
-- ----------------------------
INSERT INTO `sys_order_p` VALUES (13, '1744357523975xasl4sfh0h', 4, 24, 150.00, 'paid', 9, 8, 10, 15.00, '2025-04-11 15:45:24', '2025-04-11 15:45:24');

-- ----------------------------
-- Table structure for sys_product
-- ----------------------------
DROP TABLE IF EXISTS `sys_product`;
CREATE TABLE `sys_product`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '种植户ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `origin_place_id` bigint NOT NULL COMMENT '产地ID',
  `listed_time` date NOT NULL COMMENT '预计上市时间',
  `cover_image` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '封面图片',
  `images` json NULL COMMENT '多张现货图片',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1上架，0下架',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `origin_place_id`(`origin_place_id` ASC) USING BTREE,
  CONSTRAINT `sys_product_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_product_ibfk_2` FOREIGN KEY (`origin_place_id`) REFERENCES `sys_base_place` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_product
-- ----------------------------
INSERT INTO `sys_product` VALUES (1, 2, '测试商品', 1, '2025-04-09', 'http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg', '[{\"uid\": \"vc-upload-1744168317890-5\", \"url\": \"http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg\", \"name\": \"987f380d7297fe7659721cc2c95b6fe13c6300ff1e4e6-o9oQT6_fw1200.jpg\", \"type\": \"image/jpeg\", \"status\": \"done\", \"response\": \"http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg\"}, {\"uid\": \"vc-upload-1744247094882-4\", \"url\": \"http://localhost:9091/file/e9361944d9ab4722b50e382087c068eb.png\", \"name\": \"haha.png\", \"type\": \"image/png\", \"status\": \"done\", \"response\": \"http://localhost:9091/file/e9361944d9ab4722b50e382087c068eb.png\"}]', '包甜，好吃', 1, '2025-04-08 17:25:42', '2025-04-10 16:41:17');

-- ----------------------------
-- Table structure for sys_product_inventory
-- ----------------------------
DROP TABLE IF EXISTS `sys_product_inventory`;
CREATE TABLE `sys_product_inventory`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `price_level_id` bigint NOT NULL COMMENT '价格等级ID',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `total_stock` int NOT NULL COMMENT '总库存（斤）',
  `reserved_stock` int NULL DEFAULT 0 COMMENT '已预定库存',
  `ladder_discount` json NULL COMMENT '阶梯折扣规则(JSON，如 [{\"amount\":100,\"discount\":0.95},{\"amount\":500,\"discount\":0.9}])',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `price_level_id`(`price_level_id` ASC) USING BTREE,
  CONSTRAINT `sys_product_inventory_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `sys_product` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_product_inventory_ibfk_2` FOREIGN KEY (`price_level_id`) REFERENCES `sys_base_price_level` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_product_inventory
-- ----------------------------
INSERT INTO `sys_product_inventory` VALUES (1, 1, 1, 400.00, 9897, 0, '[{\"amount\": 100, \"discount\": 0.95}, {\"amount\": 200, \"discount\": 0.9}]');
INSERT INTO `sys_product_inventory` VALUES (2, 1, 2, 350.00, 10999, 0, '[]');

-- ----------------------------
-- Table structure for sys_product_inventory_p
-- ----------------------------
DROP TABLE IF EXISTS `sys_product_inventory_p`;
CREATE TABLE `sys_product_inventory_p`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `price_level_id` bigint NOT NULL COMMENT '价格等级ID',
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `total_stock` int NOT NULL COMMENT '总库存（斤）',
  `reserved_stock` int NULL DEFAULT 0 COMMENT '已预定库存',
  `ladder_discount` json NULL COMMENT '阶梯折扣规则(JSON，如 [{\"amount\":100,\"discount\":0.95},{\"amount\":500,\"discount\":0.9}])',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `product_id`(`product_id` ASC) USING BTREE,
  INDEX `price_level_id`(`price_level_id` ASC) USING BTREE,
  CONSTRAINT `sys_product_inventory_p_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `sys_product_p` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_product_inventory_p_ibfk_2` FOREIGN KEY (`price_level_id`) REFERENCES `sys_base_price_level` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_product_inventory_p
-- ----------------------------
INSERT INTO `sys_product_inventory_p` VALUES (7, 5, 3, 10.00, 888, 0, '[{\"amount\": 100, \"discount\": 0.95}, {\"amount\": 200, \"discount\": 0.92}]');
INSERT INTO `sys_product_inventory_p` VALUES (8, 9, 5, 15.00, 8000, 10, '[{\"amount\": 100, \"discount\": 0.95}, {\"amount\": 150, \"discount\": 0.9}]');

-- ----------------------------
-- Table structure for sys_product_p
-- ----------------------------
DROP TABLE IF EXISTS `sys_product_p`;
CREATE TABLE `sys_product_p`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '种植户ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `origin_place_id` bigint NOT NULL COMMENT '产地ID',
  `listed_time` date NOT NULL COMMENT '预计上市时间',
  `cover_image` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '封面图片',
  `images` json NULL COMMENT '多张现货图片',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '描述',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1上架，0下架',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `origin_place_id`(`origin_place_id` ASC) USING BTREE,
  CONSTRAINT `sys_product_p_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `sys_product_p_ibfk_2` FOREIGN KEY (`origin_place_id`) REFERENCES `sys_base_place` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_product_p
-- ----------------------------
INSERT INTO `sys_product_p` VALUES (2, 3, '好来橘2', 2, '2025-04-10', 'http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg', '[{\"uid\": \"vc-upload-1744274876407-5\", \"url\": \"http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg\", \"name\": \"987f380d7297fe7659721cc2c95b6fe13c6300ff1e4e6-o9oQT6_fw1200.jpg\", \"type\": \"image/jpeg\", \"status\": \"done\", \"response\": \"http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg\"}, {\"uid\": \"vc-upload-1744274876407-7\", \"url\": \"http://localhost:9091/file/e9361944d9ab4722b50e382087c068eb.png\", \"name\": \"haha.png\", \"type\": \"image/png\", \"status\": \"done\", \"response\": \"http://localhost:9091/file/e9361944d9ab4722b50e382087c068eb.png\"}]', '不好吃你打我', 0, '2025-04-10 16:49:36', '2025-04-10 16:49:36');
INSERT INTO `sys_product_p` VALUES (5, 3, '1', 2, '2025-04-03', 'http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg', '[{\"uid\": \"vc-upload-1744275302312-5\", \"url\": \"http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg\", \"name\": \"987f380d7297fe7659721cc2c95b6fe13c6300ff1e4e6-o9oQT6_fw1200.jpg\", \"status\": \"done\", \"response\": \"http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg\"}]', '123', 1, '2025-04-10 16:55:17', '2025-04-10 17:03:00');
INSERT INTO `sys_product_p` VALUES (9, 24, '测', 5, '2025-04-11', 'http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg', '[{\"uid\": \"vc-upload-1744357193428-7\", \"url\": \"http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg\", \"name\": \"987f380d7297fe7659721cc2c95b6fe13c6300ff1e4e6-o9oQT6_fw1200.jpg\", \"status\": \"done\", \"response\": \"http://localhost:9091/file/516b34ee4b394d2eb07ac71b79e192f2.jpg\"}, {\"uid\": \"vc-upload-1744357193428-9\", \"url\": \"http://localhost:9091/file/e9361944d9ab4722b50e382087c068eb.png\", \"name\": \"haha.png\", \"status\": \"done\", \"response\": \"http://localhost:9091/file/e9361944d9ab4722b50e382087c068eb.png\"}]', '测测测测测测测测测测测', 1, '2025-04-11 15:43:57', '2025-04-11 15:43:57');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '名称',
  `description` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '描述',
  `flag` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '唯一标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '管理员', '管理员', 'admin');
INSERT INTO `sys_role` VALUES (2, '种植户', '种植户', 'growers');
INSERT INTO `sys_role` VALUES (4, '批发商', '批发商', 'wholesaler');
INSERT INTO `sys_role` VALUES (5, '零售商', '零售商', 'retailer');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '密码',
  `nickname` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '昵称',
  `avatar_url` longtext CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL COMMENT '头像',
  `email` varchar(20) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `role_id` int NULL DEFAULT NULL COMMENT '角色 ',
  `status` tinyint NULL DEFAULT 1 COMMENT '是否有效 1有效 0无效',
  `role` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL COMMENT '角色',
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb3 COLLATE = utf8mb3_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '8hbtPFwp0zKdNwWMZF27gw==', '管理员', NULL, '', 1, 1, 'admin', '18154515153');
INSERT INTO `sys_user` VALUES (2, 'zzw', 'Rip/nu11DMmrM77ndrRafA==', '种植户', NULL, '', 2, 1, 'growers', '18154515153');
INSERT INTO `sys_user` VALUES (3, 'pfs', 'zOaDBTlUURn+JVZN/TN/VQ==', '批发商', NULL, NULL, 4, 1, 'wholesaler', '18154515153');
INSERT INTO `sys_user` VALUES (4, 'lss', 'c9L+93MJ1/vOhe9wnWe89A==', '零售商', NULL, NULL, 5, 1, 'retailer', '18154515153');
INSERT INTO `sys_user` VALUES (23, 'cs', 'VzmjiI1rvmI4Lo6gxeTtMA==', '测试', NULL, NULL, 5, 1, 'retailer', '18874484488');
INSERT INTO `sys_user` VALUES (24, 'pfs2', 'xSurDto+MEl5A0CBuREsog==', '测试批发商', NULL, NULL, 4, 1, 'wholesaler', '15215545544');
INSERT INTO `sys_user` VALUES (25, 'pfs3', 'L8xTDad6iLyNRgOHRSmQYA==', '良心商家', NULL, NULL, 4, 1, 'wholesaler', '15215545544');

SET FOREIGN_KEY_CHECKS = 1;
