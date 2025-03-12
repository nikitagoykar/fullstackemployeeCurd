package com.spring.fullstackbackend.controller;

import com.spring.fullstackbackend.model.User;
import com.spring.fullstackbackend.repository.UserRepository;
import com.spring.fullstackbackend.security.JwtUtil;
import com.spring.fullstackbackend.service.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserRepository userRepository, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User loginRequest, HttpServletResponse response) {
        try {
            // Authenticate the user with email and password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Generate access token (short-lived)
            UserDetailsImpl userDetails = new UserDetailsImpl(user);
            String accessToken = jwtUtil.generateToken(userDetails, 60);  // 60-minute expiration for access token

            // Generate refresh token (longer-lived)
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);  // Longer expiration for refresh token

            // Set access token as HTTP-only cookie
            ResponseCookie accessTokenCookie = ResponseCookie.from("token", accessToken)
                    .httpOnly(true)
                    .secure(false) // Set to true for production with HTTPS
                    .path("/")
                    .maxAge(24 * 60 * 60) // 1-day expiration
                    .sameSite("Strict")
                    .build();

            // Set refresh token as HTTP-only cookie
            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(false) // Set to true for production with HTTPS
                    .path("/")
                    .maxAge(30 * 24 * 60 * 60) // 30-day expiration for refresh token
                    .sameSite("Strict")
                    .build();

            // Add cookies to response
            response.addHeader("Set-Cookie", accessTokenCookie.toString());
            response.addHeader("Set-Cookie", refreshTokenCookie.toString());

            // Send tokens and role in the response body
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("accessToken", accessToken);
            responseBody.put("refreshToken", refreshToken);
            responseBody.put("role", user.getRole().name());

            return ResponseEntity.ok(responseBody);
        } catch (BadCredentialsException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Invalid email or password");
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> getUserRole(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            log.warn("Unauthorized access attempt.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "User not authenticated"));
        }

        Optional<User> optionalUser = userRepository.findByUsername(userDetails.getUsername());
        if (optionalUser.isEmpty()) {
            log.warn("User not found in database: {}", userDetails.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        User user = optionalUser.get();
        return ResponseEntity.ok(Map.of("role", user.getRole().name()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        // Clear the JWT cookies
        ResponseCookie accessTokenCookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // Expire immediately
                .sameSite("Strict")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // Expire immediately
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh token is missing"));
        }

        try {
            String username = jwtUtil.extractUsername(refreshToken);
            if (!jwtUtil.isRefreshTokenValid(refreshToken, username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
            }

            // Load UserDetails
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Generate a new Access Token
            String newAccessToken = jwtUtil.generateToken(userDetails, 60); // 1 hour expiration

            // Return the new access token in the cookie
            ResponseCookie jwtCookie = ResponseCookie.from("token", newAccessToken)
                    .httpOnly(true)
                    .secure(false) // Change to true for HTTPS
                    .path("/")
                    .maxAge(60 * 60) // 1 hour expiration
                    .sameSite("Strict")
                    .build();
            response.addHeader("Set-Cookie", jwtCookie.toString());

            return ResponseEntity.ok(Map.of("role", userDetails.getAuthorities().iterator().next().getAuthority()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or expired refresh token"));
        }
    }
}
