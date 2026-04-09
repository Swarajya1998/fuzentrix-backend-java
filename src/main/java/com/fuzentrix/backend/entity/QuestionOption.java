package com.fuzentrix.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "question_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuizQuestion question;

    @NotBlank
    @Column(name = "option_text", columnDefinition = "TEXT", nullable = false)
    private String optionText;

    @Column(name = "attached_image_s3_key")
    private String attachedImageS3Key;

    @Builder.Default
    @Column(name = "is_correct")
    private Boolean isCorrect = false;

    @Column(columnDefinition = "TEXT")
    private String explanation;
}
