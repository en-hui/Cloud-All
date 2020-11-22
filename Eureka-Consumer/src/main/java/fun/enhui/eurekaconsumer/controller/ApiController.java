package fun.enhui.eurekaconsumer.controller;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消费者
 * 调用eureka的rest请求
 *
 * @author 胡恩会
 * @date 2020/11/17 20:55
 */
@RestController
public class ApiController {
    @Autowired
    private DiscoveryClient cloudClient;

    @Qualifier("eurekaClient")
    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;

    AtomicInteger atomicInteger = new AtomicInteger();

    /**
     * 获取所有服务列表
     **/
    @GetMapping("/eureka/services")
    public List<String> listEurekaServices() {
        return cloudClient.getServices();
    }

    /**
     * 根据serviceId获取服务列表
     **/
    @GetMapping("/eureka/provider/instances")
    public List<ServiceInstance> listProviderInstances() {
        return cloudClient.getInstances("provider");
    }

    /**
     * 简单版本远程服务调用
     * 从eureka的服务列表中选第一个，远程调用
     **/
    @GetMapping("/rpc/get/hi")
    public String rpcGetHi() {
        // 使用服务名称，找列表
        List<InstanceInfo> providers = eurekaClient.getInstancesByVipAddress("provider", false);
        if (providers.size() > 0) {
            InstanceInfo instanceInfo = providers.get(0);
            if (InstanceInfo.InstanceStatus.UP.equals(instanceInfo.getStatus())) {
                String url = "http://" + instanceInfo.getHostName() + ":" + instanceInfo.getPort() + "/get/hi";
                String forEntity = restTemplate.getForObject(url, String.class);
                System.out.println(forEntity);
                return instanceInfo.getHostName() + instanceInfo.getPort() + forEntity;
            }
        }
        return null;
    }

    /**
     * 简单版本远程服务调用
     * 使用ribbon从eureka的服务列表中选一个服务，远程调用
     **/
    @GetMapping("/rpc/load/balance/get/hi")
    public String rpcLoadBalanceGetHi() {
        // ribbon 完成客户端的负载均衡，过滤掉down的节点
        ServiceInstance instance = loadBalancerClient.choose("provider");
        String url = "http://" + instance.getHost() + ":" + instance.getPort() + "/get/hi";
        String forEntity = restTemplate.getForObject(url, String.class);
        System.out.println(forEntity);
        return forEntity;
    }

    /**
     * 远程调用
     * 自定义负载均衡算法
     **/
    @GetMapping("/rpc/my/load/balance/get/hi")
    public String rpcMyLoadBalanceGetHi() {
        List<InstanceInfo> instanceInfos = eurekaClient.getInstancesByVipAddress("provider", false);

        // 随机
        int random = new Random().nextInt(instanceInfos.size());
        InstanceInfo instance1 = instanceInfos.get(random);
        String url1 = "http://" + instance1.getHostName() + ":" + instance1.getPort() + "/get/hi";

        // 轮询
        int increment = atomicInteger.getAndIncrement();
        InstanceInfo instance2 = instanceInfos.get(increment % instanceInfos.size());
        String url2 = "http://" + instance2.getHostName() + ":" + instance2.getPort() + "/get/hi";

        String forEntity1 = restTemplate.getForObject(url1, String.class);
        String forEntity2 = restTemplate.getForObject(url2, String.class);
        System.out.println("随机算法:" + forEntity1 + ",轮询算法:" + forEntity2);
        return "随机算法:" + forEntity1 + ",轮询算法:" + forEntity2;
    }

    /**
     * 自动拼url并负载访问
     **/
    @GetMapping("/auto/url/get/hi")
    public String ribbonGetHi() {
        // 自动处理url
        String url = "http://provider/get/hi";
        // 给 restTemplate 加注解 @LoadBalanced 实现负载均衡
        String response = restTemplate.getForObject(url, String.class);
        System.out.println(response);
        return response;
    }

}
