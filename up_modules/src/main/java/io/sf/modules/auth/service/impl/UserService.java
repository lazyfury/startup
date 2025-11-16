package io.sf.modules.auth.service.impl;

import io.sf.modules.auth.entity.User;
import io.sf.modules.auth.mapper.UserMapper;
import io.sf.modules.auth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }

    @Override
    public int registerUser(User user) {
        // 对明文密码进行加盐哈希存储（BCrypt 内置随机盐）
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        try {
            return userMapper.insertUser(user);
        } 
        catch(DuplicateKeyException e){
            throw new RuntimeException("用户名已存在", e);
        }
        catch (Exception e) {
            throw new RuntimeException("注册用户失败", e);
        }
    }

    // login 
    @Override
    public Optional<User> loginUser(String username, String password) {
        Optional<User> user = userMapper.getUserByUsername(username);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }
}
