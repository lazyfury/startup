package io.sf.app.api.auth.controller.dto;

import io.sf.modules.auth.security.CustomUserDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResult {
        private String token;
        private UserInfoDto user;
}
