package com.enhui.serviceauth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Spring Security 配置类
 *
 * @author 胡恩会
 * @date 2020/12/14 23:17
 */
@EnableWebSecurity
@Configuration
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityUserDetailsServiceImpl securityUserDetailsServiceImpl;

    /**
     * 设置加密方式 BCryptPasswordEncoder
     **/
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        log.debug("enter passwordEncoder method...");
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        log.debug("enter configure-auth method...");
        // 自定义查询用户方法
        auth.userDetailsService(securityUserDetailsServiceImpl);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.debug("enter configure-http method...");
        http
                //开启登录配置
                .authorizeRequests()
                //表示访问 /hello 这个接口，需要具备 admin 这个角色
                .antMatchers("/hello").hasRole("admin")
                //表示剩余的其他接口，登录之后就能访问
                .anyRequest().authenticated()
                .and()
                // 使用自带的登录
                .formLogin()
                //定义登录时，用户名的 key，默认为 username
                .usernameParameter("username")
                //定义登录时，用户密码的 key，默认为 password
                .passwordParameter("password")
                //登录成功的处理器
                .successHandler(new LoginSuccessHandler())
                .failureHandler(new LoginFailureHandler())
                .and()
                .httpBasic();

    }
}
