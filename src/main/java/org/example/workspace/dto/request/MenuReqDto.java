package org.example.workspace.dto.request;

import java.util.Set;

public record MenuReqDto(
        Set<UserMenuReqDto> menuList
) {

}
