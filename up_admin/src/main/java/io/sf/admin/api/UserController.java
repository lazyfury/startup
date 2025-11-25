package io.sf.admin.api;

import io.sf.modules.auth.entity.User;
import io.sf.modules.auth.repository.UserRepository;
import io.sf.utils.crud.CrudApiController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
@Tag(name = "用户权限/用户管理", description = "用户管理接口（Admin）")
public class UserController extends CrudApiController<User, Long, UserRepository> {
    protected UserController(UserRepository repository) {
        super(repository);
    }

    @Override
    protected Class<User> entityClass() {
        return User.class;
    }
}

