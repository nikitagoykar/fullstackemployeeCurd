package com.spring.fullstackbackend.controller;

import com.spring.fullstackbackend.dto.JwtRequest;
import com.spring.fullstackbackend.dto.JwtResponse;
import com.spring.fullstackbackend.security.JwtUtil;
import com.spring.fullstackbackend.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody JwtRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_", "")) // Remove "ROLE_" prefix
                .toList();

        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());

        return new JwtResponse(accessToken, refreshToken);
    }


    @PostMapping("/refresh")
    public Map<String, String> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String username = jwtUtil.extractUsername(refreshToken);

        if (jwtUtil.isTokenValid(refreshToken, username)) {
            String newAccessToken = jwtUtil.generateAccessToken(username, List.of());

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            return response;
        } else {
            throw new RuntimeException("Invalid Refresh Token");
        }
    }
}
