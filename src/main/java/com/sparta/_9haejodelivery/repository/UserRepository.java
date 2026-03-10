package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByNickname(String nickname);

    Optional<User> findByUsername(String username);

    Page<User> findAllByDeletedAtIsNull(Pageable pageable);

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    boolean existsByNicknameAndDeletedAtIsNull(String nickname);


}
