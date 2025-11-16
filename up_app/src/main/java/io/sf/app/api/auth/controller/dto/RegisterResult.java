package io.sf.app.api.auth.controller.dto;

import io.sf.modules.auth.security.CustomUserDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResult {
    private long userId;
    private CustomUserDetail user;
}
