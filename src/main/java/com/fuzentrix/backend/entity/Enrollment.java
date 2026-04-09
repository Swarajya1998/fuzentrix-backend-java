package com.fuzentrix.backend.entity;

import com.fuzentrix.backend.enums.EnrollmentStatus;
import com.fuzentrix.backend.enums.EnrollmentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "course_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EnrollmentType type = EnrollmentType.MANUAL_ADMIN;

    @Column(name = "enrolled_by")
    private UUID enrolledBy;

    @Column(name = "enrolled_at")
    private OffsetDateTime enrolledAt;

    @Column(name = "completion_percentage")
    @Builder.Default
    private Integer completionPercentage = 0;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;
}
