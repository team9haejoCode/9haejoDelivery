package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByNickname(String nickname);
}
