package org.example.workspace.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.example.workspace.dto.request.AuthReqDto;
import org.example.workspace.dto.request.TokenRefreshReqDto;
import org.example.workspace.entity.code.RoleType;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
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
    @DisplayName("지정된포트는cors에러를발생하지않는다")
    void 지정된_포트는_cors_에러를_발생하지_않는다() throws Exception {
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
        verify(mockCustomUserDetailsService, times(1)).loadUserByUsername(authReqDto.username());
        verifyNoMoreInteractions(mockCustomUserDetailsService);
    }

    @Test
    @DisplayName("지정되지않은포트는cors에러가발생한다")
    void 지정되지_않은_포트는_cors_에러가_발생한다() throws Exception {
        // given
        AuthReqDto authReqDto = new AuthReqDto("user", "1234");

        // when
        MvcResult mvcResult = mvc.perform(post("/api/v1/login")
                        .content(objectMapper.writeValueAsBytes(authReqDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Origin", "http://localhost:8081")
                        .header("Access-Control-Request-Method", "POST")
                )
                .andExpect(status().isForbidden())
                .andReturn();

                // then
        String result = mvcResult.getResponse().getContentAsString();
        assertThat(result).isEqualTo("Invalid CORS request");
    }

    @Test
    @DisplayName("로그인성공시인증토큰이발급된다")
    void 로그인_성공시_인증토큰이_발급된다() throws Exception {
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
        softAssertions.assertThat(jwtUtil.extractUsername(accessToken)).isEqualTo(authReqDto.username());
        softAssertions.assertThat(jwtUtil.extractRole(accessToken)).isEqualTo(RoleType.ROLE_ARTIST);
        softAssertions.assertThat(refreshToken).isNotNull();
        softAssertions.assertThat(jwtUtil.isTokenExpired(refreshToken)).isFalse();

        softAssertions.assertAll();
    }

    @Test
    @DisplayName("리프레쉬토큰으로토큰재발급이가능하다")
    void 리프레쉬토큰으로_토큰_재발급이_가능하다() throws Exception {
        // given
        final String username = "user";
        final RoleType roleType = RoleType.ROLE_ARTIST;
        String refreshToken = jwtUtil.generateToken(username, roleType).refreshToken();
        TokenRefreshReqDto tokenRefreshReqDto = new TokenRefreshReqDto(refreshToken);

        // when
        MvcResult mvcResult = mvc.perform(post("/api/v1/login-refresh")
                        .content(objectMapper.writeValueAsBytes(tokenRefreshReqDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        // then
        String responseBody = mvcResult.getResponse().getContentAsString();
        String newAccessToken = objectMapper.readTree(responseBody).get("accessToken").asText();
        String newRefreshToken = objectMapper.readTree(responseBody).get("refreshToken").asText();

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(newAccessToken).isNotNull();
        softAssertions.assertThat(jwtUtil.isTokenExpired(newAccessToken)).isFalse();
        softAssertions.assertThat(jwtUtil.extractUsername(newAccessToken)).isEqualTo(username);
        softAssertions.assertThat(jwtUtil.extractRole(newAccessToken)).isEqualTo(roleType);
        softAssertions.assertThat(newRefreshToken).isNotNull();
        softAssertions.assertThat(jwtUtil.isTokenExpired(newRefreshToken)).isFalse();
    }
}
