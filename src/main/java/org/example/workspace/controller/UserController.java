package org.example.workspace.controller;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.request.UserReqDto;
import org.example.workspace.dto.response.UserResDto;
import org.example.workspace.security.CustomUserDetails;
import org.example.workspace.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/my")
    public ResponseEntity<UserResDto> getMyInfo(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(service.getDetail(user.getId()));
    }

    @PostMapping
    public ResponseEntity<UserResDto> createUser(@RequestBody @Validated UserReqDto dto) {
        UserResDto body = service.create(dto);
        // TODO 응답 url 확인 필요
        return ResponseEntity.created(URI.create("d")).body(body);
    }

    @GetMapping("/verify")
    public ResponseEntity<Boolean> verify(@RequestParam String token) {
        return ResponseEntity.ok(service.emailVerify(token));
    }
}
