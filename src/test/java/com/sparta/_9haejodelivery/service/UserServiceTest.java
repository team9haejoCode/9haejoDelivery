package com.sparta._9haejodelivery.service;

import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.domain.UserRole;
import com.sparta._9haejodelivery.dto.UserResponseDto;
import com.sparta._9haejodelivery.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private List<User> allUsers;

    @BeforeEach
    void setUp() {
        allUsers = IntStream.rangeClosed(1, 55)
                .mapToObj(i -> User.builder()
                        .username("user" + i)
                        .nickname("테스터" + i)
                        .password("Sparta123!!")
                        .address("주소 ")
                        .role(UserRole.CUSTOMER)
                        .build())
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("유저 55명 대량 조회 테스트 - 6페이지 확인")
    void getUsers_Pagination_Test() {
        // 2. Given: 6번째 페이지(인덱스 5), 사이즈 10 요청
        int pageNumber = 5;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // 3. Mockito를 위한 슬라이싱 (55개 중 51~55번 유저만 추출)
        int start = (int) pageable.getOffset(); // 50
        int end = Math.min((start + pageable.getPageSize()), allUsers.size()); // 55
        List<User> subList = allUsers.subList(start, end);

        Page<User> fakePage = new PageImpl<>(subList, pageable, allUsers.size());

        when(userRepository.findAllByDeletedAtIsNull(pageable)).thenReturn(fakePage);

        // 4. When: 서비스 실행
        Page<UserResponseDto> result = userService.getUsers(pageable);

        System.out.println("========================================");
        System.out.println("전체 유저 수: " + result.getTotalElements());
        System.out.println("현재 페이지: " + (result.getNumber() + 1));
        System.out.println("페이지 내 데이터 수: " + result.getContent().size());
        System.out.println("--- 유저 목록 ---");
        result.getContent().forEach(u ->
                System.out.println("ID: " + u.getUsername() + " | 닉네임: " + u.getNickname())
        );
        System.out.println("========================================");

        assertEquals(55, result.getTotalElements());
        assertEquals(5, result.getContent().size());
        assertEquals("user51", result.getContent().get(0).getUsername());
    }
}
