package io.sf.modules.config.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Check(constraints = "scope_type = 'SYSTEM' OR scope_id IS NOT NULL")
@Table(name = "config_setting", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"config_key", "scope_type", "scope_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "config_key", nullable = false, length = 96)
    private String key;

    @Lob
    @Column(name = "config_value", nullable = false)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope_type", nullable = false, length = 16)
    private ScopeType scopeType;

    @Column(name = "scope_id")
    private Long scopeId;

    @Column(name = "type", nullable = false, length = 64)
    private String type;

    @Column(name = "status", nullable = false)
    private Boolean status = Boolean.TRUE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}