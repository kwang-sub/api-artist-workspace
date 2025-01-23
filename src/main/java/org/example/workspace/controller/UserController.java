package org.example.workspace.controller;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.request.*;
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
    public ResponseEntity<UserResDto> createUser(@RequestBody @Validated UserCreateReqDto dto) {
        UserResDto body = service.create(dto);
        // TODO 응답 url 확인 필요
        return ResponseEntity.created(URI.create("d")).body(body);
    }

    @PatchMapping
    public ResponseEntity<UserResDto> updateUser(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Validated UserUpdateReqDto dto
    ) {
        return ResponseEntity.ok(service.update(user.getId(), dto));
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verify(@RequestBody @Validated VerifyTokenReqDto dto) {

        return ResponseEntity.ok(service.emailVerify(dto.token()));
    }

    @PostMapping("/recover")
    public ResponseEntity<Boolean> recoveryUser(@RequestBody @Validated UserRecoveryReqDto dto) {
        return ResponseEntity.ok(service.recovery(dto.email()));
    }

    @PatchMapping("/password")
    public ResponseEntity<Boolean> updatePassword(@RequestBody @Validated UserPasswordReqDto dto) {
        return ResponseEntity.ok(service.updatePassword(dto));
    }

    @GetMapping("/duplicate")
    public ResponseEntity<Boolean> checkDuplicate(@Validated UserDuplicateReqDto dto) {
        return ResponseEntity.ok(service.getDuplicateWhether(dto));
    }
}
