package fun.enhui.userapi.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 公共api模块
 *
 * @author 胡恩会
 * @date 2020/11/23 0:14
 */
@RequestMapping("user")
public interface UserApi {

    /**
     * 最简单的调用
     **/
    @GetMapping("alive")
    String alive();
}
