package com.fuzentrix.backend.entity;

import com.fuzentrix.backend.enums.PublishStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "item_revisions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRevision extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private SectionItem item;

    @Column(name = "proposed_payload", columnDefinition = "jsonb")
    private String proposedPayload; // Can be improved with custom Jackson mapping later

    @Enumerated(EnumType.STRING)
    private PublishStatus status;

    @Column(name = "reviewer_id")
    private UUID reviewerId;

    @Column(name = "feedback_comments", columnDefinition = "TEXT")
    private String feedbackComments;

    @Column(name = "reviewed_at")
    private OffsetDateTime reviewedAt;
}
