package fun.enhui.userconsumer.controller;

import fun.enhui.userconsumer.api.ConsumerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    @GetMapping("/alive")
    public String alive() {
        // 使用feign调用
        return consumerApi.alive();
    }

    @GetMapping("/getMapById")
    public Map<Integer, String> getMapById(Integer id) {
        return consumerApi.getMap(id);
    }

    @GetMapping("/getMap2")
    Map<Integer, String> getMap2(Integer id, String name) {
        return consumerApi.getMap2(id, name);
    }

    @GetMapping("/getMap3")
    public Map<String, String> getMap3(@RequestParam Map<String, Object> map) {
        System.out.println(map);
        return consumerApi.getMap3(map);
    }

    @GetMapping("/getMap4")
    public Map<String, String> getMap4(@RequestParam Map<String, Object> map) {

        System.out.println(map);
        return consumerApi.postMap(map);
    }
}
