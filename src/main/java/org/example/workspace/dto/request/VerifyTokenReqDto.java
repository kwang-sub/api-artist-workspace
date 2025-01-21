package org.example.workspace.dto.request;

import jakarta.validation.constraints.NotBlank;

public record VerifyTokenReqDto(
        @NotBlank
        String token
) {
}
