package fun.enhui.userconsumer.api;

import fun.enhui.userapi.controller.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * 继承公共api模块，什么都不用写
 *
 * @author 胡恩会
 * @date 2020/11/23 0:16
 */
@FeignClient(name = "UserProvider")
public interface ConsumerApi extends UserApi {
}
