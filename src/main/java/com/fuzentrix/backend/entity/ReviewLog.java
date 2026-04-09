package com.fuzentrix.backend.entity;

import com.fuzentrix.backend.enums.PublishStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "review_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private SectionItem item;

    @NotNull
    @Column(name = "reviewer_id", nullable = false)
    private UUID reviewerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private PublishStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status")
    private PublishStatus newStatus;

    @Column(name = "feedback_comments", columnDefinition = "TEXT")
    private String feedbackComments;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
