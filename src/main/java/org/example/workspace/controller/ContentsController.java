package org.example.workspace.controller;

import lombok.RequiredArgsConstructor;
import org.example.workspace.dto.response.ContentsResDto;
import org.example.workspace.service.ContentsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/contents")
@RequiredArgsConstructor
public class ContentsController {

    private final ContentsService contentsService;

    @GetMapping("/{id}")
    public ResponseEntity<ContentsResDto> getContents(@PathVariable long id) {
        ContentsResDto result = contentsService.getDetail(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ContentsResDto> addContent(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(contentsService.saveFile(file));
    }
}
