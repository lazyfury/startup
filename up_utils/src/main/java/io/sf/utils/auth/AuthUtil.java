package io.sf.utils.auth;

import io.sf.modules.auth.security.CustomUserDetail;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthUtil {
    public  static CustomUserDetail getUser() throws InterruptedException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof  CustomUserDetail customUserDetail){
            return customUserDetail;
        }
        throw new InterruptedException("为获取到登录的用户");
    }
}
