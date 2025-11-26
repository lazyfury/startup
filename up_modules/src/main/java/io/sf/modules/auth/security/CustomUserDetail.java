package io.sf.modules.auth.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.sf.modules.auth.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class CustomUserDetail implements UserDetails {

    @Getter
    private final Long id;
    private final String username;

    @JsonIgnore
    private final String password;
    private final Boolean enabled;
    @JsonIgnore
    private final Collection<? extends GrantedAuthority> authorities;

    @Getter
    @JsonIgnore
    private final User user;

    public CustomUserDetail(User user, Collection<? extends GrantedAuthority> authorities) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.enabled = Boolean.TRUE.equals(user.getEnabled());
        this.authorities = authorities != null ? authorities : Collections.emptyList();
        this.user = user;
    }

    public static CustomUserDetail from(User user) {
        return new CustomUserDetail(user, Collections.emptyList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return Boolean.TRUE.equals(enabled);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomUserDetail)) return false;
        CustomUserDetail that = (CustomUserDetail) o;
        return Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
