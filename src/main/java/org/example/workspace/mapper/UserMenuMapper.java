package org.example.workspace.mapper;

import org.example.workspace.dto.response.UserMenuResDto;
import org.example.workspace.entity.UserMenu;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {ContentsMapper.class})
public interface UserMenuMapper extends BaseMapper<UserMenu, UserMenuResDto> {

}
