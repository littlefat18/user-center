# 竞赛帮
>帮助大家找到志同道合的赛友的移动端网站（APP风格）
### 本作品地址：[竞赛帮](https://genius.zhaogeban.love)
### 后端地址：https://github.com/littlefat18/user-center/

## 界面展示
### 用户登录界面
![image](https://github.com/littlefat18/user-center/assets/82098994/65c9691f-709b-443f-a01b-d4c9fb11b6ab)
### 主页面
![image](https://github.com/littlefat18/user-center/assets/82098994/d6e6ffd9-d3de-4a0a-a472-a5baf60a91bc)
### 好友界面
![image](https://github.com/littlefat18/user-center/assets/82098994/88f0b050-d3c8-4ee7-83bb-6c3a97d4f429)
### 队伍界面
![image](https://github.com/littlefat18/user-center/assets/82098994/b1cf874d-0269-4cc1-b28a-20d4396f0728)
### 个人界面
![image](https://github.com/littlefat18/user-center/assets/82098994/c76f5da9-f077-4006-8395-08c29317bbcd)
### 搜索界面
![image](https://github.com/littlefat18/user-center/assets/82098994/bbf58317-48f8-418d-ae64-4ffd8c5ed6e7)
### 聊天界面
![image](https://github.com/littlefat18/user-center/assets/82098994/fe8f52bb-3f97-4a32-8ee4-56b2ea0dbefc)
### 好友申请界面
![image](https://github.com/littlefat18/user-center/assets/82098994/3cd73166-0742-492e-a342-c6fea68abd14)

## 技术选型
### 前端
* Vue 3
* Vant UI 组件库（移动端）
* Vite 脚手架
* TypeScript
* Axios 请求库

### 后端
* Java SpringBoot 2.7.x 框架
* MySQL 数据库  
* MyBatis-Plus
* MyBatis X 自动生成器
* Redis 缓存（Spring Data Redis 等多种实现方式）
* Redisson 分布式锁
* Swagger + Knife4j 接口文档
* Gson：JSON 序列化库

### 前后端交互
* WebSocket

## 项目亮点
1. 用户登录：使用 Redis 实现分布式 Session，解决集群间登录态同步问题；并使用 Hash 代替 String 来存储用户信息，节约了 5% 的内存并便于单字段的修改。
2. 对于项目中复杂的集合处理（比如为队伍列表关联已加入队伍的用户），使用 Java 8 Stream API 和 Lambda 表达式来简化编码。
3. 使用 Easy Excel 读取收集来的基础用户信息，并通过自定义线程池 + CompletableFuture 并发编程提高批量导入数据库的性能。实测导入 100 万行的时间从 20秒缩短至 7秒。
4. 使用 Redis 缓存首页高频访问的用户信息列表，将接口响应时长从 800ms缩短至 120ms 。且通过自定义 Redis 序列化器来解决数据乱码、空间浪费的问题。
5. 为解决首次访问系统的用户主页加载过慢的问题，使用 Spring Scheduler 定时任务来实现缓存预热，并通过分布式锁保证多机部署时定时任务不会重复执行。
6. 为解决同一用户重复加入队伍、入队人数超限的问题，使用 Redisson 分布式锁来实现操作互斥，保证了接口幂等性。
7. 使用 Knife4j + Swagger 自动生成后端接口文档，并通过编写 ApiOperation 等注解补充接口注释，避免了人工编写维护文档的麻烦。
8. 前端使用 Vant UI 组件库，并封装了全局通用的 Layout 组件，使主页、搜索页、组队页布局一致、并减少重复代码。
9. 基于 Vue Router 全局路由守卫实现了根据不同页面来动态切换导航栏标题， 并通过在全局路由配置文件扩展 title 字段来减少无意义的 if else 代码。
10. 使用WebSocket实现聊天功能,实时进行交流

