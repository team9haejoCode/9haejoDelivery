package com.sparta._9haejodelivery.controller;

import com.sparta._9haejodelivery.common.ApiResponse;
import com.sparta._9haejodelivery.dto.*;
import com.sparta._9haejodelivery.global.security.UserDetailsImpl;
import com.sparta._9haejodelivery.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@EnableMethodSecurity
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        userService.signup(requestDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, "회원가입이 완료되었습니다."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refresh(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");

        Map<String, String> tokens = userService.reissue(refreshToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(HttpStatus.OK, "토큰이 재발급되었습니다.", tokens));
    }

    @GetMapping
    @PreAuthorize("hasRole('MANAGER')") // 관리자만 접근 가능
    @Operation(summary = "전체 유저 조회 (관리자)")
    public ResponseEntity<ApiResponse<Page<UserResponseDto>>> getUsers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        int validatedSize = UserSearchRequestDto.validateSize(pageable.getPageSize());

        if (pageable.getPageSize() != validatedSize) {
            pageable = PageRequest.of(pageable.getPageNumber(), validatedSize, pageable.getSort());
        }

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK, "유저 조회가 완료되었습니다.", userService.getUsers(pageable)));
    }

    @PatchMapping("/profile/edit")
    @Operation(summary = "유저 정보 수정")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserUpdateProfileRequestDto requestDto) {

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK, "프로필 수정이 완료되었습니다.",
                userService.updateProfile(userDetails.getUsername(), requestDto)));
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자가 스스로 탈퇴를 수행합니다.")
    public ResponseEntity<ApiResponse<Void>> withdraw(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.withdraw(userDetails.getUsername(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK, "탈퇴가 정상적으로 처리되었습니다.", null));
    }

    @DeleteMapping("/withdraw/{username}")
    @PreAuthorize("hasRole('MANAGER')") // 관리자만 접근 가능
    @Operation(summary = "유저 강제 탈퇴(관리자)", description = "관리자가 특정 사용자를 강제로 탈퇴 처리합니다.")
    public ResponseEntity<ApiResponse<Void>> withdrawByAdmin(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetailsImpl adminDetails) {

        userService.withdraw(username, adminDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK, "사용자가 강제 탈퇴 처리되었습니다.", null));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "DB에 저장된 리프레시 토큰을 삭제하여 재발급을 차단합니다.")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        userService.logout(userDetails.getUsername());

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK, "사용자가 로그아웃 되었습니다.", null));
    }

    @GetMapping("/profile")
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 유저 본인의 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMyProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK, "내 프로필 조회가 완료되었습니다.",
                userService.getUserProfile(userDetails.getUsername())));
    }

    @GetMapping("/{username}/profile")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "특정 유저 상세 조회(관리자)", description = "관리자가 특정 유저의 상세 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserProfileByAdmin(
            @PathVariable String username) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK, "유저 상세 정보 조회가 성공하였습니다.",
                userService.getUserProfile(username)));
    }

}
