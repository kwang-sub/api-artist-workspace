package org.example.workspace.dto.response;

public record ContentsResDto(
        Long id,
        String contentsName,
        String contentsOgName,
        String contentsPath,
        Long contentsSize,
        String contentsType
) {
}
