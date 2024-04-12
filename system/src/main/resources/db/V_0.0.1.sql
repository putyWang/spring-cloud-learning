/*
 Navicat Premium Data Transfer

 Source Server         : 192.168.3.51
 Source Server Type    : MySQL
 Source Server Version : 50736 (5.7.36)
 Source Host           : 192.168.3.51:3306
 Source Schema         : test_0

 Target Server Type    : MySQL
 Target Server Version : 50736 (5.7.36)
 File Encoding         : 65001

 Date: 05/04/2024 22:56:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL,
  `username` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '用户名',
  `nick_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '用户昵称',
  `real_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '用户真实姓名',
  `password` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '密码',
  `sex` tinyint(4) DEFAULT NULL COMMENT '性别，1为男，2为女',
  `phone` varchar(11) COLLATE utf8_bin NOT NULL COMMENT '电话号码',
  `email` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '用户邮箱',
  `status` tinyint(4) DEFAULT NULL COMMENT '用户状态，0为禁用，1为正常',
  `create_by` bigint(20) COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `version` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_index` (`username`) USING BTREE COMMENT '用户名必须唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户表';

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission` (
  `id` bigint(20) NOT NULL,
  `pid` bigint(20) NOT NULL COMMENT '父权限id',
  `name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '权限名',
  `url` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '菜单权限url',
  `permission` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '后台权限验证值',
  `permissions_type` tinyint(4) DEFAULT 2 COMMENT '0：菜单 1：导航按钮 2:普通按钮',
  `icon` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '菜单图标',
  `sort` bigint(20) COLLATE utf8_bin NOT NULL COMMENT '用户邮箱',
  `is_enabled` tinyint(4) DEFAULT 0 COMMENT '是否启用，0-未启用，1-启用',
  `create_by` bigint(20) COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `version` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `pid_index` (`pid`) USING BTREE COMMENT '父 id 索引',
  KEY `sort_index` (`sort`) USING BTREE COMMENT '排序索引'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '权限表';


-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL,
  `role_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '角色名',
  `role_code` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '角色编码',
  `index_url` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '角色首页路由',
  `role_desc` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '角色描述',
  `create_by` bigint(20) COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint(20) COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `version` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `role_code_index` (`role_code`) USING BTREE COMMENT '角色编码索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '角色表';

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission` (
  `id` bigint(20) NOT NULL,
  `role_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '角色id',
  `permission_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '权限id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_permission_id_index` (`role_id`, `permission_id`) USING BTREE COMMENT '角色权限id索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '角色权限关联表';

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL,
  `user_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '用户id',
  `role_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '角色id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_role_id_index` (`user_id`, `role_id`) USING BTREE COMMENT '用户角色id索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '用户角色关联表';