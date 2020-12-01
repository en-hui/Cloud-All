# Sleuth & zipkin
## Sleuth介绍
Sleuth是Spring cloud的分布式跟踪解决方案。

    span(跨度)，基本工作单元。一次链路调用，创建一个span，

    span用一个64位id唯一标识。包括：id，描述，时间戳事件，spanId,span父id。

    span被启动和停止时，记录了时间信息，初始化span叫：root span，它的span id和trace id相等。

    trace(跟踪)，一组共享“root span”的span组成的树状结构 称为 trace，trace也有一个64位ID，trace中所有span共享一个trace id。类似于一颗 span 树。

    annotation（标签），annotation用来记录事件的存在，其中，核心annotation用来定义请求的开始和结束。
        CS(Client Send客户端发起请求)。客户端发起请求描述了span开始。
        SR(Server Received服务端接到请求)。服务端获得请求并准备处理它。SR-CS=网络延迟。
        SS（Server Send服务器端处理完成，并将结果发送给客户端）。表示服务器完成请求处理，响应客户端时。SS-SR=服务器处理请求的时间。
        CR（Client Received 客户端接受服务端信息）。span结束的标识。客户端接收到服务器的响应。CR-CS=客户端发出请求到服务器响应的总时间。

其实数据结构是一颗树，从root span 开始。

## sleuth搭建
每个需要监控的服务都加依赖
```
<!-- 引入sleuth依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-sleuth</artifactId>
</dependency>
```
## zipkin介绍
使用sleuth 排错看日志，很原始。刀耕火种，可以加入利器 zipkin。
zipkin是twitter开源的分布式跟踪系统。

原理收集系统的时序数据，从而追踪微服务架构中系统延时等问题。还有一个友好的界面。

由4个部分组成：

Collector、Storage、Restful API、Web UI组成

采集器，存储器，接口，UI。

原理：   
sleuth收集跟踪信息通过http请求发送给zipkin server，zipkin将跟踪信息存储，以及提供RESTful API接口，zipkin ui通过调用api进行数据展示。   
默认内存存储，可以用mysql，ES等存储。

## zipkin搭建
每个需要监控的服务都加依赖
```
<!-- zipkin -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-zipkin</artifactId>
</dependency>
```
每个需要监控的服务都加配置文件
```
spring:
  #zipkin,默认端口9411，需启动jar包
  zipkin:
    base-url: http://localhost:9411/
    #采样比例1
  sleuth:
    sampler:
      rate: 1  
```
下载jar包并启动(使用java -jar 运行jar包)
```
访问：https://zipkin.io/pages/quickstart.html选择下载方式
或直接下载：https://search.maven.org/remote_content?g=io.zipkin&a=zipkin-server&v=LATEST&c=exec

jar包放在 docs/Sleuth&zipkin 下了
java -jar zipkin-server-2.23.0-exec.jar
```

然后可以访问 http://localhost:9411/ 查看链路