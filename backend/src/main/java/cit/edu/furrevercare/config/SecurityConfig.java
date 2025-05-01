package cit.edu.furrevercare.config;

import cit.edu.furrevercare.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Apply CORS configuration from the bean below
                .csrf(csrf -> csrf.disable()) // Disable CSRF (common for stateless APIs)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Allow unauthenticated access to all /api/auth/ paths
                        // .requestMatchers("/api/auth/login").permitAll() // Redundant due to the line above
                        // .requestMatchers("/api/auth/register").permitAll() // Redundant due to the line above
                        .anyRequest().authenticated() // Require authentication for all other requests
                )
                // Add the custom JWT filter before the standard username/password auth filter
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // *** CHANGE HERE: Allow both localhost and your Vercel deployment ***
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",             // For local development
                "https://furr-ever-care.vercel.app"  // For your deployed frontend
        ));

        // Specify allowed HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Specify allowed headers (second call overrides the first commented one)
        // configuration.setAllowedHeaders(Collections.singletonList("*")); // Allow all headers (less secure)
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", // For JWT token
                "Content-Type",  // For request body type (e.g., application/json)
                "Origin"         // Standard header sent by browsers
                // Add any other custom headers your frontend might send
        ));

        // Specify headers the frontend JavaScript can access from the response
        configuration.setExposedHeaders(Collections.singletonList("Authorization")); // e.g., if you refresh token via headers

        // Allow credentials (like cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // How long the results of a preflight request (OPTIONS) can be cached
        configuration.setMaxAge(3600L); // 1 hour

        // Apply this configuration to all paths under /api/
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Use BCrypt for password hashing
        return new BCryptPasswordEncoder();
    }
}