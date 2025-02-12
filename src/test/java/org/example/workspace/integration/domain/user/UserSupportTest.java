package org.example.workspace.integration.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.workspace.entity.User;
import org.example.workspace.factory.ObjectFactory;
import org.example.workspace.factory.RequestParameterFactory;
import org.example.workspace.util.JwtUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ComponentScan(basePackages = "org.example.workspace")
@Transactional
@AutoConfigureMockMvc
public class UserSupportTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ObjectFactory objectFactory;
    @Autowired
    private RequestParameterFactory requestParameterFactory;
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void 사용자_인증성공시_활성화된다() throws Exception {
        // given
        User user = objectFactory.createUsersEntity();
        assertThat(user.getIsActivated()).isFalse();
        String token = jwtUtil.generateEmailVerifyToken(user.getEmail(), user.getId());

        // when
        mvc.perform(get("/api/v1/users/verify").param("token", token))
                .andExpect(status().isOk());
        
        // then
        assertThat(user.getIsActivated()).isTrue();
    }

    @Test
    void 사용자_인증시_올바르지_않은_토큰은_예외발생한다() throws Exception {
        // given
        User user = objectFactory.createUsersEntity();
        assertThat(user.getIsActivated()).isFalse();
        String token = jwtUtil.generateSignInToken(user.getUserName(), user.getRole().getRoleType())
                .accessToken();
        // when
        MvcResult mvcResult = mvc.perform(get("/api/v1/users/verify").param("token", token))
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);

        assertThat(response.getDetail()).isEqualTo("유효하지 않은 토큰 정보입니다.");
    }

    @Test
    @Disabled
    void 사용자는_이메일로_마스킹된_아이디를_찾을수있다() {
        fail();
        // given

        // when

        // then
    }

    @Test
    @Disabled
    void 사용자는_로그인아이디_이메일로_임시_비밀번호를_이메일로_발송할수있다() {
        fail();
        // given

        // when

        // then
    }

    @Test
    @Disabled
    void 사용자_비밀번호_찾기시_로그인아이디_이메일에_맞는_계정_없는_경우_예외발생한다() {
        fail();
        // given

        // when

        // then
    }
}
