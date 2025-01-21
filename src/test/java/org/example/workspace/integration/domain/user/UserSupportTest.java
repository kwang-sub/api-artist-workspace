package org.example.workspace.integration.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.example.workspace.dto.request.UserPasswordReqDto;
import org.example.workspace.dto.request.UserRecoveryReqDto;
import org.example.workspace.dto.request.VerifyTokenReqDto;
import org.example.workspace.entity.User;
import org.example.workspace.entity.UserVerification;
import org.example.workspace.factory.ObjectFactory;
import org.example.workspace.factory.RequestParameterFactory;
import org.example.workspace.repository.UserVerificationRepository;
import org.example.workspace.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserVerificationRepository userVerificationRepository;

    @MockitoBean
    private JavaMailSender mockMailSender;

    @Test
    void 사용자_인증성공시_활성화된다() throws Exception {
        // given
        User user = objectFactory.createUsersEntity();
        assertThat(user.getIsActivated()).isFalse();
        String token = jwtUtil.generateEmailVerifyToken(user.getEmail(), user.getId());
        VerifyTokenReqDto verifyTokenReqDto = new VerifyTokenReqDto(token);
        // when
        mvc.perform(post("/api/v1/users/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(verifyTokenReqDto))
                )
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
        VerifyTokenReqDto verifyTokenReqDto = new VerifyTokenReqDto(token);
        // when
        MvcResult mvcResult = mvc.perform(post("/api/v1/users/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(verifyTokenReqDto))
                )
                .andReturn();

        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);

        assertThat(response.getDetail()).isEqualTo("유효하지 않은 토큰 정보입니다.");
    }

    @Test
    void 사용자는_계정찾기는_이메일로_아이디와_비밀번호변경_토큰과_함께_발송된다() throws Exception {
        // given
        when(mockMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        User user = objectFactory.createUsersEntity();
        String email = user.getEmail();
        UserRecoveryReqDto userRecoveryReqDto = new UserRecoveryReqDto(email);
        // when
        mvc.perform(post("/api/v1/users/recover")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userRecoveryReqDto))
                )
                .andExpect(status().isOk());
        // then

        verify(mockMailSender, times(1)).createMimeMessage();
        verify(mockMailSender, times(1)).send((MimeMessage) any());
    }

    @Test
    void 사용자는_계정찾기시_등록되지_않은_이메일은_예외를_발생한다() throws Exception {
        // given
        UserRecoveryReqDto userRecoveryReqDto = new UserRecoveryReqDto("fail@example.com");
        // when
        MvcResult mvcResult = mvc.perform(post("/api/v1/users/recover")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userRecoveryReqDto))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);

        assertThat(response.getDetail()).isEqualTo("엔티티 정보를 찾을 수 없습니다. entity: [User] identifier [null]");
    }

    @Test
    void 사용자_계정찾기시_인증코드가_생성된다() throws Exception {
        // given
        when(mockMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        User user = objectFactory.createUsersEntity();
        String email = user.getEmail();
        UserRecoveryReqDto userRecoveryReqDto = new UserRecoveryReqDto(email);
        // when
        mvc.perform(post("/api/v1/users/recover")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userRecoveryReqDto))
                )
                .andExpect(status().isOk());
        // then
        Optional<UserVerification> optionalVerification = userVerificationRepository.findByUserId(user.getId());
        assertThat(optionalVerification.isPresent()).isTrue();

        verify(mockMailSender, times(1)).createMimeMessage();
        verify(mockMailSender, times(1)).send((MimeMessage) any());
    }

    @Test
    void 사용자_계정찾기시_인증코드가_이미_있는경우_재생성된다() throws Exception {
        // given
        when(mockMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        User user = objectFactory.createUsersEntity();
        UserVerification userVerification = objectFactory.createUserVerificationEntity(user);
        String beforeVerificationCode = userVerification.getVerificationCode();
        String email = user.getEmail();
        UserRecoveryReqDto userRecoveryReqDto = new UserRecoveryReqDto(email);

        // when
        mvc.perform(post("/api/v1/users/recover")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userRecoveryReqDto))
                )
                .andExpect(status().isOk());

        // then
        UserVerification findVerification = userVerificationRepository.findByUserId(user.getId())
                .orElse(null);
        assertThat(findVerification).isNotNull();
        assertThat(findVerification.getVerificationCode()).isNotEqualTo(beforeVerificationCode);

        verify(mockMailSender, times(1)).createMimeMessage();
        verify(mockMailSender, times(1)).send((MimeMessage) any());
    }

    @Test
    void 사용자는_비밀번호_변경이_가능하다() throws Exception {
        // given
        User user = objectFactory.createUsersEntity();
        UserVerification userVerification = objectFactory.createUserVerificationEntity(user);
        String token = jwtUtil.generateRecoveryToken(user.getId(), userVerification.getVerificationCode());
        UserPasswordReqDto dto = objectFactory.createUserPasswordReqDto(token);

        // when
        mvc.perform(patch("/api/v1/users/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(dto))
        ).andExpect(status().isOk());

        // then
        assertThat(passwordEncoder.matches(dto.password(), user.getPassword())).isTrue();
    }
}
