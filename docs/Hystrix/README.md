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

## 整合RestTemplate和hystrix
service
```
	@HystrixCommand(fallbackMethod = "back")
	public String alive() {
		// 自动处理URL
		
		RestTemplate restTemplate = new RestTemplate();
		
		String url ="http://user-provider/User/alive";
		String object = restTemplate.getForObject(url, String.class);
		
		return object;
		
	}
	
	
	public String back() {
		
		return "请求失败~bbb...";
	}
```
启动类
```
@EnableCircuitBreaker
```
## 线程隔离和信号量隔离（重点）
hystrix默认使用线程池控制请求隔离   
1.线程池隔离：即hystrix维护一个线程池去执行调用    
2.信号量隔离：即使用tomcat的Worker线程池去执行调用。信号量隔离就是一道关卡，设置多少信号量，就允许多少线程通过它来调用外部服务

信号量隔离主要维护的是Tomcat的线程，不需要内部线程池，更加轻量级。

配置
```
hystrix.command.default.execution.isolation.strategy 隔离策略，默认是Thread, 可选Thread｜Semaphore
thread 通过线程数量来限制并发请求数，可以提供额外的保护，但有一定的延迟。一般用于网络调用
semaphore 通过semaphore count来限制并发请求数，适用于无网络的高并发请求
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds 命令执行超时时间，默认1000ms
hystrix.command.default.execution.timeout.enabled 执行是否启用超时，默认启用true
hystrix.command.default.execution.isolation.thread.interruptOnTimeout 发生超时是是否中断，默认true
hystrix.command.default.execution.isolation.semaphore.maxConcurrentRequests 最大并发请求数，默认10，该参数当使用ExecutionIsolationStrategy.SEMAPHORE策略时才有效。如果达到最大并发请求数，请求会被拒绝。理论上选择semaphore size的原则和选择thread size一致，但选用semaphore时每次执行的单元要比较小且执行速度快（ms级别），否则的话应该用thread。
semaphore应该占整个容器（tomcat）的线程池的一小部分。
```

## 开启dashboard
启动类添加
```
@EnableHystrixDashboard
```
添加依赖
```
<!-- hystrix dashboard-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>
        spring-cloud-starter-netflix-hystrix-dashboard
    </artifactId>
</dependency>
<!-- actuator -->		
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

健康上报   
http://localhost:90/actuator/hystrix.stream

图形化   
http://localhost:90/hystrix

如果没有，autuator开启全部节点信息