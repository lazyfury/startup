package io.sf.modules.auth.service;

import io.sf.modules.auth.entity.User;
import java.util.Optional;



public interface IUserService {
    public Optional<User> getUserByUsername(String username);
    public int registerUser(User user);

    Optional<User> loginUser(String username, String username1);
}
