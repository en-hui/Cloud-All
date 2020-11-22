package fun.enhui.userprovider.controller;

import fun.enhui.userapi.controller.api.UserApi;
import org.springframework.web.bind.annotation.RestController;

/**
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
        return "ok";
    }
}
