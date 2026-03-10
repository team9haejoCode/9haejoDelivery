package com.sparta._9haejodelivery.repository;

import com.sparta._9haejodelivery.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

}
