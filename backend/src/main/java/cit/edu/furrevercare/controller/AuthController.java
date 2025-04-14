package cit.edu.furrevercare.controller;

import cit.edu.furrevercare.entity.User;
import cit.edu.furrevercare.security.JwtUtil;
import cit.edu.furrevercare.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FirebaseAuth firebaseAuth;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) throws ExecutionException, InterruptedException {
        // Check if email already exists
        if (userService.getUserByEmail(user.getEmail()) != null) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // Generate userID if not provided
        if (user.getUserID() == null || user.getUserID().trim().isEmpty()) {
            user.setUserID(userService.firestore.collection("users").document().getId());
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save user
        userService.saveUser(user);

        // Generate JWT
        String token = jwtUtil.generateToken(user.getUserID());
        return ResponseEntity.ok(new AuthResponse(user.getUserID(), token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws ExecutionException, InterruptedException {
        User user = userService.getUserByEmail(loginRequest.getEmail());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getUserID());
        return ResponseEntity.ok(new AuthResponse(user.getUserID(), token));
    }

    @PostMapping("/google-auth")
    public ResponseEntity<?> googleAuth(@RequestBody GoogleAuthRequest request) throws Exception {
        try {
            // Verify Firebase ID token
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(request.getIdToken());
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String userID = decodedToken.getUid();

            // Check if user exists, create if not
            User user = userService.getUserById(userID);
            if (user == null) {
                user = new User(
                        userID,
                        name != null ? name : "Google User",
                        email,
                        "", // No password for Google users
                        ""  // Empty phone for Google users
                );
                userService.saveUser(user);
            }

            // Generate JWT
            String token = jwtUtil.generateToken(userID);
            return ResponseEntity.ok(new GoogleAuthResponse(token, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Google authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // Stateless JWT setup, logout handled client-side
        return ResponseEntity.ok("Logged out successfully");
    }
}

// Helper classes
class LoginRequest {
    private String email;
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class GoogleAuthRequest {
    private String idToken;

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
}

class AuthResponse {
    private String userId;
    private String token;

    public AuthResponse(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}

class GoogleAuthResponse {
    private String token;
    private User user;

    public GoogleAuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}