package io.sf.app.api.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResult {
    private long userId;
    private UserInfoDto user;
    private String token;
}
