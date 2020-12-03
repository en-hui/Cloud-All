# SpringCloud Config

## 为什么需要配置中心

单体应用，配置写在配置文件中，没有什么大问题。如果要切换环境 可以切换不同的profile（2种方式），但在微服务中。   
微服务比较多。成百上千，配置很多，需要集中管理。   
管理不同环境的配置。   
需要动态调整配置参数，更改配置不停服。   

## 配置中心介绍

分布式配置中心包括3个部分：   
1.存放配置的地方：git ，本地文件 等。   
2.config server。从 1 读取配置。   
3.config client。是 config server 的客户端 消费配置。   

## 环境搭建

1.首先在github创建一个仓库，专门用来存储配置文件.并创建配置文件**Config-Client-dev.yml**    
2.搭建config server端   
1.1依赖
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```
1.2配置文件
```yaml
server:
  port: 9999
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/en-hui/SpringCloud-Config-Center.git
      label: test
  application:
    name: Config-Center
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
1.3启动类添加注解：   
```
@EnableConfigServer
```
1.4启动服务访问```http://localhost:9999/test/Config-Client-dev.yml``` 可以获取到来自git的配置
1.5git上的配置文件和访问匹配规则
> 获取配置规则：根据前缀匹配   
  /{name}-{profiles}.properties   
  /{name}-{profiles}.yml   
  /{name}-{profiles}.json   
  /{label}/{name}-{profiles}.yml   
  name 服务名称   
  profile 环境名称，开发、测试、生产：dev qa prd    
  lable 仓库分支、默认master分支   
  匹配原则：从前缀开始。   


3.搭建config client端(以一个服务为例子，理论上所有想使用统一配置文件的服务都如下配置)   
依赖
```
<dependencies>
    <!-- spring cloud config 客户端-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-client</artifactId>
    </dependency>
    <!--web-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!--erueka客户端-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>
```
配置文件：必须起名为``` bootstrap.yml ``` 后缀名不强制
```
server:
  port: 9998
spring:
  application:
    name: Config-Client
  cloud:
    config:
      #直接URL方式查找配置中心
      #uri: http://localhost:9999/
      #通过注册中心查找
      discovery:
        service-id: CONFIG-CENTER
        enabled: true
      # 开发、测试、生产：dev qa prd
      profile: dev
      # 从什么分支获取
      label: dev
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
测试代码
```java
package fun.enhui.configclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 胡恩会
 * @date 2020/12/3 21:18
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    // 远程配置文件中的配置
    @Value("${myconfig}")
    String myconfig;

    @GetMapping("/get/myconfig")
    public String getName() {
        return myconfig;
    }
}

```

## 刷新配置
上面那种只能满足重启加载远程配置。
### 第一种，单节点刷新配置
>1.开启actuator中的refresh端点
2.Controller中添加 @RefreshScope 注解(所有用到远程配置变量的类都加此注解)
3.向 http://localhost:9998/actuator/refresh 发送Post请求,就可以刷新这台服务的配置了。如果集群需要一个一个访问

添加依赖
```
<!-- actuator 监控 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
开启端点
```
management:
  #autuator开启全部节点信息
  endpoints:
    web:
      exposure:
        include: "*"
```

### 第二种，多节点刷新配置
安装 RabbitMQ
```
# 开启RabbitMQ节点
rabbitmqctl start_app
# 开启RabbitMQ管理模块的插件，并配置到RabbitMQ节点上
rabbitmq-plugins enable rabbitmq_management
```

服务配置：
```
# RabbitMQ 连接信息
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```
添加依赖
```
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```
启动两个服务（9998、9997），此时向一个服务发送刷新，两个都刷新了    
post请求：http://localhost:9998/actuator/bus-refresh
