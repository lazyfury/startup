package io.sf.admin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean isStaff;
    private Long tenantId;
    private Long merchantId;
    private List<Long> roles;
}

