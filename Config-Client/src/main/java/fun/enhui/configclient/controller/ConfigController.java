package fun.enhui.configclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 胡恩会
 * @date 2020/12/3 21:18
 */
@RestController
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    @Value("${myconfig}")
    String myconfig;

    @GetMapping("/get/myconfig")
    public String getName() {
        return myconfig;
    }
}
