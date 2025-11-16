package io.sf.modules.auth.mapper;

import io.sf.modules.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {

   public Optional<User> getUserByUsername(String username);

   public int insertUser(User user);

}
