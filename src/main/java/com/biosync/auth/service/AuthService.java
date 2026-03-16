package com.biosync.auth.service;

import com.biosync.auth.domain.RefreshToken;
import com.biosync.auth.dto.AuthUserResponse;
import com.biosync.auth.dto.LoginRequest;
import com.biosync.auth.dto.LoginResponse;
import com.biosync.auth.dto.LogoutRequest;
import com.biosync.auth.dto.LogoutResponse;
import com.biosync.auth.dto.RefreshRequest;
import com.biosync.auth.dto.RefreshResponse;
import com.biosync.auth.dto.SignUpRequest;
import com.biosync.auth.dto.SignUpResponse;
import com.biosync.auth.repository.RefreshTokenRepository;
import com.biosync.common.exception.ApiException;
import com.biosync.security.AuthenticatedUser;
import com.biosync.security.JwtProperties;
import com.biosync.security.JwtTokenProvider;
import com.biosync.user.domain.Role;
import com.biosync.user.domain.User;
import com.biosync.user.domain.UserStatus;
import com.biosync.user.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public SignUpResponse signup(SignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException("CONFLICT", "Email already exists", HttpStatus.CONFLICT);
        }

        User user = userRepository.save(User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .name(request.name())
                .birthDate(request.birthDate())
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build());

        return new SignUpResponse(user.getId(), user.getEmail(), user.getName(), user.getRole());
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ApiException("NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));
        user.updateLastLoginAt(Instant.now());

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                true);

        String accessToken = jwtTokenProvider.generateAccessToken(authenticatedUser);
        String refreshTokenValue = UUID.randomUUID().toString();

        refreshTokenRepository.findByUserAndRevokedFalse(user).forEach(RefreshToken::revoke);
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(refreshTokenValue)
                .expiresAt(Instant.now().plusSeconds(jwtProperties.refreshExpiration()))
                .revoked(false)
                .build());

        return new LoginResponse(
                accessToken,
                refreshTokenValue,
                "Bearer",
                jwtProperties.accessExpiration(),
                new AuthUserResponse(user.getId(), user.getEmail(), user.getName(), user.getRole()));
    }

    public RefreshResponse refresh(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new ApiException("UNAUTHORIZED", "Invalid refresh token", HttpStatus.UNAUTHORIZED));

        if (refreshToken.isRevoked() || refreshToken.isExpired(Instant.now())) {
            throw new ApiException("UNAUTHORIZED", "Expired refresh token", HttpStatus.UNAUTHORIZED);
        }

        refreshToken.revoke();

        User user = refreshToken.getUser();
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                true);

        String newToken = jwtTokenProvider.generateAccessToken(authenticatedUser);
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plusSeconds(jwtProperties.refreshExpiration()))
                .revoked(false)
                .build());

        return new RefreshResponse(newToken, "Bearer", jwtProperties.accessExpiration());
    }

    public LogoutResponse logout(LogoutRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new ApiException("UNAUTHORIZED", "Invalid refresh token", HttpStatus.UNAUTHORIZED));
        refreshToken.revoke();
        return new LogoutResponse("logged out");
    }
}
