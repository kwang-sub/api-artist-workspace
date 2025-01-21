package org.example.workspace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserPasswordReqDto(
        @NotBlank
        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).{8,16}$")
        String password,

        @NotBlank
        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).{8,16}$")
        String confirmPassword,

        @NotBlank
        String token
) {
}
