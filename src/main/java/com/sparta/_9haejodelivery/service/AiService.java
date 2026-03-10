package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.Ai;
import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.dto.AiResponseDto;
import com.sparta._9haejodelivery.repository.AiLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {
    private final AiLogRepository aiLogRepository;
    private final ChatClient chatClient;

    public AiResponseDto ask(User user, String request){
        String response = chatClient.prompt()
                .system("""
                        당신은 고객이 입력한 정보로 해당 제품에 대한 설명을 만들어줍니다. 50자 내로 작성하세요." +
                        내용:
                        """)
                .user(request)
                .call()
                .content();
        String aiLogId=aiLogRepository.save(Ai.builder()
                .user(user)
                .requestData(request)
                .responseData(response).build()).getAiLogId().toString();
        return AiResponseDto.builder()
                .aiLogId(aiLogId)
                .text(response).build();
    }
}
