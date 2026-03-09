package com.sparta._9haejodelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AiResponseDto {
    public String aiLogId;
    public String text;
}
