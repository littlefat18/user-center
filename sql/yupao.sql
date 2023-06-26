/*
Navicat MySQL Data Transfer

Source Server         : dzldbs1
Source Server Version : 50737
Source Host           : localhost:3306
Source Database       : yupao

Target Server Type    : MYSQL
Target Server Version : 50737
File Encoding         : 65001

Date: 2023-05-24 15:26:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for team
-- ----------------------------
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(256) NOT NULL COMMENT '队伍名称',
  `description` varchar(1024) DEFAULT NULL COMMENT '描述',
  `maxNum` int(11) NOT NULL DEFAULT '1' COMMENT '最大人数',
  `expireTime` datetime DEFAULT NULL COMMENT '过期时间',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '0 - 公开，1 - 私有，2 - 加密',
  `password` varchar(512) DEFAULT NULL COMMENT '密码',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='队伍';

-- ----------------------------
-- Records of team
-- ----------------------------
INSERT INTO `team` VALUES ('2', '超级小分队', '超级无敌小分队', '10', '2023-06-16 09:13:55', '1', '0', null, '2023-05-15 16:44:20', '2023-05-15 16:44:20', '0');
INSERT INTO `team` VALUES ('3', '超级小分队1', '超级无敌小分队', '10', '2023-06-16 09:13:55', '1', '0', null, '2023-05-15 16:44:45', '2023-05-15 16:44:45', '0');
INSERT INTO `team` VALUES ('4', '超级小分队2', '超级无敌小分队', '10', '2023-06-16 09:13:55', '1', '0', null, '2023-05-15 16:45:12', '2023-05-15 16:45:12', '0');
INSERT INTO `team` VALUES ('5', '超级小分队3', '超级无敌小分队', '10', '2023-06-16 09:13:55', '1', '0', null, '2023-05-15 16:45:16', '2023-05-15 16:45:16', '0');
INSERT INTO `team` VALUES ('6', '超级小分队4', '超级无敌小分队', '10', '2023-06-16 09:13:55', '1', '0', null, '2023-05-15 16:45:18', '2023-05-15 16:45:18', '0');
INSERT INTO `team` VALUES ('8', '神秘小队', '', '10', '2023-06-16 09:13:55', '1', '0', '', '2023-05-15 16:50:27', '2023-05-18 18:46:44', '0');
INSERT INTO `team` VALUES ('9', '超级小分队111', '超级无敌小分队111', '10', '2023-06-16 09:13:55', '2', '0', null, '2023-05-18 18:17:17', '2023-05-18 18:17:17', '0');
INSERT INTO `team` VALUES ('10', '无敌小队', '', '3', '2023-06-30 12:00:00', '2', '0', '', '2023-05-18 18:26:43', '2023-05-18 18:26:43', '0');
INSERT INTO `team` VALUES ('11', '无敌小队11', '11111', '3', '2023-06-30 12:00:00', '2', '2', '12345678', '2023-05-18 18:27:09', '2023-05-18 18:27:09', '0');
INSERT INTO `team` VALUES ('12', '超级小分队d', '超级无敌小分队dasdasd', '10', '2023-07-18 12:00:00', '8', '0', null, '2023-05-20 13:30:15', '2023-05-22 15:51:25', '1');
INSERT INTO `team` VALUES ('13', '宇宙第一', '111', '5', '2023-08-18 12:00:00', '8', '0', '', '2023-05-20 15:30:09', '2023-05-20 15:40:57', '1');
INSERT INTO `team` VALUES ('14', '超级小分队111', '超级无敌小分队3333', '10', '2023-06-19 12:00:00', '8', '0', null, '2023-05-22 15:51:46', '2023-05-22 20:10:37', '0');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(256) DEFAULT NULL COMMENT '用户昵称\r\n',
  `userAccount` varchar(256) DEFAULT NULL COMMENT '用户账户\r\n',
  `avatarUrl` varchar(1024) DEFAULT NULL COMMENT '头像',
  `gender` tinyint(4) DEFAULT NULL COMMENT '性别',
  `profile` varchar(512) DEFAULT NULL COMMENT '个人简介',
  `phone` varchar(128) DEFAULT NULL COMMENT '电话',
  `email` varchar(512) DEFAULT NULL COMMENT '邮箱',
  `userStatus` int(11) NOT NULL DEFAULT '0' COMMENT '用户状态 0正常',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `userRole` int(11) NOT NULL DEFAULT '0' COMMENT '用户类型 0普通用户 1管理员',
  `planetCode` varchar(512) DEFAULT NULL COMMENT '星球编号',
  `tags` varchar(1024) DEFAULT NULL COMMENT '标签列表',
  `userPassword` varchar(512) NOT NULL COMMENT '用户密码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'lvzi1', 'luozi', 'https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png', '1', '我是一头快乐的驴子', '12312321', '11111111@qq.com', '0', null, '2023-05-18 17:52:39', '0', '0', '4', '[\"男\",\"java\"]', 'b0dd3697a192885d7c055db46155b26a');
INSERT INTO `user` VALUES ('2', 'dogyupi', 'yupi', 'https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png', '0', '我是鱼皮', '1234567', '609753896@qq.com', '0', null, '2022-08-10 14:54:25', '0', '1', '1', '[\"男\",\"java\",\"emo\"]', 'b0dd3697a192885d7c055db46155b26a');
INSERT INTO `user` VALUES ('3', '高手', 'dogyupi', 'https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png', '1', '我是高手', null, null, '0', null, '2023-05-18 17:52:46', '0', '0', '5', '[\"女\",\"大一\",\"emo\"]', 'b0dd3697a192885d7c055db46155b26a');
INSERT INTO `user` VALUES ('8', '神秘大侠', 'tiancai', 'https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png', '0', '一个神秘的高手，非常神秘，哈哈哈哈哈哈，啦啦啦啦啦啦', null, null, '0', '2022-06-29 10:07:08', '2023-05-18 17:52:50', '0', '1', '6', '[\"男\",\"java\",\"c++\"]', 'b0dd3697a192885d7c055db46155b26a');
INSERT INTO `user` VALUES ('9', 'luozi', 'liuchenghui', 'https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png', '1', '我是luozi', null, null, '0', '2022-06-29 11:31:16', '2023-05-18 17:52:56', '0', '0', '7', '[\"女\",\"大二\",\"java\"]', 'b0dd3697a192885d7c055db46155b26a');
INSERT INTO `user` VALUES ('10', '小狗', 'ruigou', 'https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png', '0', '我是小狗', null, null, '0', '2022-06-30 14:22:58', '2023-05-18 17:52:59', '0', '0', '8', '[\"男\",\"python\",\"c++\"]', 'b0dd3697a192885d7c055db46155b26a');
INSERT INTO `user` VALUES ('11', '骡子', 'dingziluo', 'https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png', '0', '我是骡子', null, null, '0', '2022-07-01 14:31:46', '2023-05-18 17:53:03', '0', '0', '2', '[\"男\",\"go\",\"喜欢打游戏\"]', 'b0dd3697a192885d7c055db46155b26a');
INSERT INTO `user` VALUES ('12', '水淼', 'shuimiao', 'https://636f-codenav-8grj8px727565176-1256524210.tcb.qcloud.la/img/logo.png', '0', '我是水哥', null, null, '0', '2022-07-01 14:48:00', '2023-05-18 17:53:07', '0', '0', '3', '[\"男\",\"java\"]', 'b0dd3697a192885d7c055db46155b26a');
INSERT INTO `user` VALUES ('13', 'shazi', '12345', 'https://img2.baidu.com/it/u=4084621093,2971972319&fm=253&fmt=auto&app=120&f=JPEG?w=889&h=500', '0', '我是傻子', '123', '123', '0', '2022-08-09 16:04:01', '2022-08-10 16:11:48', '0', '0', '12312', '[\"男\",\"java\"]', '123');
INSERT INTO `user` VALUES ('14', 'dingziluo', 'dingziluo', 'https://img1.baidu.com/it/u=2863108920,4275403644&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=281', '0', '我是钉子', null, null, '0', '2022-08-09 16:05:03', '2022-08-10 16:11:48', '0', '0', '2', '[\"男\",\"c++\"]', 'b0dd3697a192885d7c055db46155b26a');
INSERT INTO `user` VALUES ('15', 'shazi', '613513241421', 'https://img0.baidu.com/it/u=2421848171,2412138811&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500', '0', '我是傻子2号', '123', '123', '0', '2022-08-09 16:05:03', '2022-08-10 16:11:48', '0', '0', '13421', '[\"男\",\"php\"]', '123');
INSERT INTO `user` VALUES ('16', 'wudi', 'wudi', 'https://img1.baidu.com/it/u=30005361,3682623062&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500', '0', '我是无敌', null, null, '0', '2022-08-09 16:22:33', '2022-08-10 16:11:48', '0', '0', '1111', '[\"男\",\"kotlin\"]', 'b0dd3697a192885d7c055db46155b26a');

-- ----------------------------
-- Table structure for user_team
-- ----------------------------
DROP TABLE IF EXISTS `user_team`;
CREATE TABLE `user_team` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `teamId` bigint(20) DEFAULT NULL COMMENT '队伍id',
  `joinTime` datetime DEFAULT NULL COMMENT '加入时间',
  `createTime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COMMENT='用户队伍关系';

-- ----------------------------
-- Records of user_team
-- ----------------------------
INSERT INTO `user_team` VALUES ('1', '1', '2', '2023-05-15 08:44:21', '2023-05-15 16:44:20', '2023-05-15 16:44:20', '0');
INSERT INTO `user_team` VALUES ('2', '1', '3', '2023-05-15 08:44:45', '2023-05-15 16:44:45', '2023-05-15 16:44:45', '0');
INSERT INTO `user_team` VALUES ('3', '1', '4', '2023-05-15 08:45:13', '2023-05-15 16:45:12', '2023-05-15 16:45:12', '0');
INSERT INTO `user_team` VALUES ('4', '1', '5', '2023-05-15 08:45:17', '2023-05-15 16:45:16', '2023-05-15 16:45:16', '0');
INSERT INTO `user_team` VALUES ('5', '1', '6', '2023-05-15 08:45:19', '2023-05-15 16:45:18', '2023-05-15 16:45:18', '0');
INSERT INTO `user_team` VALUES ('6', '2', '8', '2023-05-15 08:50:27', '2023-05-15 16:50:27', '2023-05-15 16:50:27', '0');
INSERT INTO `user_team` VALUES ('7', '2', '9', '2023-05-18 10:17:18', '2023-05-18 18:17:17', '2023-05-18 18:17:17', '0');
INSERT INTO `user_team` VALUES ('8', '2', '10', '2023-05-18 10:26:43', '2023-05-18 18:26:43', '2023-05-18 18:26:43', '0');
INSERT INTO `user_team` VALUES ('9', '2', '11', '2023-05-18 10:27:10', '2023-05-18 18:27:09', '2023-05-18 18:27:09', '0');
INSERT INTO `user_team` VALUES ('10', '2', '2', '2023-05-18 10:41:07', '2023-05-18 18:41:06', '2023-05-18 18:41:06', '0');
INSERT INTO `user_team` VALUES ('11', '8', '2', '2023-05-18 10:43:39', '2023-05-18 18:43:39', '2023-05-18 18:43:39', '0');
INSERT INTO `user_team` VALUES ('12', '8', '3', '2023-05-18 10:43:46', '2023-05-18 18:43:45', '2023-05-20 15:42:53', '1');
INSERT INTO `user_team` VALUES ('13', '8', '12', '2023-05-20 05:30:15', '2023-05-20 13:30:15', '2023-05-22 15:51:25', '1');
INSERT INTO `user_team` VALUES ('14', '8', '13', '2023-05-20 07:30:10', '2023-05-20 15:30:09', '2023-05-20 15:40:57', '1');
INSERT INTO `user_team` VALUES ('15', '8', '3', '2023-05-22 07:50:44', '2023-05-22 15:50:43', '2023-05-22 15:50:56', '1');
INSERT INTO `user_team` VALUES ('16', '8', '14', '2023-05-22 07:51:47', '2023-05-22 15:51:46', '2023-05-22 15:51:46', '0');
INSERT INTO `user_team` VALUES ('17', '8', '3', '2023-05-22 12:10:16', '2023-05-22 20:10:16', '2023-05-22 20:10:16', '0');
INSERT INTO `user_team` VALUES ('18', '8', '11', '2023-05-22 13:08:40', '2023-05-22 21:08:40', '2023-05-22 21:08:45', '1');
INSERT INTO `user_team` VALUES ('19', '8', '11', '2023-05-23 05:16:28', '2023-05-23 13:16:27', '2023-05-23 13:16:27', '0');
INSERT INTO `user_team` VALUES ('20', '8', '4', '2023-05-23 05:40:52', '2023-05-23 13:40:51', '2023-05-23 13:40:51', '0');


create table `friends`
(
    `id`         bigint auto_increment comment '好友申请id' primary key,
    `fromId`     bigint                             not null comment '发送申请的用户id',
    `receiveId`  bigint                             null comment '接收申请的用户id ',
    `isRead`     tinyint  default 0                 not null comment '是否已读(0-未读 1-已读)',
    `status`     tinyint  default 0                 not null comment '申请状态 默认0 （0-未通过 1-已同意 2-已过期 3-已撤销）',
    `createTime` datetime default CURRENT_TIMESTAMP null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP null,
    `isDelete`   tinyint  default 0                 not null comment '是否删除',
    `remark`     varchar(214)                       null comment '好友申请备注信息'
)
    comment '好友申请管理表' charset = utf8mb4;
create table chat
(
    id         bigint auto_increment comment '聊天记录id'
        primary key,
    fromId     bigint
                                                       not null comment '发送消息id',
    toId       bigint                                  null comment '接收消息id',
    text       varchar(512) collate utf8mb4_unicode_ci null,
    chatType   tinyint                                 not null comment '聊天类型 1-私聊 2-群聊',
    createTime datetime default CURRENT_TIMESTAMP      null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP      null,
    teamId     bigint                                  null
)
    comment '聊天消息表' charset = utf8mb4;
