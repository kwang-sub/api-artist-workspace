package org.example.workspace.mapper;

import org.example.workspace.dto.response.UserSnsResDto;
import org.example.workspace.entity.UserSns;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface UserSnsMapper extends BaseMapper<UserSns, UserSnsResDto>{
}
