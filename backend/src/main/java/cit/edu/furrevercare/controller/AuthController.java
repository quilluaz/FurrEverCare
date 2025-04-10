package cit.edu.furrevercare.controller;

import cit.edu.furrevercare.entity.User;
import cit.edu.furrevercare.security.JwtUtil;
import cit.edu.furrevercare.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) throws ExecutionException, InterruptedException {
        // Check if user already exists
        try {
            if (userService.getUserByEmail(user.getEmail()) != null) {
                return ResponseEntity.badRequest().body("Email already in use");
            }
        } catch (Exception e) {
            // Continue if user not found (expected behavior)
        }

        // Set user ID if not already set
        if (user.getUserID() == null || user.getUserID().trim().isEmpty()) {
            user.setUserID(UUID.randomUUID().toString());
        }

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user
        userService.saveUser(user);

        // Generate token
        String token = jwtUtil.generateToken(user.getUserID());

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserID());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws ExecutionException, InterruptedException {
        User user = userService.getUserByEmail(loginRequest.getEmail());

        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getUserID());

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserID());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}