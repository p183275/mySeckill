package com.feng.seckill.security;

import com.feng.seckill.entitys.vo.UserVO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author : pcf
 * @date : 2022/1/15 17:09
 */
public class SecurityUser implements UserDetails {
    //当前登录用户
    private transient UserVO currentUserInfo;

    //当前权限
    private List<String> permissionValueList;
    public SecurityUser() {
    }
    public SecurityUser(UserVO loginUserVO) {
        if (loginUserVO != null) {
            this.currentUserInfo = loginUserVO;
        }
    }

    public List<String> getPermissionValueList() {
        return permissionValueList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (permissionValueList != null){
            for(String permissionValue : permissionValueList) {
                if(StringUtils.isEmpty(permissionValue)) continue;
                SimpleGrantedAuthority authority = new
                        SimpleGrantedAuthority(permissionValue);
                authorities.add(authority);
            }
        }
        return authorities;
    }

    // 拿到当前登录用户
    public UserVO getLoginUser(){
        return this.currentUserInfo;
    }
    public void setLoginUser(UserVO userVO){
        this.currentUserInfo = userVO;
    }

    @Override
    public String getPassword() {
        return currentUserInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return currentUserInfo.getLoginAccount();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
