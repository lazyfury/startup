package io.sf.modules.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

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

@Column(name = "tenant_id")
private Long tenantId;

@CreationTimestamp
@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;

@UpdateTimestamp
@Column(name = "updated_at")
private LocalDateTime updatedAt;
}
