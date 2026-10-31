package com.cognizant.gatewayserver.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    public SecurityConfig(JwtAuthenticationWebFilter jwtAuthenticationWebFilter) {
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        UserDetailsRepositoryReactiveAuthenticationManager manager = 
            new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        manager.setPasswordEncoder(passwordEncoder);
        return manager;
    }    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf().disable()
            .authorizeExchange(exchanges -> exchanges
                // Public endpoints
                .pathMatchers("/authenticate", "/auth/**").permitAll()
                
                // Role-based test endpoints
                .pathMatchers("/admin/**").hasRole("ADMIN")
                .pathMatchers("/manager/**").hasRole("MANAGER") 
                .pathMatchers("/user/**").hasRole("USER")
                
                // Accounts Service - Banking operations
                .pathMatchers("/accounts/api/create").hasAnyRole("ADMIN", "MANAGER", "USER") // Customers can open accounts
                .pathMatchers("/accounts/api/update/**").hasAnyRole("ADMIN", "MANAGER") // Staff can update accounts
                .pathMatchers("/accounts/api/status/**").hasAnyRole("ADMIN", "MANAGER") // Staff can change status
                .pathMatchers("/accounts/api/close/**").hasRole("ADMIN") // Only admin can close accounts
                .pathMatchers("/accounts/api/fetch/all").hasAnyRole("ADMIN", "MANAGER") // Staff can view all accounts
                .pathMatchers("/accounts/api/**").hasAnyRole("ADMIN", "MANAGER", "USER") // All can view individual accounts
                
                // Customers Service - Customer management
                .pathMatchers("/customers/api/create").hasAnyRole("ADMIN", "MANAGER", "USER") // Self-registration allowed
                .pathMatchers("/customers/api/update/**").hasAnyRole("ADMIN", "MANAGER", "USER") // Can update own profile
                .pathMatchers("/customers/api/delete/**").hasRole("ADMIN") // Only admin can delete customers
                .pathMatchers("/customers/api/fetch/all").hasAnyRole("ADMIN", "MANAGER") // Staff can view all customers
                .pathMatchers("/customers/api/**").hasAnyRole("ADMIN", "MANAGER", "USER") // All can view individual profiles
                
                // Transactions Service - Banking transactions
                .pathMatchers("/transactions/api/create").hasAnyRole("ADMIN", "MANAGER", "USER") // All can make transactions
                .pathMatchers("/transactions/api/update/**").hasAnyRole("ADMIN", "MANAGER") // Staff can update transactions
                .pathMatchers("/transactions/api/cancel/**").hasAnyRole("ADMIN", "MANAGER") // Staff can cancel transactions
                .pathMatchers("/transactions/api/**").hasAnyRole("ADMIN", "MANAGER", "USER") // All can view transaction details
                
                .anyExchange().authenticated()
            )
            .addFilterBefore(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}
