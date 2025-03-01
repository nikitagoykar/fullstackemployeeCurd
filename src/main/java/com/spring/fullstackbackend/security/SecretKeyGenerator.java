package com.spring.fullstackbackend.security;

import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Base64;

public class SecretKeyGenerator {
    public static String generateSecretKey() {
        Key key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        // Use URL-safe Base64 encoding
        return Base64.getUrlEncoder().encodeToString(key.getEncoded());
    }
}
