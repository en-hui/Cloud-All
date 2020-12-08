# Eureka Server优化

## eureka启动原理
引入```spring-cloud-starter-netflix-eureka-server```依赖  
并且添加```@EnableEurekaServer```注解，是如何启动了eureka server的

> @EnableEurekaServer注解本质是向ioc容器注入了一个 Marker 类的对象（看作开关）  
> 当存在Mark类对象时，eureka包的启动类EurekaServerAutoConfiguration就会启动  
> 启动类又引入了EurekaServerInitializerConfiguration类，引入这个类就会运行这个类中的start方法。  
> 因为start是重写了org.springframework.context包的start方法。所以只要引入就会调用（生命周期函数）  
> start方法：启动了一个线程，


## 优化点
1.不同数量服务的自我保护配置：微服务较多时开启**自我保护**，微服务较少时不开启**自我保护**
> 第一种情况：假如一共有5个微服务，1个真实不可用，注册成功率80%，触发自我保护，不剔除服务，则会频繁访问不可用服务  
> 第二种情况：假如一共有100个微服务，其中15个由于网络原因不能成功注册，5个真实不可用，注册成功率80%，触发自我保护，保护了15个有效服务
```
eureka:
  server:
    # 开启自我保护机制
    enable-self-preservation: true
    # 自我保护阀值
    renewal-percent-threshold: 0.85
```
2.快速下线配置：当服务不可用时，快速剔除服务。避免客户端拉取到不可用服务列表
```
eureka:
  server:
    # 剔除服务时间间隔
    eviction-interval-timer-in-ms: 1000
```
源码：
```
// 使用Timer的不好处：多线程并行处理定时任务时，Timer运行多个TimeTask时，只要其中之一没有捕获抛出的异常，其他任务便会自动终止运行，使用ScheduledExecutorService则没有这个问题。
this.evictionTimer.schedule((TimerTask)this.evictionTaskRef.get(), this.serverConfig.getEvictionIntervalTimerInMs(), this.serverConfig.getEvictionIntervalTimerInMs());
```
3.关闭从三级缓存读注册表或者降低二三级缓存同步时间间隔
```
eureka:
  server:
    # 2选1
    # 1.关闭从readOnly读注册表
    use-read-only-response-cache: false
    # 2.readWrite 和 readOnly 同步时间间隔。
    response-cache-update-interval-ms: 1000
```
原理：
eureka的三级缓存
```
// 一级缓存：map<服务名，map<实例id，实例信息>>
ConcurrentHashMap<String, Map<String, Lease<InstanceInfo>>> registry
// 二级缓存：
LoadingCache<Key, ResponseCacheImpl.Value> readWriteCacheMap
// 三级缓存：
ConcurrentMap<Key, ResponseCacheImpl.Value> readOnlyCacheMap

每次客户端注册，会写入registry注册表，同时将二级缓存失效   
每次客户端拉取服务列表，
    （如果useReadOnlyCache是true）先从三级缓存取，如果三级缓存是空，在从二级缓存取（并且赋值到三级缓存）
    （如果useReadOnlyCache是false）直接从二级缓存取
默认情况下定时任务每30s将readWriteCacheMap同步至readOnlyCacheMap
```
为什么说eureka实现了CAP种的AP，没有实现C  
首先CAP表示一致性（Consistency）、可用性（Availability）、分区容错性（Partition tolerance）  
由于三级缓存中，readWriteCacheMap和readOnlyCacheMap每30秒做一次同步，所以不是强一致性




