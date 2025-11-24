package io.sf.modules.auth.service;

import io.sf.modules.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import java.util.Optional;
import java.util.Collection;



public interface IUserService {
    public Optional<User> getUserByUsername(String username);
    public int registerUser(User user);

    Optional<User> findUserById(Long id);
    Optional<User> loginUser(String username, String username1);
    Collection<? extends GrantedAuthority> getAuthorities(User user);
}
