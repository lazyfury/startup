package io.sf.modules.auth.security;

import io.sf.modules.auth.entity.User;
import io.sf.modules.auth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserService userService;


    

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.getUserByUsername(username).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        Collection<? extends GrantedAuthority> authorities = userService.getAuthorities(user);
        return new CustomUserDetail(user, authorities);
    }
}
