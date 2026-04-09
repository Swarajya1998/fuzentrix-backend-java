package com.fuzentrix.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "student_quiz_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentQuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "progress_id", nullable = false)
    private StudentProgress progress;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "score_percentage")
    private Integer scorePercentage;

    @Column(name = "is_passing")
    private Boolean isPassing;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;
}
