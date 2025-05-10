package cit.edu.furrevercare.controller;

   import cit.edu.furrevercare.entity.User;
   import cit.edu.furrevercare.security.JwtUtil;
   import cit.edu.furrevercare.service.UserService;
   import com.google.firebase.auth.FirebaseAuth;
   import com.google.firebase.auth.FirebaseToken;
   import org.springframework.beans.factory.annotation.Autowired;
   import org.springframework.http.ResponseEntity;
   import org.springframework.web.bind.annotation.*;

   @RestController
   @RequestMapping("/api/auth")
   public class AuthController {

       @Autowired
       private UserService userService;

       @Autowired
       private JwtUtil jwtUtil;

       @Autowired
       private FirebaseAuth firebaseAuth;

       @PostMapping("/register")
       public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
           try {
               // Verify Firebase ID token
               FirebaseToken decodedToken = firebaseAuth.verifyIdToken(request.getIdToken());
               String firebaseUid = decodedToken.getUid();
               String email = decodedToken.getEmail();

               // Check if email or firebaseUid already exists
               if (userService.getUserByEmail(email) != null || userService.getUserById(firebaseUid) != null) {
                   return ResponseEntity.badRequest().body("Email or user already exists");
               }

               // Create user in Firestore with empty password
               User user = new User();
               user.setUserID(firebaseUid);
               user.setName(request.getName());
               user.setEmail(email);
               user.setPhone(request.getPhone());
               user.setPassword(""); // Set empty password to avoid null

               userService.saveUser(user);

               // Generate JWT
               String token = jwtUtil.generateToken(firebaseUid);
               return ResponseEntity.ok(new AuthResponse(firebaseUid, token));
           } catch (Exception e) {
               return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
           }
       }

       @PostMapping("/login")
       public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
           try {
               // Verify Firebase ID token
               FirebaseToken decodedToken = firebaseAuth.verifyIdToken(loginRequest.getIdToken());
               String firebaseUid = decodedToken.getUid();
               String email = decodedToken.getEmail();

               // Check if user exists in Firestore
               User user = userService.getUserById(firebaseUid);
               if (user == null) {
                   // Create user if not found
                   user = new User();
                   user.setUserID(firebaseUid);
                   user.setName(decodedToken.getName() != null ? decodedToken.getName() : "Unknown");
                   user.setEmail(email);
                   user.setPhone("");
                   user.setPassword(""); // Set empty password
                   userService.saveUser(user);
               }

               // Generate JWT
               String token = jwtUtil.generateToken(firebaseUid);
               return ResponseEntity.ok(new AuthResponse(firebaseUid, token));
           } catch (Exception e) {
               return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
           }
       }

       @PostMapping("/google-auth")
       public ResponseEntity<?> googleAuth(@RequestBody GoogleAuthRequest request) {
           try {
               // Verify Firebase ID token
               FirebaseToken decodedToken = firebaseAuth.verifyIdToken(request.getIdToken());
               String email = decodedToken.getEmail();
               String name = decodedToken.getName();
               String userID = decodedToken.getUid();

               // Check if user exists, create if not
               User user = userService.getUserById(userID);
               if (user == null) {
                   user = new User();
                   user.setUserID(userID);
                   user.setName(name != null ? name : "Google User");
                   user.setEmail(email);
                   user.setPhone("");
                   user.setPassword(""); // Set empty password
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
           return ResponseEntity.ok("Logged out successfully");
       }
   }

   class RegisterRequest {
       private String name;
       private String phone;
       private String email;
       private String firebaseUid;
       private String idToken;

       public String getName() { return name; }
       public void setName(String name) { this.name = name; }
       public String getPhone() { return phone; }
       public void setPhone(String phone) { this.phone = phone; }
       public String getEmail() { return email; }
       public void setEmail(String email) { this.email = email; }
       public String getFirebaseUid() { return firebaseUid; }
       public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }
       public String getIdToken() { return idToken; }
       public void setIdToken(String idToken) { this.idToken = idToken; }
   }

   class LoginRequest {
       private String email;
       private String idToken;
       private String firebaseUid;

       public String getEmail() { return email; }
       public void setEmail(String email) { this.email = email; }
       public String getIdToken() { return idToken; }
       public void setIdToken(String idToken) { this.idToken = idToken; }
       public String getFirebaseUid() { return firebaseUid; }
       public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }
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