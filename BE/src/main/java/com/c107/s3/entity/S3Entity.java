package com.c107.s3.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "images", schema = "catcat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @Column(name = "url", length = 255)
    private String url;

    @Column(name = "extension", length = 4)
    private String extension;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "usage_type", length = 255)
    private String usageType;  // 예: "PROFILE", "CHALLENGE", "CARD"

    @Column(name = "usage_id")
    private Integer usageId;   // 예: 유저 아이디 (프로필 이미지인 경우)
}
