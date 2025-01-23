package org.example.workspace.dto.request;

import org.example.workspace.entity.code.MenuType;

import java.util.Objects;

public record UserMenuReqDto(
        Long id,
        Long contentsId,
        MenuType menuType
) {

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserMenuReqDto that)) return false;
        return menuType == that.menuType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(menuType);
    }
}
