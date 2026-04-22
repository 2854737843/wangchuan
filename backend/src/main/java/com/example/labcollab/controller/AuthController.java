package com.example.labcollab.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.labcollab.common.ApiResponse;
import com.example.labcollab.entity.SysUser;
import com.example.labcollab.exception.UnauthorizedException;
import com.example.labcollab.mapper.SysUserMapper;
import com.example.labcollab.security.JwtService;
import com.example.labcollab.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(SysUserMapper sysUserMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.sysUserMapper = sysUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, request.username()));
        if (user == null || !Boolean.TRUE.equals(user.getEnabled())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        if (!matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername());
        return ApiResponse.ok(Map.of(
                "token", token,
                "user", Map.of("id", user.getId(), "username", user.getUsername())
        ));
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> me(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return ApiResponse.ok(Map.of("id", principal.userId(), "username", principal.username()));
    }

    private boolean matches(String rawPassword, String savedHash) {
        if (savedHash == null) {
            return false;
        }
        if (savedHash.startsWith("{noop}")) {
            return savedHash.substring(6).equals(rawPassword);
        }
        return passwordEncoder.matches(rawPassword, savedHash);
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }
}
