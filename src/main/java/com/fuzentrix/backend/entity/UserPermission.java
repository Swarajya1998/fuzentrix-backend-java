package com.fuzentrix.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPermission {

    @EmbeddedId
    private UserPermissionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("permissionId")
    @JoinColumn(name = "permission_id")
    private Permission permission;

    @Column(name = "is_granted", nullable = false)
    @Builder.Default
    private Boolean isGranted = true;
}
