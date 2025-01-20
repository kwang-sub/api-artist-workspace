package org.example.workspace.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import org.example.workspace.entity.code.SnsType;

@Builder
public record UsersSnsReqDto(
        @NotNull
        SnsType snsType,
        @NotBlank
        String snsUsername
) {
}
