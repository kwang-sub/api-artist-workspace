package org.example.workspace.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRecoveryReqDto(
        @Email
        @NotBlank
        String email
) {
}
