-- auto-generated definition
create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(256)                        null comment '用户昵称
',
    userAccount  varchar(256)                        null comment '用户账户
',
    avatarUrl    varchar(1024)                       null comment '头像',
    gender       tinyint                             null comment '性别',
    userPassword varchar(512)                        not null comment '用户密码',
    phone        varchar(128)                        null comment '电话',
    email        varchar(512)                        null comment '邮箱',
    userStatus   int       default 0                 not null comment '用户状态 0正常',
    createTime   datetime                            null comment '创建时间',
    updateTime   timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint   default 0                 not null comment '是否删除',
    userRole     int       default 0                 not null comment '用户类型 0普通用户 1管理员',
    planetCode   varchar(512)                        null comment '星球编号',
    tags         varchar(1024)                       null comment '标签列表'
)
    comment '用户表';

