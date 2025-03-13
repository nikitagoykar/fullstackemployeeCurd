package com.spring.fullstackbackend.service;

import com.spring.fullstackbackend.model.User;
import com.spring.fullstackbackend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);  // Find by email instead of username
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        return new UserDetailsImpl(user.get());  // Return the custom UserDetailsImpl instead of UserDetailsServiceImpl
    }
}
