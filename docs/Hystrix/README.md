# Hystrix
## 理解hystrix是什么
```
本质（用于理解）。如果自己写，用try catch——>用aop——>用注解
try{
    1.向服务提供者发起请求
       1.1判断连接超时
           --> 超时，记录到服务里
       1.2向其他服务器发起请求
}catch(Exception e){
    避免返回不友好的错误信息
    返回降级页面
}

Hystrix就是做这件事的

降级：
1.返回一个好看的页面，提供重试按钮。或返回部分数据
例如：想要查10天的记录，但数据库崩了，查不到数据，就把redis里当天一天的记录返回回去

限流：
1.使用map（URI，线程数）或线程池控制服务可接受的最大线程数。
线程隔离，避免某个服务用了太多线程导致拖垮其他服务
例如：（user服务，10个线程）
（order服务，20个线程）
if(线程数满了){
    抛异常，到降级页面
}

熔断：
对失败次数计数，当达到一定的阈值/阀值，直接到降级页面

有一个半开状态，时不时试一下
例如：每次请求count++
当count%10==0，则去请求一次
```


## 整合feign和hystrix
添加依赖
```xml
<!--Hystrix-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```
添加配置
```yaml
feign:
  hystrix:
    enabled: true
```

第一种方式    
在feignClient处配置fallback类
```java
@FeignClient(name = "UserProvider", fallback = UserProviderBack.class)
public interface ConsumerApi extends UserApi {
    @GetMapping("alive")
    String alive();
}
```
fallback类，做特殊逻辑用的
```java
@Component
public class UserProviderBack implements ConsumerApi {
     @Override
    public String alive() {
        return "降级了";
    }
}
```

第二种方式,可以根据异常状态处理    
在feignClient处配置fallbackFactory类
```java
@FeignClient(name = "UserProvider", fallbackFactory = UserProviderBackFactory.class)
public interface ConsumerApi extends UserApi {
    @GetMapping("alive")
    String alive();    
}
```

```java
@Component
public class UserProviderBackFactory implements FallbackFactory<ConsumerApi> {

    @Override
    public ConsumerApi create(Throwable throwable) {
        return new ConsumerApi() {
            @Override
            public String alive() {
                if (throwable instanceof FeignException.InternalServerError) {
                    return "远程服务器500了";
                }
                System.out.println(throwable);
                return ToStringBuilder.reflectionToString(throwable);
            }
        };
    }
}
```