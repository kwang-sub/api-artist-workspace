package org.example.workspace.dto.response;

public record UsersResDto(
        Long id,
        String loginId,
        String userName,
        String email
) {
}