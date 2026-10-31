package com.cognizant.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserDetailsConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return new MapReactiveUserDetailsService(
            // Bank Administrator - Full system access
            User.withUsername("admin").password(passwordEncoder().encode("admin123")).roles("ADMIN").build(),
            
            // Bank Manager - Staff operations and management
            User.withUsername("manager").password(passwordEncoder().encode("manager123")).roles("MANAGER").build(),
            
            // Bank Customer - Self-service banking operations
            User.withUsername("customer").password(passwordEncoder().encode("customer123")).roles("USER").build(),
            
            // Additional test users
            User.withUsername("john").password(passwordEncoder().encode("john123")).roles("USER").build(),
            User.withUsername("sarah").password(passwordEncoder().encode("sarah123")).roles("MANAGER").build()
        );
    }
}
