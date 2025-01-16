package org.example.workspace.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersReqDto {
    @NotBlank
    @Pattern(regexp = "^[a-z][a-z0-9]{3,15}$")
    private String loginId;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).{8,16}$")
    private String password;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[\\W_]).{8,16}$")
    private String confirmPassword;

    @NotBlank
    @Size(min = 2, max = 100)
    private String userName;

    @NotBlank
    @Size(min = 2, max = 100)
    private String nickname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\d{10,11}$")
    private String phoneNumber;
}
