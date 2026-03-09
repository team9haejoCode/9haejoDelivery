package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.dto.AiResponseDto;
import com.sparta._9haejodelivery.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI", description = "AI 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ai")
public class AiController {
    private final AiService aiLogService;

    @Operation(summary = "AI 요청 처리", description = "AI 요청을 처리 후 응답 반환.")
    @PostMapping("/")
    public AiResponseDto aiLog(@AuthenticationPrincipal UserDetails userDetails,
                               @Valid @RequestBody AiResponseDto requestDto) {
        User user=/*userDetails.getUser()*/null;
        return aiLogService.ask(user, requestDto.text);
    }
}
