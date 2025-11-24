package io.sf.modules.menu.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "code", nullable = false, length = 64)
    private String code;

    @Column(name = "path", length = 256)
    private String path;

    @Column(name = "component", length = 256)
    private String component;

    @Column(name = "redirect", length = 256)
    private String redirect;

    @Column(name = "icon", length = 64)
    private String icon;

    @Column(name = "type", length = 16)
    private String type;

    @Column(name = "order_no")
    private Integer orderNo = 0;

    @Column(name = "hidden")
    private Boolean hidden = Boolean.FALSE;

    @Column(name = "status", nullable = false)
    private Boolean status = Boolean.TRUE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Transient
    private List<Menu> children = new ArrayList<>();
}

