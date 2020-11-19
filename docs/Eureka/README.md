# Eureka
版本信息
```
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.6.RELEASE</version>
    <relativePath/> <!-- lookup parent from repository -->
</parent>

<properties>
    <java.version>1.8</java.version>
    <spring-cloud.version>Hoxton.SR3</spring-cloud.version>
</properties>
```
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
server:
  port: 7001
eureka:
  client:
    # 是否将自己注册到Eureka Server
    register-with-eureka: false
    # 是否从eureka server获取注册信息
    fetch-registry: false
    # 必须覆盖此配置，否则会默认向8761注册。上面两个false不能关闭自己的注册
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
  port: 7001
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
      defaultZone: http://eureka2.com:7002/eureka/
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
      defaultZone: http://eureka1.com:7001/eureka/
  # 主机名.host文件中配置 eureka1.com和eureka2.com 表示 127.0.0.1
  instance:
    hostname: eureka2.com
```

3.启动类增加注解   
启动类上添加此注解标识该服务为配置中心
```@EnableEurekaServer```

## eureka自我保护机制
eureka客户端默认30秒向服务端发送一次心跳，即一分钟两次    
客户端每分钟续约数量小于客户端续约总数量的85%时会触发自我保护机制    
触发自我保护机制（主要为了保护服务列表），就不会从服务列表中剔除不可用的服务    
关闭自我保护机制，在eureka服务端配置   
```yaml
eureka.server.enable-self-preservation=false
```  

关闭后会在eureka页面提示
```
THE SELF PRESERVATION MODE IS TURNED OFF. THIS MAY NOT PROTECT INSTANCE EXPIRY IN CASE OF NETWORK/OTHER PROBLEMS.
```

## actuator 安全监控
由于eureka服务端自带，所以只需在eureka客户端配置即可
```xml
<!--actuator 安全监控，用来上报节点信息-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
配置完后，可以访问本服务的 /actuator 查看节点信息
```
#autuator开启全部节点信息
management.endpoints.web.exposure.include=*
#开启后，可以用post请求远程关闭服务节点,必须和上面的一起用
management.endpoints.shutdown.enable=true
```

## eureka 健康检查
由于server和client通过心跳保持服务状态，而只有状态为UP的服务才能被访问。看eureka界面中的status。

比如心跳一直正常，服务一直UP，但是此服务DB连不上了，无法正常提供服务，此时需要手动下线(手动将状态设置为DOWN)。
    
开启手动控制，在 Eureka-Provider 模块演示    
1.Client端配置开启健康检查    
```yaml
#向eureka上报真实的健康状态
eureka:
  client:
    healthcheck:
      enabled: true
```

2.Client端配置Actuator
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
代码见 Eureka-Provider 模块下的 HealthStatusService

## eureka 的安全配置
1.Eureka 服务端配置pom
```
<!--安全配置-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
2.Eureka 服务端添加配置
```yaml
spring:
  security:
    user:
      name: admin
      password: admin
```
3.Eureka 客户端连接配置位置添加账户密码
```yaml
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

Eureka 服务端添加配置类(关闭防跨域)
```java
package fun.enhui.eurekaserver;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 安全配置
 *
 * @author 胡恩会
 * @date 2020/11/19 1:40
 */
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭防跨域
        http.csrf().disable();
        super.configure(http);
    }
}

```