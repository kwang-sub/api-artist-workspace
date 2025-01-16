package org.example.workspace.mapper;

import org.example.workspace.dto.response.UsersResDto;
import org.example.workspace.entity.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<Users, UsersResDto> {
}
