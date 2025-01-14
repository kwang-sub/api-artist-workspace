package org.example.workspace.controller;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.request.AuthReqDto;
import org.example.workspace.dto.response.AuthTokenResDto;
import org.example.workspace.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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
        if (!(principal instanceof UserDetails))
            throw new AuthenticationCredentialsNotFoundException(authReqDto.username());

        return ResponseEntity.ok(jwtUtil.generateToken((UserDetails) principal));
    }
}
