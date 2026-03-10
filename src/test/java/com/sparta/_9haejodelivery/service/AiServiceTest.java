package com.sparta._9haejodelivery.service;

import com.google.genai.Client;
import com.google.genai.Models;
import com.google.genai.types.GenerateContentResponse;
import com.sparta._9haejodelivery.domain.Ai;
import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.domain.UserRole;
import com.sparta._9haejodelivery.dto.AiResponseDto;
import com.sparta._9haejodelivery.repository.AiLogRepository;
import com.sparta._9haejodelivery.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AiServiceTest {
    private AiService aiService;
    private AiLogRepository aiLogRepository;
    private UserRepository userRepository;

    User user;
    Ai aiLog;

    @BeforeEach
    void setUp() {
        aiLogRepository= mock(AiLogRepository.class);
        userRepository= mock(UserRepository.class);

        aiService = new AiService(aiLogRepository,userRepository);

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
        ReflectionTestUtils.setField(aiService, "geminiKey", "test-api-key");
    }

    @Test
    @DisplayName("AI 요청 및 응답 테스트")
    void aiRequestAndResponseTest() {
        try (MockedStatic<Client> mockedClientStatic = mockStatic(Client.class)) {

            Client mockClient = mock(Client.class, RETURNS_DEEP_STUBS);
            Client.Builder mockBuilder = mock(Client.Builder.class);
            Models mockModels = mock(Models.class);
            ReflectionTestUtils.setField(mockClient, "models", mockModels);
            GenerateContentResponse mockResponse = mock(GenerateContentResponse.class);

            mockedClientStatic.when(Client::builder).thenReturn(mockBuilder);
            when(mockBuilder.apiKey(anyString())).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(mockClient);

            when(mockModels.generateContent(anyString(), anyString(), any())).thenReturn(mockResponse);
            when(mockResponse.text()).thenReturn("test용 답변");

            when(userRepository.findByUsername("owner")).thenReturn(Optional.of(user));
            when(aiLogRepository.save(any(Ai.class))).thenReturn(aiLog);

            AiResponseDto result = aiService.ask("owner", "requestData");

            ArgumentCaptor<Ai> captor = ArgumentCaptor.forClass(Ai.class);
            verify(aiLogRepository).save(captor.capture());

            Ai saved = captor.getValue();
            assertThat(result.getText()).isEqualTo("test용 답변");
            assertThat(saved.getRequestData()).isEqualTo("requestData");
            assertThat(saved.getUser().getUsername()).isEqualTo("owner");
        }
    }
}