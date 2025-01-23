package org.example.workspace.service;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.response.ContentsResDto;
import org.example.workspace.entity.Contents;
import org.example.workspace.exception.EntityNotFoundException;
import org.example.workspace.exception.FileUploadException;
import org.example.workspace.mapper.ContentsMapper;
import org.example.workspace.repository.ContentsRepository;
import org.example.workspace.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

@Service
@Transactional
@RequiredArgsConstructor
public class ContentsService {

    private final FileUtil fileUtil;
    private final ContentsRepository contentsRepository;
    private final ContentsMapper contentsMapper;

    @Value("${app.file.upload.path}")
    private String uploadPath;

    public ContentsResDto saveFile(MultipartFile file) {
        Path savePath;
        try {
            savePath = fileUtil.saveFile(uploadPath, file);
        } catch (IOException e) {
            throw new FileUploadException();
        }
        if (savePath == null)
            throw new FileUploadException();

        Contents contents = Contents.create(
                savePath.getFileName().toString(),
                file.getName(),
                savePath.getParent().toString(),
                file.getSize(),
                file.getContentType()
        );
        contentsRepository.save(contents);

        return getDetail(contents.getId());
    }

    public ContentsResDto getDetail(Long id) {
        Contents contents = contentsRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(Contents.class, id));

        return contentsMapper.toDto(contents);

    }

    public Contents getEntity(Long id) {
        return contentsRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(Contents.class, id));
    }
}
