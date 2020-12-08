# Eureka Server优化

## eureka启动原理
引入```spring-cloud-starter-netflix-eureka-server```依赖  
并且添加```@EnableEurekaServer```注解，是如何启动了eureka server的

> @EnableEurekaServer注解本质是向ioc容器注入了一个 Marker 类的对象（看作开关）  
> 当存在Mark类对象时，eureka包的启动类EurekaServerAutoConfiguration就会启动  
> 启动类又引入了EurekaServerInitializerConfiguration类，引入这个类就会运行这个类中的start方法。  
> 因为start是重写了org.springframework.context包的start方法。所以只要引入就会调用（生命周期函数）  
> start方法：启动了一个线程，1.从peer拉取注册表2.启动定时剔除任务（自我保护）


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
为什么说eureka实现了CAP中的AP，没有实现C  
首先CAP表示一致性（Consistency）、可用性（Availability）、分区容错性（Partition tolerance）  
一致性（C）：在分布式系统中的所有数据备份，在同一时刻是否同样的值。（等同于所有节点访问同一份最新的数据副本）  
可用性（A）：在集群中一部分节点故障后，集群整体是否还能响应客户端的读写请求。（对数据更新具备高可用性）  
分区容忍性（P）：以实际效果而言，分区相当于对通信的时限要求。系统如果不能在时限内达成数据一致性，就意味着发生了分区的情况，必须就当前操作在C和A之间做出选择。  
1.由于三级缓存中，readWriteCacheMap和readOnlyCacheMap每30秒做一次同步，所以不是强一致性  
2.从其他peer拉取注册表。```int registryCount = this.registry.syncUp();```因为启动eureka server的时候才去拉取，所以不是强一致性的  
3.网络不好的情况，定时剔除（非即时）保证了P  
4.多个eureka server保证A

生产环境问题：服务重启时，先停服，在手动下线（否则可能下线后停服前又续约了，导致白下线了）


eureka server源码主要关注功能：
剔除（本质也是下面的下线）。长时间没有心跳的服务，eureka server将它从注册表剔除  
注册  
续约  
下线  
集群间同步  
拉取注册表
## 服务测算

20个服务，每个服务部署5个实例   eureka client：100个
不调优的情况下。
每分钟心跳：100*2 = 200 次
每分钟拉取：100*2 = 200 次

则每天访问量：400*60*24 = 57.6W


