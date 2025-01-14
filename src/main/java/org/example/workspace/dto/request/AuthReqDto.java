package org.example.workspace.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record AuthReqDto(@NotEmpty String username, @NotEmpty String password) {
}
