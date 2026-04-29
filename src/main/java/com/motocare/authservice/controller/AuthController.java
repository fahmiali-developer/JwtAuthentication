package com.motocare.authservice.controller;

import com.motocare.authservice.dto.ApiResponse;
import com.motocare.authservice.dto.AuthRequest;
import com.motocare.authservice.dto.AuthResponse;
import com.motocare.authservice.dto.Role;
import com.motocare.authservice.security.UserPrincipal;
import com.motocare.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register/user")
    public ApiResponse<?> register(@RequestBody AuthRequest request) {

        String token = authService.register(
                request.getEmail(),
                request.getPassword()
        );

        return new ApiResponse<>(
                "success",
                "User registered successfully",
                new AuthResponse(token)
        );
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody AuthRequest request) {

        String token = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        return new ApiResponse<>(
                "success",
                "Login success",
                new AuthResponse(token)
        );
    }

    @PostMapping("/register/admin")
    public ApiResponse<?> registerAdmin(@RequestBody AuthRequest request) {
        System.out.printf("iniiii "+ request.getEmail());
        String token = authService.registerAdmin(request);

        return new ApiResponse<>(
                "success",
                "Admin registered successfully",
                token
        );
    }

    @PostMapping("/admin/create")
    public ApiResponse<?> createAdmin(
            @RequestBody AuthRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        System.out.printf("Admin created "+user.getRole());
        // 🔐 hanya admin
        if (user.getRole() != Role.ADMIN) {
            System.out.println("kayanya masuk sini");
            throw new RuntimeException("Forbidden");
        }

        authService.createAdminByAdmin(
                request.getEmail(),
                request.getPassword(),
                user.getWorkshopId()
        );

        return new ApiResponse<>(
                "success",
                "Admin created successfully", null
        );
    }
}