package com.fuzentrix.backend.entity;

import com.fuzentrix.backend.enums.QuestionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @NotBlank
    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(name = "attached_image_s3_key")
    private String attachedImageS3Key;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Builder.Default
    private Integer points = 1;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionOption> options = new ArrayList<>();
}
