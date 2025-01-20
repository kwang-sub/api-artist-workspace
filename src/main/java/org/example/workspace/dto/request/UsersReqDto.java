package org.example.workspace.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;


@Builder
public record UsersReqDto(
        @NotBlank
        @Pattern(regexp = "^[a-z][a-z0-9]{3,15}$")
        String loginId,

        @NotBlank
        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).{8,16}$")
        String password,

        @NotBlank
        @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).{8,16}$")
        String confirmPassword,

        @NotBlank
        @Size(min = 2, max = 100)
        String userName,

        @NotBlank
        @Size(min = 2, max = 100)
        String nickname,

        @NotBlank
        @Size(min = 2, max = 100)
        String workspaceName,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Pattern(regexp = "^\\d{10,11}$")
        String phoneNumber,

        @Valid
        List<UsersSnsReqDto> userSnsList
) {
}
