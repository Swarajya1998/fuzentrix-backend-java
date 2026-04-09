package com.fuzentrix.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "coding_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private SectionItem item;

    @Column(name = "problem_statement", columnDefinition = "TEXT")
    private String problemStatement;

    @Column(name = "starter_code", columnDefinition = "TEXT")
    private String starterCode;

    @Column(name = "expected_output", columnDefinition = "TEXT")
    private String expectedOutput;
}
