package com.c107.subscribe.dto;
import com.c107.subscribe.entity.SubscribeEntity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscribeRequestDto {

    @NotBlank(message = "구독 이름은 필수입니다.")
    private String subscribeName;

    @NotNull(message = "구독 가격은 필수입니다.")
    private Integer subscribePrice;

    private String paymentDate;

    public SubscribeEntity toEntity() {
        Integer paymentDay = parsePaymentDate(paymentDate);

        return SubscribeEntity.builder()
                .subscribeName(subscribeName)
                .subscribePrice(subscribePrice)
                .paymentDate(paymentDay)
                .build();
    }

    private Integer parsePaymentDate(String dateStr) {
        try {
            return Integer.parseInt(dateStr);
        } catch (NumberFormatException e) {
            // 날짜 형식이 숫자가 아닌 경우 기본값 1로 설정
            return 1;
        }
    }
}