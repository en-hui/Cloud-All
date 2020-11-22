package fun.enhui.userconsumer.controller;

import fun.enhui.userconsumer.api.ConsumerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 胡恩会
 * @date 2020/11/22 23:29
 */
@RestController
@RequestMapping("/userconsumer")
public class UserController {

    @Autowired
    private ConsumerApi consumerApi;

    /**
     * 返回服务状态
     **/
    @GetMapping("alive")
    public String alive() {
        // 使用feign调用
        return consumerApi.alive();
    }
}
