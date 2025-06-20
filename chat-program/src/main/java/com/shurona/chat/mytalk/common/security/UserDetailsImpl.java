package com.shurona.chat.mytalk.common.security;


import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * UserDetails를 커스터마이징하기 위한 클래스입니다.
 */
public record UserDetailsImpl(
    Long userId,
    Collection<? extends GrantedAuthority> authorities,
    String role
) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        throw new UnsupportedOperationException("UserDetailsImpl#Password() not implemented");
    }

    @Override
    public String getUsername() {
        return String.valueOf(userId);
    }
}
