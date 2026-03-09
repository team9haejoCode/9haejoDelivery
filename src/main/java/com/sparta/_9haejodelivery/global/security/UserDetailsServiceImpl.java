package com.sparta._9haejodelivery.global.security;

import com.sparta._9haejodelivery.domain.User;
import com.sparta._9haejodelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not Found " + username));
        //최신 상태의 user 객체

        if (user.getDeletedAt() != null) {
            throw new UsernameNotFoundException("탈퇴 처리된 계정입니다: " + username);
        }

        return new UserDetailsImpl(user);
    }
}
