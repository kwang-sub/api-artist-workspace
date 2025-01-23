package org.example.workspace.dto.response;

import org.example.workspace.entity.code.MenuType;

public record UserMenuResDto(
        Long id,
        ContentsResDto contents,
        MenuType menuType
) {
}
