package org.example.workspace.dto.response;

import java.util.List;

public record UserResDto(
        Long id,
        String loginId,
        String userName,
        String nickname,
        String workspaceName,
        String email,
        String phoneNumber,
        String bio,
        Boolean isActivated,
        List<UserSnsResDto> userSnsList,
        ContentsResDto logo
) {
}