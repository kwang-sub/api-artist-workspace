package org.example.workspace.integration.domain.contents;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.workspace.dto.response.ContentsResDto;
import org.example.workspace.entity.Contents;
import org.example.workspace.factory.ObjectFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ComponentScan(basePackages = "org.example.workspace")
@AutoConfigureMockMvc
public class ContentsBasicTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectFactory objectFactory;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${app.file.upload.path}")
    private String uploadPath;

    @Test
    @WithMockUser
    void 파일저장_할수있다() throws Exception {
        // given
        MockMultipartFile file = objectFactory.createMultipartFile("hello.jpg", MediaType.IMAGE_JPEG);
        // when
        MvcResult mvcResult = mvc.perform(multipart("/api/v1/contents").file(file))
                .andExpect(status().isOk())
                .andReturn();
        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        ContentsResDto response = objectMapper.readValue(responseString, ContentsResDto.class);

        Path savedFilePath = Paths.get(response.contentsPath()).resolve(response.contentsName());
        assertThat(Files.exists(savedFilePath)).isTrue();
        assertThat(Files.deleteIfExists(savedFilePath)).isTrue();

        Path parentDir = savedFilePath.getParent();
        while (parentDir != null && Files.exists(parentDir)) {
            assertThat(Files.deleteIfExists(parentDir)).isTrue();
            parentDir = parentDir.getParent();
        }
    }

    @Test
    @WithMockUser
    void 저장된_파일정보를_제공한다() throws Exception {
        // given
        Contents content = objectFactory.createContentEntity();
        // when
        MvcResult mvcResult = mvc.perform(get("/api/v1/contents/"+content.getId()))
                .andExpect(status().isOk())
                .andReturn();
        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        ContentsResDto response = objectMapper.readValue(responseString, ContentsResDto.class);

        assertThat(response.getClass().getRecordComponents().length).isEqualTo(6);
        assertThat(response.id()).isEqualTo(content.getId());
        assertThat(response.contentsName()).isEqualTo(content.getContentsName());
        assertThat(response.contentsOgName()).isEqualTo(content.getContentsOgName());
        assertThat(response.contentsPath()).isEqualTo(content.getContentsPath());
        assertThat(response.contentsSize()).isEqualTo(content.getContentsSize());
        assertThat(response.contentsType()).isEqualTo(content.getContentsType());
    }

    @Test
    @WithMockUser
    void 저장되지_않은_파일정보_조회시_예외발생() throws Exception {
        // given
        // when
        MvcResult mvcResult = mvc.perform(get("/api/v1/contents/0"))
                .andExpect(status().isBadRequest())
                .andReturn();
        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);

        assertThat(response.getDetail()).isEqualTo("엔티티 정보를 찾을 수 없습니다. entity: [Contents] identifier [0]");
    }
}
