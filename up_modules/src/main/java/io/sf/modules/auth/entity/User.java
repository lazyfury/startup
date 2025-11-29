package io.sf.modules.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id")
private Long id;

@Column(name = "username", nullable = false, unique = true,length = 100)
private String username;

@Column(name = "password", nullable = false)
@JsonIgnore
private String password;

@Column(name = "enabled", nullable = false)
    private Boolean enabled = Boolean.TRUE;

    @Column(name = "is_staff")
    private Boolean isStaff = Boolean.FALSE;

@Column(name = "tenant_id")
private Long tenantId;

@Column(name = "merchant_id")
private Long merchantId;

@CreationTimestamp
@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private List<Long> roles = new ArrayList<>();

    @Transient
    private String tenantName;

    @Transient
    private String merchantName;

    public User(Long id, String username, String password, Boolean enabled, Boolean isStaff, Long tenantId, Long merchantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.isStaff = isStaff;
        this.tenantId = tenantId;
        this.merchantId = merchantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
