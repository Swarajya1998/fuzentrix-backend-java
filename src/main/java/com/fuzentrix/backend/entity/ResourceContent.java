package com.fuzentrix.backend.entity;

import com.fuzentrix.backend.enums.ResourceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "resource_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private SectionItem item;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource_type")
    private ResourceType resourceType;
}
