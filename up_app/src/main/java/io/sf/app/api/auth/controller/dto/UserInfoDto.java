package io.sf.app.api.auth.controller.dto;

import io.sf.modules.auth.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {
    private Long id;
    private String username;
    private Boolean enabled;
    private Long tenantId;

    public UserInfoDto(User user){
        id = user.getId();
        username = user.getUsername();
        enabled = user.getEnabled();
        tenantId = user.getTenantId();
    }
}
