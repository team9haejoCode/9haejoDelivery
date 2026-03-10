package com.sparta._9haejodelivery.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRequestDto {
    @Size(max=100, message="내용은 100자 이하여야 합니다.")
    public String text;
}
