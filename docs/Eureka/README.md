# Eureka

## Eureka 单节点搭建

1.pom.xml中添加依赖   
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

2.application.yml添加配置   
```yaml
eureka: 
  client:
    #是否将自己注册到Eureka Server
    register-with-eureka: false
    #是否从eureka server获取注册信息
    fetch-registry: false
    #设置服务注册中心的URL
    service-url:                      
      defaultZone: http://localhost:7001/eureka/
```

3.启动类增加注解   
启动类上添加此注解标识该服务为配置中心
```@EnableEurekaServer```


## Eureka 高可用搭建

### 节点1
1.pom.xml中添加依赖   
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

2.application.yml添加配置   
```yaml
server:
  port: 7002
spring:
  # 多个实例用一个 application name.表示属于同一组
  application:
    name: EurekaServer
eureka:
  client:
    # 是否将自己注册到Eureka Server
    register-with-eureka: true
    # 是否从eureka server获取注册信息
    fetch-registry: true
    # 设置服务注册中心的URL
    service-url:
      defaultZone: http://eureka2.com:7001/eureka/
  # 主机名.host文件中配置 eureka1.com和eureka2.com 表示 127.0.0.1
  instance:
    hostname: eureka1.com
```

3.启动类增加注解   
启动类上添加此注解标识该服务为配置中心
```@EnableEurekaServer```

### 节点2
1.pom.xml中添加依赖   
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

2.application.yml添加配置   
```yaml
server:
  port: 7002
spring:
  # 多个实例用一个 application name.表示属于同一组
  application:
    name: EurekaServer
eureka:
  client:
    # 是否将自己注册到Eureka Server
    register-with-eureka: true
    # 是否从eureka server获取注册信息
    fetch-registry: true
    # 设置服务注册中心的URL
    service-url:
      defaultZone: http://eureka1.com:7002/eureka/
  # 主机名.host文件中配置 eureka1.com和eureka2.com 表示 127.0.0.1
  instance:
    hostname: eureka2.com
```

3.启动类增加注解   
启动类上添加此注解标识该服务为配置中心
```@EnableEurekaServer```
