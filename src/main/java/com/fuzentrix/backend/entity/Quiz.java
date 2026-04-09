package com.fuzentrix.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private SectionItem item;

    @Column(name = "passing_score_percentage")
    private Integer passingScorePercentage;

    @Column(name = "max_attempts")
    private Integer maxAttempts;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<QuizQuestion> questions = new ArrayList<>();
}
