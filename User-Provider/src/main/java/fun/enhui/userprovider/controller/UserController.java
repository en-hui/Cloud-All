package fun.enhui.userprovider.controller;

import fun.enhui.userapi.controller.api.UserApi;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务提供者
 *
 * @author 胡恩会
 * @date 2020/11/22 23:29
 */
@RestController
public class UserController implements UserApi {
    /**
     * 返回服务状态
     **/
    @Override
    public String alive() {
        return "User-Provider:ok";
    }

    @GetMapping("/getMap1")
    public Map<Integer, String> getMap(Integer id) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "provider：key是1");
        return map;
    }

    @GetMapping("/getMap2")
    Map<Integer, String> getMap2(Integer id, String name) {
        Map<Integer, String> map = new HashMap<>();
        map.put(id, name);
        return map;
    }

    @GetMapping("/getMap3")
    public Map<String, String> getMap3(@RequestParam Map<String, Object> map) {
        Map<String, String> result = new HashMap<>();
        result.put("method", "get");
        result.put("name", map.get("name").toString());
        return result;
    }

    @PostMapping("/postMap")
    public Map<String, String> postMap(@RequestBody Map<String, Object> map) {
        Map<String, String> result = new HashMap<>();
        result.put("method", "post");
        result.put("name", map.get("name").toString());
        return result;
    }

}
