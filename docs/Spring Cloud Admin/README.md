# Spring Cloud Admin
## Admin 服务端
Admin服务搭建
```
<!-- Admin 服务 server端-->
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
    <version>2.2.1</version>
</dependency>
<!-- Admin 界面 -->
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-server-ui</artifactId>
    <version>2.2.1</version>
</dependency>
```
配置文件
```
server:
  port: 8888
```
启动类
```
@EnableAdminServer
```

所有要被管理的微服务
```
<!-- Admin 客户端 -->
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>2.2.1</version>
</dependency>
<!-- actuator 监控 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
配置
```
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always
spring.boot.admin.client.url=http://localhost:8888

management:
  endpoints:
    web:
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: always
spring:
  boot:
    admin:
      client:
        url: http://localhost:8888
```

## 配置邮件通知（Admin服务中）

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```
配置
```
spring: 
  application: 
    name: cloud-admin
  security:
    user:
      name: root
      password: root
  # 邮件设置
  mail:
    host: smtp.qq.com
    username: 单纯QQ号
    password: xxxxxxx授权码
    properties:
      mail: 
        smpt: 
          auth: true
          starttls: 
            enable: true
            required: true
#收件邮箱
spring.boot.admin.notify.mail.to: 2634982208@qq.com   
# 发件邮箱
spring.boot.admin.notify.mail.from: xxxxxxx@qq.com   
```

## 配置钉钉群通知（Admin服务中）
配置类或启动类
```
@Bean
public DingDingNotifier dingDingNotifier(InstanceRepository repository) {
    return new DingDingNotifier(repository);
}
```

