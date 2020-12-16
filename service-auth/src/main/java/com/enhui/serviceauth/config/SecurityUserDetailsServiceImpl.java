package com.enhui.serviceauth.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义用户登录查询
 *
 * @author 胡恩会
 * @date 2020/12/14 23:23
 */
@Service
public class SecurityUserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        SecurityUser userDetails = new SecurityUser();
        userDetails.setUsername("username");
        userDetails.setPassword("password");
        userDetails.setAccountNonExpired(false);
        userDetails.setAccountNonLocked(false);
        userDetails.setCredentialsNonExpired(false);
        userDetails.setEnabled(false);
        return userDetails;
    }
}
