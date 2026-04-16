package com.fuzentrix.backend.service;

import com.fuzentrix.backend.dto.AuthDto;
import com.fuzentrix.backend.entity.Role;
import com.fuzentrix.backend.entity.User;
import com.fuzentrix.backend.entity.UserSession;
import com.fuzentrix.backend.exception.ConflictException;
import com.fuzentrix.backend.exception.UnauthorizedException;
import com.fuzentrix.backend.repository.RoleRepository;
import com.fuzentrix.backend.repository.UserRepository;
import com.fuzentrix.backend.repository.UserSessionRepository;
import com.fuzentrix.backend.security.CustomUserDetails;
import com.fuzentrix.backend.security.JwtService;
import com.fuzentrix.backend.security.TokenHashUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserSessionRepository sessionRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;

    @Value("${app.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Value("${app.security.cookie.secure:false}")
    private boolean cookieSecure;

    @Transactional
    public void login(AuthDto.LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Clean up expired/revoked sessions for this user before creating a new one
        sessionRepository.deleteExpiredOrRevokedSessionsForUser(user.getId(), OffsetDateTime.now());

        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Store the hash of the refresh token — never persist the raw JWT
        UserSession session = UserSession.builder()
                .user(user)
                .refreshTokenHash(TokenHashUtil.hash(refreshToken))
                .deviceInfo(request.getDeviceInfo() != null ? request.getDeviceInfo() : httpRequest.getHeader(HttpHeaders.USER_AGENT))
                .ipAddress(httpRequest.getRemoteAddr())
                .expiresAt(OffsetDateTime.now().plus(Duration.ofMillis(refreshExpiration)))
                .isRevoked(false)
                .build();
        sessionRepository.save(session);

        setTokenCookies(httpResponse, accessToken, refreshToken);
    }

    @Transactional
    public void register(AuthDto.RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("Email is already registered");
        }

        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new UnauthorizedException("Default STUDENT role not found"));

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();

        user.getRoles().add(studentRole);

        userRepository.save(user);
    }

    @Transactional
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookies(request);

        if (refreshToken == null) {
            throw new UnauthorizedException("Refresh token missing");
        }

        // Look up the session by hashed token
        UserSession session = sessionRepository.findByRefreshTokenHash(TokenHashUtil.hash(refreshToken))
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (session.getIsRevoked() || session.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new UnauthorizedException("Refresh token expired or revoked");
        }

        String userEmail = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        CustomUserDetails userDetails = new CustomUserDetails(user);

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        // Revoke the old session — refresh token rotation
        session.setIsRevoked(true);
        sessionRepository.save(session);

        // Issue new access + refresh tokens and create a new session
        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        UserSession newSession = UserSession.builder()
                .user(user)
                .refreshTokenHash(TokenHashUtil.hash(newRefreshToken))
                .deviceInfo(session.getDeviceInfo())
                .ipAddress(session.getIpAddress())
                .expiresAt(OffsetDateTime.now().plus(Duration.ofMillis(refreshExpiration)))
                .isRevoked(false)
                .build();
        sessionRepository.save(newSession);

        setTokenCookies(response, newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshTokenFromCookies(request);

        if (refreshToken != null) {
            Optional<UserSession> session = sessionRepository.findByRefreshTokenHash(TokenHashUtil.hash(refreshToken));
            session.ifPresent(s -> {
                s.setIsRevoked(true);
                sessionRepository.save(s);
            });
        }

        // Clear cookies regardless of whether a session was found
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true).secure(cookieSecure)
                .path("/").maxAge(0).sameSite("Lax").build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true).secure(cookieSecure)
                .path("/").maxAge(0).sameSite("Lax").build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    // ─── Private Helpers ────────────────────────────────────────────────────────

    private void setTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(jwtExpiration / 1000)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(refreshExpiration / 1000)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private String extractRefreshTokenFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
