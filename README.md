# SSO

#### 介绍
OAuth2单点登录

#### 软件架构
springboot+shiro+oltu+thymeleaf+vue.js  
基于springboot框架整合shiro安全验证框架,基于oltu实现oauth2单点登录认证  
前端采用thymeleaf+vue.js实现  
项目由core,web_oauth2_server,app_oauth2_server三部分组成  
core为核心验证逻辑  
web_oauth2_server提供web前端通过跳转方式登录  
app_oauth2_server提供手机app登录接口  

#### 安装教程
1.  使用MAVEN命令将代码导入到idea或者其他IDE中
2.  执行SQL初始化
3.  运行

#### 使用说明

1.  访问http://localhost:9288/oauth2/authorize?response_type=code&client_id=system&redirect_uri=http://*****
2.  根据回调返回code，调用accesstoken接口返回token信息
3.	通过token接口请求userinfo接口，获取用户信息

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 码云特技

1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5.  码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)
