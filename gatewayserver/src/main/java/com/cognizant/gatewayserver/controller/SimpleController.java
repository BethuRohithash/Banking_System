package com.cognizant.gatewayserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class SimpleController {
    
    @GetMapping("/admin/dashboard")
    public Mono<String> adminDashboard() {
        return Mono.just("Welcome to Admin Dashboard - Full Bank System Access");
    }

    @GetMapping("/manager/dashboard")
    public Mono<String> managerDashboard() {
        return Mono.just("Welcome to Manager Dashboard - Staff Operations & Customer Management");
    }

    @GetMapping("/user/dashboard")
    public Mono<String> userDashboard() {
        return Mono.just("Welcome to Customer Portal - Self-Service Banking");
    }
    
    @GetMapping("/admin/test")
    public Mono<String> adminTest() {
        return Mono.just("Admin Access: Can view all system data, close accounts, delete customers");
    }

    @GetMapping("/manager/test")
    public Mono<String> managerTest() {
        return Mono.just("Manager Access: Can manage customers, accounts, and cancel transactions");
    }

    @GetMapping("/user/test")
    public Mono<String> userTest() {
        return Mono.just("Customer Access: Can register, open accounts, make transactions, view history");
    }
}
