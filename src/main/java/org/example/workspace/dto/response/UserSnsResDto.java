package org.example.workspace.dto.response;

import org.example.workspace.entity.code.SnsType;

public record UserSnsResDto(
        SnsType snsType,
        String snsUsername
) {
}
