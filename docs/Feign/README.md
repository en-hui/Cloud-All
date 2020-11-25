# Feign

## 面向公共api jar包开发
### 公共api模块
```
<dependencies>
    <!--web-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```
定义公共api，所有服务提供者和服务消费者遵从此标准   
```java
package fun.enhui.userapi.controller.api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 公共api模块
 *
 * @author 胡恩会
 * @date 2020/11/23 0:14
 */
@RequestMapping("user")
public interface UserApi {

    /**
     * 最简单的调用
     **/
    @GetMapping("alive")
    String alive();

}
```

### 服务提供者
```
<dependencies>
    <!--erueka客户端-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <!--web-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!--自定义接口规范 feign-->
    <dependency>
        <groupId>fun.enhui</groupId>
        <artifactId>User-Api</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```
服务提供者遵循公共api标准，提供服务实现
```java
package fun.enhui.userprovider.controller;

import fun.enhui.userapi.controller.api.UserApi;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务提供者
 *
 * @author 胡恩会
 * @date 2020/11/22 23:29
 */
@RestController
public class UserController implements UserApi {
    /**
     * 返回服务状态
     **/
    @Override
    public String alive() {
        return "ok";
    }
}

```
### 服务消费者
```
<dependencies>
    <!--erueka客户端-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <!--web-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!--openfeign-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <!--自定义接口规范 feign-->
    <dependency>
        <groupId>fun.enhui</groupId>
        <artifactId>User-Api</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```
定义feginClient，继承公共api
```java
package fun.enhui.userconsumer.api;

import fun.enhui.userapi.controller.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 继承公共api模块，什么都不用写
 *
 * @author 胡恩会
 * @date 2020/11/23 0:16
 */
@FeignClient(name = "UserProvider")
public interface ConsumerApi extends UserApi {

}
```

> 注意事项：
>