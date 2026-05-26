package com.agri.store.controller;

import com.agri.store.dto.JwtAuthenticationResponse;
import com.agri.store.dto.LoginRequest;
import com.agri.store.dto.RegisterRequest;
import com.agri.store.entity.Store;
import com.agri.store.entity.User;
import com.agri.store.repository.StoreRepository;
import com.agri.store.repository.UserRepository;
import com.agri.store.security.JwtTokenProvider;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
                        UserRepository userRepository,
                        StoreRepository storeRepository,
                        PasswordEncoder passwordEncoder,
                        JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login request for user: {}", loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            JwtAuthenticationResponse response = new JwtAuthenticationResponse(
                    token,
                    "Bearer",
                    user.getUsername(),
                    user.getRole()
            );
            response.setUserId(user.getId());

            // 查询用户店铺状态
            java.util.Optional<Store> storeOpt = storeRepository.findByUserId(user.getId());
            response.setHasStore(storeOpt.isPresent());
            storeOpt.ifPresent(store -> response.setStoreStatus(store.getStatus()));

            logger.info("Login successful for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed for user {}: {}", loginRequest.getUsername(), e.getMessage());
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Register request for user: {}", registerRequest.getUsername());

        try {
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                logger.warn("Username already taken: {}", registerRequest.getUsername());
                return ResponseEntity.badRequest().body("Username is already taken");
            }

            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setNickname(registerRequest.getNickname());
            user.setRole("ROLE_USER");

            logger.debug("Saving user to database: {}", registerRequest.getUsername());
            User savedUser = userRepository.save(user);
            logger.info("User saved successfully with ID: {}", savedUser.getId());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(registerRequest.getUsername(), registerRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = tokenProvider.generateToken(authentication);

            JwtAuthenticationResponse response = new JwtAuthenticationResponse(
                    token,
                    "Bearer",
                    savedUser.getUsername(),
                    savedUser.getRole()
            );
            response.setUserId(savedUser.getId());

            logger.info("Registration successful for user: {}", registerRequest.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Registration failed for user {}: {}", registerRequest.getUsername(), e.getMessage(), e);
            throw e;
        }
    }
}
