package org.example.workspace.controller;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.request.AuthReqDto;
import org.example.workspace.dto.request.TokenRefreshReqDto;
import org.example.workspace.dto.response.AuthTokenResDto;
import org.example.workspace.entity.code.RoleType;
import org.example.workspace.security.CustomUserDetails;
import org.example.workspace.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResDto> login(@RequestBody @Validated AuthReqDto authReqDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authReqDto.username(), authReqDto.password())
        );

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof CustomUserDetails))
            throw new AuthenticationCredentialsNotFoundException(authReqDto.username());
        CustomUserDetails customUserDetails = (CustomUserDetails) principal;

        return ResponseEntity.ok(jwtUtil.generateSignInToken(customUserDetails.getUsername(), customUserDetails.getRoleType()));
    }

    @PostMapping("/login-refresh")
    public ResponseEntity<AuthTokenResDto> loginRefresh(@RequestBody @Validated TokenRefreshReqDto dto) {
        String refreshToken = dto.refreshToken();

        String username = jwtUtil.extractSubject(refreshToken, JwtUtil.TokenType.REFRESH);
        RoleType roleType = jwtUtil.extractRole(refreshToken);
        if (!jwtUtil.validateToken(refreshToken, username, JwtUtil.TokenType.REFRESH))
            throw new AccessDeniedException("Invalid refresh token");

        AuthTokenResDto authTokenResDto = jwtUtil.generateSignInToken(username, roleType);

        return ResponseEntity.ok(authTokenResDto);
    }

}
