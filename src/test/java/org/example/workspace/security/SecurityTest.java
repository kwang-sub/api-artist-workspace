package org.example.workspace.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.example.workspace.dto.request.AuthReqDto;
import org.example.workspace.entity.code.RoleCode;
import org.example.workspace.factory.ObjectFactory;
import org.example.workspace.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(ObjectFactory.class)
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ObjectFactory objectFactory;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService mockCustomUserDetailsService;

    @Test
    @DisplayName("cors 9000번 포트 성공 테스트")
    void cors_9000번_포트_성공_테스트() throws Exception {
        // given
        AuthReqDto authReqDto = new AuthReqDto("user", "1234");
        when(mockCustomUserDetailsService.loadUserByUsername(authReqDto.username()))
                .thenReturn(objectFactory.createRoleUserDetails(authReqDto));

        // when
        mvc.perform(post("/api/v1/login")
                        .content(objectMapper.writeValueAsBytes(authReqDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Origin", "http://localhost:9000")
                        .header("Access-Control-Request-Method", "POST")
                )
        // then
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("cors 임의 포트 실패 테스트")
    void cors_임의_포트_실패_테스트() throws Exception {
        // given
        AuthReqDto authReqDto = new AuthReqDto("user", "1234");
        // when
        mvc.perform(post("/api/v1/login")
                        .content(objectMapper.writeValueAsBytes(authReqDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Origin", "http://localhost:8081")
                        .header("Access-Control-Request-Method", "POST")
                )
        // then
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("발급된 토큰 유효성 확인")
    void 발급된_토큰_유효성_확인() throws Exception {
        // given
        AuthReqDto authReqDto = new AuthReqDto("user", "1234");
        when(mockCustomUserDetailsService.loadUserByUsername(authReqDto.username()))
                .thenReturn(objectFactory.createRoleUserDetails(authReqDto));
        // when
        MvcResult mvcResult = mvc.perform(post("/api/v1/login")
                        .content(objectMapper.writeValueAsBytes(authReqDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        // then
        String responseBody = mvcResult.getResponse().getContentAsString();
        String accessToken = objectMapper.readTree(responseBody).get("accessToken").asText();
        String refreshToken = objectMapper.readTree(responseBody).get("refreshToken").asText();

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(accessToken).isNotNull();
        softAssertions.assertThat(jwtUtil.isTokenExpired(accessToken)).isFalse();
        softAssertions.assertThat(jwtUtil.extractUsername(accessToken)).isEqualTo( authReqDto.username());
        softAssertions.assertThat(jwtUtil.extractRole(accessToken)).isEqualTo(RoleCode.ROLE_ARTIST);
        softAssertions.assertThat(refreshToken).isNotNull();
        softAssertions.assertThat(jwtUtil.isTokenExpired(refreshToken)).isFalse();

        softAssertions.assertAll();
    }
}
