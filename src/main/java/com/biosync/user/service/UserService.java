package com.biosync.user.service;

import com.biosync.common.exception.ApiException;
import com.biosync.user.domain.User;
import com.biosync.user.dto.UserMeResponse;
import com.biosync.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserMeResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));

        return new UserMeResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getBirthDate(),
                user.getRole(),
                user.getCreatedAt());
    }
}
