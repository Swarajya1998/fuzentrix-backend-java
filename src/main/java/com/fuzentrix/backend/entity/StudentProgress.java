package com.fuzentrix.backend.entity;

import com.fuzentrix.backend.enums.ProgressStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "student_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "enrollment_id", "item_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private SectionItem item;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ProgressStatus status = ProgressStatus.NOT_STARTED;

    @Column(name = "last_watched_position_seconds")
    @Builder.Default
    private Integer lastWatchedPositionSeconds = 0;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;
}
