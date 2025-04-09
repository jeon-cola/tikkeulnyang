package com.c107.ledger.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GenerateMemoRequest {
    private LocalDate date;
}
