#### 单点登录 springboot+shiro+oltu+thymeleaf+vue.js  
****
#### 项目介绍
Maven项目, 字符编码: UTF-8;  

基于springboot框架整合shiro安全验证框架,基于oltu实现oauth2单点登录认证  
前端采用thymeleaf+vue.js实现  
项目由core,web_oauth2_server,app_oauth2_server三部分组成  
core为核心验证逻辑  
web_oauth2_server提供web前端通过跳转方式登录  
app_oauth2_server提供手机app登录接口  

主要技术版本  
JDK(1.8)  
springboot(2.2.0.RELEASE)  
shiro(1.3.2)  
oltu(1.0.2)  
****
#### 功能列表
当前版本v1.0.0  
1.暂时只支持账号密码，相同session免登录两种登录方式（后面版本扩充多种登录方式认证）  
2.支持authorization_code,password,refresh_token三种获取token方式  
3.支持redis session缓存  
4.支持redis信息暂存 (账号信息，客户端信息访问后暂存redis中，下次请求直接访问redis不在请求数据库)  
5.支持账号登录互斥功能(不同电脑或手机登录时踢出之前登录账号)  
6.支持登录日志记录 
****
后期计划  
v1.1.0  
1.增加验证码登录  
2.增加微信小程序扫码登录  
3.增加手机端接口 
4.增加流控  

v2.0.0  
1.增加权限管理  

****
#### 安装教程
1.  使用MAVEN命令将代码导入到idea或者其他IDE中
2.  安装lombok插件(如果已安装忽略此步骤)
3.  执行目录中 SQL建表语句 创建登录相关表
4.  安装本机redis，并启动
3.  运行
****
#### 使用说明 
    (http://localhost:19888/swagger-ui.html查看接口文档)  
1.  访问http://localhost:19888/oauth2/authorize?response_type=code&client_id=system&redirect_uri={redirect_uri}&state={state}  
2.  输入账号admin 密码!QAZ2wsx 提交  
3.  系统回调{redirect_uri}?code={code}&state={state}  
4.  根据回调返回code，post调用http://localhost:19888/oauth2/accessToken接口返回token信息,参数如下:Content-Type=application/x-www-form-urlencoded  
    [{"key":"client_id","value":"system","description":""},{"key":"client_secret","value":"system","description":""},{"key":"grant_type","value":"authorization_code","description":""},{"key":"code","value":"77c9c36b1d9ef13f366d20f47789c80b","description":""},{"key":"redirect_uri","value":"http://baidu.com","description":""}]  
5.	访问http://localhost:19888/user/info?access_token={access_token}获取用户信息  
    也可将token信息放入header中  以Authorization=Bearer{access_token}访问获取用户信息
6.  访问http://localhost:19888/user/revoke 登出账号
