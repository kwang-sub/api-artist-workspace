package org.example.workspace.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UserRecoveryReqDto(
        @Email
        @NotNull
        String email
) {
}
