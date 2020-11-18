package fun.enhui.eurekaprovider.controller;

import fun.enhui.eurekaprovider.service.HealthStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 胡恩会
 * @date 2020/11/17 20:55
 */
@RestController
public class ApiController {

    @Autowired
    private HealthStatusService healthStatusService;

    /**
     * 生产者提供服务的接口
     **/
    @GetMapping("/get/hi")
    public String getHi() {
        return "hi";
    }

    /**
     * 健康检查
     **/
    @GetMapping("/health")
    public String health(@RequestParam("status") Boolean status) {
        healthStatusService.setStatus(status);
        return healthStatusService.getStatus();
    }
}
