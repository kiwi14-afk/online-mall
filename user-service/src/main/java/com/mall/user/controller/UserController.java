package com.mall.user.controller;

import com.mall.user.common.ApiResult;
import com.mall.user.dto.LoginRequest;
import com.mall.user.dto.LoginResponse;
import com.mall.user.dto.RegisterRequest;
import com.mall.user.dto.UserResponse;
import com.mall.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResult<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = userService.register(request);
            return ApiResult.success("注册成功", response);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.login(request);
            return ApiResult.success("登录成功", response);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ApiResult<UserResponse> getProfile(@RequestHeader("X-User-Id") Long userId) {
        try {
            UserResponse profile = userService.getUserProfile(userId);
            return ApiResult.success(profile);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ApiResult<UserResponse> updateProfile(@RequestHeader("X-User-Id") Long userId,
                                                  @RequestBody UserResponse request) {
        try {
            UserResponse updated = userService.updateProfile(userId, request);
            return ApiResult.success("更新成功", updated);
        } catch (RuntimeException e) {
            return ApiResult.error(e.getMessage());
        }
    }

    @GetMapping("/validate/{userId}")
    public ApiResult<Boolean> validateUser(@PathVariable Long userId) {
        boolean exists = userService.validateUser(userId);
        return ApiResult.success(exists);
    }
}
