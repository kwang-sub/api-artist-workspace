package org.example.workspace.mapper;

import org.example.workspace.dto.response.ContentsResDto;
import org.example.workspace.entity.Contents;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContentsMapper extends BaseMapper<Contents, ContentsResDto> {
}
