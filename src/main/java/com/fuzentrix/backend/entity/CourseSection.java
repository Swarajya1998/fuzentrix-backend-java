package com.fuzentrix.backend.entity;

import com.fuzentrix.backend.enums.PublishStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "course_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSection extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotBlank
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PublishStatus status = PublishStatus.DRAFT;

    @Column(name = "is_deleted")
    @Builder.Default
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<SectionItem> items = new ArrayList<>();
}
