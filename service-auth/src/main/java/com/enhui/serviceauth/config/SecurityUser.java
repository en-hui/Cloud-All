package com.enhui.serviceauth.config;

import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * spring-security 用户对象
 *
 * @author 胡恩会
 * @date 2020/12/14 23:47
 */
@Setter
public class SecurityUser implements UserDetails {
    /**
     * 登录用户名
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 帐户未过期
     */
    private boolean accountNonExpired = true;

    /**
     * 帐户未锁定
     */
    private boolean accountNonLocked = true;

    /**
     * 证书未过期
     */
    private boolean credentialsNonExpired = true;

    /**
     * 是否可用
     */
    private boolean enabled = true;

    /**
     * 用户权限列表
     */
    private List<SecurityAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
