package com.fuzentrix.backend.entity;

import com.fuzentrix.backend.enums.ItemType;
import com.fuzentrix.backend.enums.PublishStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "section_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionItem extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private CourseSection section;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PublishStatus status = PublishStatus.DRAFT;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    // One-to-One content links
    @OneToOne(mappedBy = "item", fetch = FetchType.LAZY)
    private VideoContent videoContent;

    @OneToOne(mappedBy = "item", fetch = FetchType.LAZY)
    private ResourceContent resourceContent;

    @OneToOne(mappedBy = "item", fetch = FetchType.LAZY)
    private CodingTask codingTask;

    @OneToOne(mappedBy = "item", fetch = FetchType.LAZY)
    private Quiz quiz;
}
