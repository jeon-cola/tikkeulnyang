package com.c107.bucket.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum DayOfWeek {
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일");

    private final String koreanName;

    DayOfWeek(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public static List<String> getAllKoreanNames() {
        return Arrays.stream(DayOfWeek.values())
                .map(DayOfWeek::getKoreanName)
                .collect(Collectors.toList());
    }
}