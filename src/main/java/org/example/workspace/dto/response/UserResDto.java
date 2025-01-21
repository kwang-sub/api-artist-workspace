package org.example.workspace.dto.response;

import java.util.List;

public record UserResDto(
        Long id,
        String loginId,
        String userName,
        String nickname,
        String email,
        String phoneNumber,
        Boolean isActivated,
        List<UserSnsResDto> userSnsList
) {
}