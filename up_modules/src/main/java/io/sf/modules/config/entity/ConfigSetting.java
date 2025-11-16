package io.sf.modules.config.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "config_setting",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_scope_key", columnNames = {"tenant_id", "merchant_id", "cfg_key"})
        },
        indexes = {
                @Index(name = "idx_tenant_key", columnList = "tenant_id,cfg_key"),
                @Index(name = "idx_merchant_key", columnList = "merchant_id,cfg_key")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 空=全局；有值=租户范围
    @Column(name = "tenant_id")
    private Long tenantId;

    // 空=非商户级；有值=商户范围
    @Column(name = "merchant_id")
    private Long merchantId;

    // 配置键
    @Column(name = "cfg_key", nullable = false)
    private String key;

    // 配置值（字符串存储，必要时使用JSON）
    @Column(name = "cfg_value", length = 4000)
    private String value;

    // 值类型：string,json,int,bool 等
    @Column(name = "value_type")
    private String valueType;

    @Column(name = "enabled")
    private Boolean enabled = true;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}