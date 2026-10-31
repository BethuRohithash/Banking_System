package com.cognizant.gatewayserver.controller;

import com.cognizant.gatewayserver.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private ReactiveAuthenticationManager authenticationManager;
    @Autowired
    private ReactiveUserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Mono<Map<String, String>> login(@RequestBody Map<String, String> authRequest) {
        String username = authRequest.get("username");
        String password = authRequest.get("password");
        
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password))
            .then(userDetailsService.findByUsername(username))
            .map(userDetails -> {
                String jwt = jwtUtil.generateToken(userDetails);
                String role = userDetails.getAuthorities().iterator().next().getAuthority();
                
                Map<String, String> response = new HashMap<>();
                response.put("token", jwt);
                response.put("role", role);
                response.put("username", username);
                response.put("message", "Authentication successful");
                return response;
            });
    }
    
    // Keep backward compatibility
    @PostMapping("/authenticate")
    public Mono<Map<String, String>> authenticate(@RequestBody Map<String, String> authRequest) {
        return login(authRequest);
    }
    
    @GetMapping("/users")
    public Mono<Map<String, Object>> getAvailableUsers() {
        Map<String, Object> users = new HashMap<>();
        users.put("admin", Map.of("username", "admin", "password", "admin123", "role", "ADMIN", "description", "Bank Administrator"));
        users.put("manager", Map.of("username", "manager", "password", "manager123", "role", "MANAGER", "description", "Bank Manager"));
        users.put("customer", Map.of("username", "customer", "password", "customer123", "role", "USER", "description", "Bank Customer"));
        users.put("john", Map.of("username", "john", "password", "john123", "role", "USER", "description", "Customer John"));
        users.put("sarah", Map.of("username", "sarah", "password", "sarah123", "role", "MANAGER", "description", "Manager Sarah"));
        return Mono.just(users);
    }
}
