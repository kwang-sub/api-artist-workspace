package org.example.workspace.dto.response;

import java.util.List;

public record MenuResDto(
        List<UserMenuResDto> menuList
) {
}
