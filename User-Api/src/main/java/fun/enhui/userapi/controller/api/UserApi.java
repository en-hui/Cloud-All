package fun.enhui.userapi.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 胡恩会
 * @date 2020/11/23 0:14
 */
@RequestMapping("user")
public interface UserApi {

    @GetMapping("alive")
    String alive();
}
