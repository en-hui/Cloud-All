package com.enhui.serviceauth.config;

import org.springframework.security.core.GrantedAuthority;

/**
 * Spring-security 用户权限列表
 *
 * @author 胡恩会
 * @date 2020/12/14 23:48
 */
public class SecurityAuthority implements GrantedAuthority {

    private final String permissionCode;

    public SecurityAuthority(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    @Override
    public String getAuthority() {
        return permissionCode;
    }
}
