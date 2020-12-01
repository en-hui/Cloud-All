package fun.enhui.admintest;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 胡恩会
 * @date 2020/12/1 22:48
 */
@SpringBootApplication
@EnableAdminServer
public class AdminTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminTestApplication.class, args);
    }
}
