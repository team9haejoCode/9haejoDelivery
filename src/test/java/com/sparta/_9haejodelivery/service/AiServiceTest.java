package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.Ai;
import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.domain.UserRole;
import com.sparta._9haejodelivery.dto.AiRequestDto;
import com.sparta._9haejodelivery.dto.AiResponseDto;
import com.sparta._9haejodelivery.repository.AiLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {
    private AiService aiService;
    private AiLogRepository aiLogRepository;

    User user;
    Ai aiLog;
    ChatClient chatClient;

    @BeforeEach
    void setUp() {
        aiLogRepository= mock(AiLogRepository.class);
        chatClient= mock(ChatClient.class);
        aiService=new AiService(aiLogRepository, chatClient);

        user = User.builder()
                .username("owner")
                .nickname("tester2")
                .password("1234")
                .address("address")
                .role(UserRole.OWNER).build();

        aiLog =Ai.builder()
                .user(user)
                .requestData("requestData")
                .responseData("responseData")
                .build();
        ReflectionTestUtils.setField(aiLog, "aiLogId", UUID.nameUUIDFromBytes("ai".getBytes()));
    }

    @Test
    @DisplayName("AI 요청 및 응답 테스트")
    void aiRequestAndResponseTest() {
        //given
        ChatClient.ChatClientRequestSpec requestSpec = mock(ChatClient.ChatClientRequestSpec.class);
        ChatClient.CallResponseSpec responseSpec = mock(ChatClient.CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec); // prompt() 호출 시 spec 반환
        when(requestSpec.system(anyString())).thenReturn(requestSpec); // system() 호출 시 자기 자신 반환
        when(requestSpec.user(anyString())).thenReturn(requestSpec);   // user() 호출 시 자기 자신 반환
        when(requestSpec.call()).thenReturn(responseSpec);             // call() 호출 시 응답 spec 반환
        when(responseSpec.content()).thenReturn("test용 답변"); // 마지막 답변
        when(aiLogRepository.save(any(Ai.class))).thenReturn(aiLog);

        AiRequestDto aiRequestDto = AiRequestDto.builder()
                .text("requestData")
                .build();

        //when
        AiResponseDto savedAi = aiService.ask(user,aiRequestDto.text);

        //then
        ArgumentCaptor<Ai> captor = ArgumentCaptor.forClass(Ai.class);
        verify(aiLogRepository).save(captor.capture());
        Ai saved = captor.getValue();
        assertThat(saved).isNotNull();
        assertThat(savedAi.getText()).isEqualTo("test용 답변");
        assertThat(saved.getRequestData()).isEqualTo("requestData");
        assertThat(saved.getUser().getUsername()).isEqualTo("owner");
    }
}