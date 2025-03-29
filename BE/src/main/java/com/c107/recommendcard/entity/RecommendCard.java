package com.c107.recommendcard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name = "recommend_cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reco_card_id")
    private Integer recoCardId;

    @Column(name = "reco_card_name")
    private String recoCardName;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "corp_name")
    private String corpName;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "source_card_id")
    private Integer sourceCardId;
}
