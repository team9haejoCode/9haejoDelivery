package com.sparta._9haejodelivery.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.sparta._9haejodelivery.domain.Ai;
import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.dto.AiResponseDto;
import com.sparta._9haejodelivery.repository.AiLogRepository;
import com.sparta._9haejodelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AiService {
    private final AiLogRepository aiLogRepository;
    private final UserRepository userRepository;

    private final String geminiKey = System.getenv("GOOGLE_AI_STUDIO_KEY");
    private final String geminiModel = "models/gemini-2.5-flash";

    public AiResponseDto ask(String username, String request){
        User user=userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        String prompt = """
                        당신은 고객이 입력한 정보로 해당 제품에 대한 설명을 만들어줍니다. 50자 내로 작성하세요." +
                        내용:
                        """
                +request;

        GenerateContentResponse response;
        try (Client client = Client.builder().apiKey(geminiKey).build()) {

            response = client.models.generateContent(
                    geminiModel,
                    prompt,
                    null);
        }catch (Exception e) {
            throw new RuntimeException("AI 호출 중 에러 발생: " + e.getMessage());
        }

        String aiLogId=aiLogRepository.save(Ai.builder()
                .user(user)
                .requestData(request)
                .responseData(response.text()).build()).getAiLogId().toString();

        return AiResponseDto.builder()
                .aiLogId(aiLogId)
                .text(response.text()).build();
    }
}
