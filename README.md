# 智码数云 - 数码电商平台
一个基于 Spring Cloud 微服务架构 + Vue 3 的全栈数码电商平台，涵盖用户购物、商家管理、平台运营三大业务场景。
## 项目简介
智码数云是一个面向数码产品领域的 B2C 电商平台，支持多角色（用户/商家/管理员）协同操作。后端采用 Spring Cloud 微服务架构，通过 API 网关统一鉴权路由；前端基于 Vue 3 + Element Plus 构建单页面应用，提供流畅的交互体验。

项目性质说明：本项目为个人全栈学习实践作品，旨在探索 Spring Cloud 微服务架构在实际业务场景中的应用，已完成核心业务闭环流程。项目内置支付为模拟接口，暂不具备生产环境安全性及高并发能力，不建议直接用于商业用途。
## 技术栈
### 后端
| 技术 | 说明 |
|------|------|
| Spring Boot 3.3.4 | 基础框架 |
| Spring Cloud Gateway | API 网关，路由转发与统一鉴权 |
| Nacos | 服务注册与发现、配置中心 |
| OpenFeign | 微服务间远程调用 |
| Sa-Token | 权限认证（网关侧 Reactor 模式） |
| MySQL | 关系型数据库 |
| MyBatis | ORM 框架 |
| Redis | 缓存中间件 |
| MongoDB | 文档型数据库（评论系统、Banner 管理） |
| Elasticsearch 7.12 + IK 分词 | 商品搜索引擎 |
| RabbitMQ | 消息队列（订单超时自动取消、购物车同步） |
| Spring Cache | 声明式缓存 |
| Lombok | 代码简化 |
### 前端
| 技术 | 说明 |
|------|------|
| Vue 3 | 渐进式前端框架 |
| Vite 5 | 构建工具 |
| Element Plus | UI 组件库 |
| ECharts 6 | 数据可视化图表 |
| Axios | HTTP 请求库 |
| Sass | CSS 预处理器 |
## 系统架构
```
┌─────────────┐
│   Vue 3 SPA │  前端（qd）
└──────┬──────┘
       │ HTTP
┌──────▼──────┐
│   Gateway   │  网关（gateway-demo）:8080
│  Sa-Token   │  统一鉴权 · 路由转发
└──────┬──────┘
       │
   ┌───┼───────────────────────────────────┐
   │   │           │           │            │
┌──▼─┐┌▼────┐┌────▼───┐┌────▼────┐┌──────▼──┐
│user││order││merchant││commodity││  admin   │
│demo││demo ││  demo  ││  demo   ││  demo    │
└────┘└─────┘└────────┘└─────────┘──────────┘
   │      │        │          │          │
   └──────┴────────┴────┬─────┴──────────┘
                        │
              ┌─────────┼─────────┐
              │         │         │
          ┌───▼──┐ ┌───▼──┐ ┌───▼──┐
          │ MySQL │ │Redis │ │MongoDB│
          └──────┘ └──────┘ └───────┘
              │         │
          ┌───▼──┐ ┌───▼──────┐
          │  ES  │ │ RabbitMQ │
          └──────┘ └──────────┘
```
## 微服务模块
### 后端模块（cloud-demo）
| 模块 | 端口 | 职责 |
|------|------|------|
| gateway-demo | 8080 | API 网关：路由转发、Sa-Token 统一鉴权、全局异常处理 |
| common | - | 公共模块：统一返回结果、JWT 工具、Sa-Token 辅助、异常处理、MQ 消息体 |
| service-common | - | 服务公共模块：操作日志注解等 |
| api-demo | - | Feign 客户端模块：定义各服务间的远程调用接口与 DTO/VO |
| user-demo | 8082 | 用户服务：注册登录、购物车、收货地址、商品浏览/搜索/推荐、收藏商家、评论系统、申请管理 |
| merchant-demo | 8083 | 商家服务：注册登录、信息管理、地址管理 |
| commodity-demo | 8084 | 商品服务：商品 CRUD、ES 搜索、MySQL 数据同步到 ES |
| order-demo | 8085 | 订单服务：下单、支付、发货、确认收货、取消订单、订单统计、超时自动取消 |
| admin-demo | 8081 | 管理服务：用户/商家管理、封禁/解封、商品管理、Banner 管理、标签管理、申请处理、数据统计 |
### 前端模块（qd）
| 页面 | 功能 |
|------|------|
| 商城首页 | 商品推荐、分类导航、Banner 轮播、搜索、AI 助手入口 |
| 搜索页 | 商品/商家搜索、分类筛选、ES 全文检索 |
| 商品详情 | 商品信息展示、加购、进店、评论系统 |
| 商家店铺 | 商家信息、商品列表、收藏商家 |
| 购物车 | 商品增删改、数量调整、结算下单 |
| 订单流程 | 确认订单 → 模拟支付 → 订单完成 |
| 用户中心 | 个人资料、收货地址管理、我的订单、我的收藏、申请管理 |
| AI 助手 | 智能聊天窗口（优惠/物流/客服咨询） |
| 商家后台 | 商品管理、订单中心、财务报表（ECharts 可视化）、店铺设置、申请管理 |
| 管理后台 | 控制台概览、用户管理、商家管理、商品管理、内容管理（Banner）、标签管理、请求管理、账号管理 |
## 核心功能
### 用户端
- 注册/登录：用户注册、登录、退出，Sa-Token 鉴权
- 商品浏览：首页推荐、分类浏览、ES 全文搜索（IK 中文分词）
- 商品推荐：基于用户行为日志的个性化推荐算法
- 购物车：添加/删除/修改数量、清空购物车
- 订单管理：下单（单品/购物车结算）、模拟支付、取消订单、确认收货、查看订单详情
- 收货地址：增删改查、设置默认地址
- 收藏商家：收藏/取消收藏、收藏列表
- 评论系统：发表评论、回复、点赞、删除（MongoDB 存储）
- 申请管理：退款申请、举报商家、举报商品
- AI 助手：智能聊天窗口，快速查询优惠/物流等信息
### 商家端
- 商品管理：添加商品、修改商品、上架/下架、删除商品
- 订单中心：查看订单列表、发货、取消订单、按状态/订单号筛选
- 财务报表：订单统计曲线图（7天/30天）、交易构成分析、金额统计
- 店铺设置：商家信息修改、地址管理
- 申请管理：查看申请状态
### 管理端
- 控制台概览：平台总用户数、商家数、商品数、订单数、封禁统计、活跃度趋势图
- 用户管理：用户列表、封禁/解封用户
- 商家管理：商家列表、封禁/解封商家
- 商品管理：商品搜索、强制下架/恢复、查看详情
- 内容管理：Banner 增删改、排序、点击统计
- 标签管理：商品标签/分类管理
- 请求管理：处理退款申请、举报申请（商品举报、商家举报、删除评论申请）
- 数据统计：用户/商家新增曲线、活跃度曲线、平台概览
## 技术亮点
1. 微服务架构：基于 Spring Cloud + Nacos 实现服务注册发现，OpenFeign 实现服务间调用，Gateway 统一路由鉴权
2. Elasticsearch 搜索：商品数据 MySQL → ES 双写同步，支持 IK 中文分词全文检索，支持分类/商家/关键词多维度搜索
3. RabbitMQ 消息队列：订单超时自动取消（延迟队列）、购物车数据异步同步
4. MongoDB 文档存储：评论系统（主评论 + 子回复 + 点赞）与 Banner 管理采用 MongoDB 存储，灵活应对非结构化数据
5. 个性化推荐：基于用户行为日志（浏览/加购/下单）的协同过滤推荐算法，结合 Redis 缓存预热
6. 统一鉴权：Gateway 层 Sa-Token 统一拦截验证，解析 token 后将用户信息注入请求头传递至下游服务
7. Spring Cache + Redis：热点数据缓存，推荐结果缓存预热，降低数据库压力
8. 多角色权限：用户/商家/管理员三角色隔离，网关侧根据请求路径区分角色鉴权
## 项目结构
```
bs/
├── cloud-demo/                    # 后端微服务项目
│   ├── common/                    # 公共模块（Result/JWT/Sa-Token/异常处理）
│   ├── gateway-demo/              # API 网关
│   ├── service/
│   │   ├── api-demo/              # Feign 客户端 & DTO/VO 定义
│   │   ├── service-common/        # 服务公共模块（日志注解等）
│   │   ├── user-demo/             # 用户服务
│   │   ├── merchant-demo/         # 商家服务
│   │   ├── commodity-demo/        # 商品服务
│   │   ├── order-demo/            # 订单服务
│   │   └── admin-demo/            # 管理服务
│   ├── mq_cj/                     # MQ 插件（延迟队列、IK 分词器）
│   └── pom.xml                    # 父 POM
│
└── qd/                            # 前端项目
    ├── src/
    │   ├── api/                   # API 请求模块
    │   │   ├── admin/             # 管理端 API
    │   │   ├── merchant/          # 商家端 API
    │   │   └── user/              # 用户端 API
    │   ├── assets/                # 静态资源
    │   ├── components/            # 公共组件
    │   ├── config/                # 配置文件
    │   ├── utils/                 # 工具函数
    │   └── views/
    │       ├── login/             # 登录/注册
    │       ├── user/              # 用户端页面
    │       ├── merchant/          # 商家端页面
    │       └── admin/             # 管理端页面
    └── package.json
```
## 快速开始
### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis
- MongoDB
- Elasticsearch 7.12 + IK 分词插件
- RabbitMQ 3.13+（需安装延迟消息插件）
- Nacos 2.x
### 后端启动
1. 启动 Nacos、MySQL、Redis、MongoDB、Elasticsearch、RabbitMQ
2. 在 MySQL 中创建 cloud-test 数据库并执行建表脚本
3. 修改 common/src/main/resources/application-dev.yml 中的中间件连接配置
4. 按顺序启动各服务：
- GatewayDemoApplication（网关）
- UserDemoApplication
- MerchantDemoApplication
- ShopDemoApplication（commodity-demo）
- OrderDemoApplication
- AdminDemoApplication
### 前端启动
cd qd
npm install
npm run dev
本地部署访问 http://localhost:5173即可使用。
## 系统截图
<img width="2526" height="1399" alt="image" src="https://github.com/user-attachments/assets/9554d345-cd27-4918-b181-5481752a543a" />
<img width="1340" height="1400" alt="image" src="https://github.com/user-attachments/assets/8358f425-5587-4be7-81e1-6ecaed2fb5c0" />
<img width="1767" height="1392" alt="image" src="https://github.com/user-attachments/assets/34ba8b5b-c87b-4df0-9178-7c900fac0646" />
<img width="2559" height="1364" alt="image" src="https://github.com/user-attachments/assets/dcb932c0-ac6f-435f-a429-1524524fa625" />
<img width="2556" height="1337" alt="image" src="https://github.com/user-attachments/assets/68e18408-68f6-465f-aba9-fb3f467f24eb" />

