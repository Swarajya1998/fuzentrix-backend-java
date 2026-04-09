package com.fuzentrix.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "video_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private SectionItem item;

    @Column(name = "s3_bucket")
    private String s3Bucket;

    @Column(name = "s3_key")
    private String s3Key;

    @Column(name = "duration_seconds")
    private Long durationSeconds;
}
