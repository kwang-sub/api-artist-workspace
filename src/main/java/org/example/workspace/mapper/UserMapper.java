package org.example.workspace.mapper;

import org.example.workspace.dto.response.UserResDto;
import org.example.workspace.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserSnsMapper.class, ContentsMapper.class})
public interface UserMapper extends BaseMapper<User, UserResDto> {
}
