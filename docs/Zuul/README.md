# Zuul
zuul默认集成了：ribbon和hystrix。
## 搭建Zuul环境
新建项目引入依赖
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
</dependency>
```
配置文件
```yaml
server:
  port: 80
spring:
  application:
    name: ZuulTest
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      #无密码形式
      #defaultZone: http://localhost:7001/eureka/
      #安全验证，有密码模式
      defaultZone: http://admin:admin@localhost:7001/eureka/
```
启动类
```
@EnableZuulProxy
```
请求：    
http://localhost/userconsumer/alive   
注意：    
服务名是UserConsumer，但访问时需要把serviceId转成小写   


