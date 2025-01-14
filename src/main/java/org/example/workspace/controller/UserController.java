package org.example.workspace.controller;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.response.UsersResDto;
import org.example.workspace.security.CustomUserDetails;
import org.example.workspace.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/my")
    public ResponseEntity<UsersResDto> getMyInfo(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(service.getDetail(user.getId()));
    }
}
