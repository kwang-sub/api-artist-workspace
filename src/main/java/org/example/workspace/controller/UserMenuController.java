package org.example.workspace.controller;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.request.MenuReqDto;
import org.example.workspace.dto.response.MenuResDto;
import org.example.workspace.security.CustomUserDetails;
import org.example.workspace.service.UserMenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-menus")
@RequiredArgsConstructor
public class UserMenuController {

    private final UserMenuService userMenuService;

    @GetMapping
    public ResponseEntity<MenuResDto> getMenus(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(userMenuService.getUserMenus(user.getId()));
    }

    @PostMapping
    public ResponseEntity<MenuResDto> saveMenus(
            @RequestBody @Validated MenuReqDto dto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        return ResponseEntity.ok(userMenuService.savaAll(user.getId(), dto.menuList()));
    }
}
