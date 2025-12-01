package io.sf.admin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean isStaff;
    private Long tenantId;
    private Long merchantId;
}

