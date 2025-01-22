package org.example.workspace.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDuplicateReqDto(
        @NotBlank
        String value,
        @NotNull
        Type type
) {
    public enum Type {
        EMAIL,
        LOGIN_ID,
        WORKSPACE_NAME
    }
}
