# Eureka

## Eureka 单节点搭建

### pom.xml
```xml
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

### application.yml
```yaml
eureka: 
  client:
    #是否将自己注册到Eureka Server,默认为true，由于当前就是server，故而设置成false，表明该服务不会向eureka注册自己的信息
    register-with-eureka: false
    #是否从eureka server获取注册信息，由于单节点，不需要同步其他节点数据，用false
    fetch-registry: false
    #设置服务注册中心的URL，用于client和server端交流
    service-url:                      
      defaultZone: http://localhost:7001/eureka/
```
### 启动类
启动类上添加此注解标识该服务为配置中心
@EnableEurekaServer