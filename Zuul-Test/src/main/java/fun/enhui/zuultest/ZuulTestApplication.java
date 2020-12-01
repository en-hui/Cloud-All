package fun.enhui.zuultest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author 胡恩会
 * @date 2020/11/30 23:28
 */
@SpringBootApplication
@EnableZuulProxy
public class ZuulTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulTestApplication.class, args);
    }
}
