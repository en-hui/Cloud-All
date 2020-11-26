package fun.enhui.userconsumer.api;

import fun.enhui.userapi.controller.api.UserApi;
import fun.enhui.userconsumer.fallback.UserProviderBack;
import fun.enhui.userconsumer.fallback.UserProviderBackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 继承公共api模块，什么都不用写
 *
 * @author 胡恩会
 * @date 2020/11/23 0:16
 */
@FeignClient(name = "UserProvider", fallbackFactory = UserProviderBackFactory.class)
public interface ConsumerApi extends UserApi {
    /**
     * 带参数的
     **/
    @GetMapping("/getMap1")
    Map<Integer, String> getMap(@RequestParam("id") Integer id);

    @GetMapping("/getMap2")
    Map<Integer, String> getMap2(@RequestParam("id") Integer id, @RequestParam("name") String name);

    @GetMapping("/getMap3")
    Map<String, String> getMap3(@RequestParam Map<String, Object> map);

    @PostMapping("/postMap")
    Map<String, String> postMap(Map<String, Object> map);
}
