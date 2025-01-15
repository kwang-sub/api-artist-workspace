package org.example.workspace.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record TokenRefreshReqDto(@NotEmpty String refreshToken) {
}
