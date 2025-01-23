package org.example.workspace.integration.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.example.workspace.dto.request.*;
import org.example.workspace.dto.response.MenuResDto;
import org.example.workspace.dto.response.UserMenuResDto;
import org.example.workspace.entity.Contents;
import org.example.workspace.entity.User;
import org.example.workspace.entity.UserMenu;
import org.example.workspace.entity.UserVerification;
import org.example.workspace.entity.code.MenuType;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");
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
        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");
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

        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");
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

        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");
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

        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");
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
        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");
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

    @Test
    void 사용자_로그인_아이디로_중복검사가능하다() throws Exception {
        // given
        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");

        // when
        MvcResult mvcResult = mvc.perform(get("/api/v1/users/duplicate")
                        .param("value", user.getLoginId())
                        .param("type", UserDuplicateReqDto.Type.LOGIN_ID.name())
                )
                .andExpect(status().isOk())
                .andReturn();

        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        Boolean response = objectMapper.readValue(responseString, Boolean.class);

        assertThat(response).isTrue();
    }

    @Test
    void 사용자_이메일로_중복검사가능하다() throws Exception {
        // given
        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");

        // when
        MvcResult mvcResult = mvc.perform(get("/api/v1/users/duplicate")
                        .param("value", user.getEmail())
                        .param("type", UserDuplicateReqDto.Type.EMAIL.name())
                )
                .andExpect(status().isOk())
                .andReturn();

        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        Boolean response = objectMapper.readValue(responseString, Boolean.class);

        assertThat(response).isTrue();
    }

    @Test
    void 사용자_홈페이지명으로_중복검사가능하다() throws Exception {
        // given
        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");

        // when
        MvcResult mvcResult = mvc.perform(get("/api/v1/users/duplicate")
                        .param("value", user.getWorkspaceName())
                        .param("type", UserDuplicateReqDto.Type.WORKSPACE_NAME.name())
                )
                .andExpect(status().isOk())
                .andReturn();

        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        Boolean response = objectMapper.readValue(responseString, Boolean.class);

        assertThat(response).isTrue();
    }

    @Test
    void 중복되는_계정_없을경우_false를_반환한다() throws Exception {
        // given

        // when
        MvcResult mvcResult = mvc.perform(get("/api/v1/users/duplicate")
                        .param("value", "newuser123")
                        .param("type", UserDuplicateReqDto.Type.LOGIN_ID.name())
                )
                .andExpect(status().isOk())
                .andReturn();

        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        Boolean response = objectMapper.readValue(responseString, Boolean.class);

        assertThat(response).isFalse();
    }

    @Test
    void 사용자_메뉴를_조회할_수있다() throws Exception {
        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");
        Contents contents = objectFactory.createContentEntity();
        UserMenu userMenu = objectFactory.createUserMenu(user, contents, MenuType.ARTWORK);
        String token = jwtUtil.generateSignInToken(user.getLoginId(), user.getRole().getRoleType()).accessToken();
        // given
        MvcResult mvcResult = mvc.perform(get("/api/v1/user-menus")
                        .header("Authorization", "Bearer " + token)
                ).andExpect(status().isOk())
                .andReturn();
        // when
        String responseString = mvcResult.getResponse().getContentAsString();
        MenuResDto response = objectMapper.readValue(responseString, MenuResDto.class);

        // then
        assertThat(response.menuList()).hasSize(1);
        assertThat(response.menuList().get(0).menuType()).isEqualTo(userMenu.getMenuType());
        assertThat(response.menuList().get(0).contents().contentsName()).isEqualTo(contents.getContentsName());
    }

    @Test
    void 사용자는_메뉴를_관리할_수있다() throws Exception {
        // given
        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");
        MenuReqDto dto = new MenuReqDto(
                Set.of(
                        new UserMenuReqDto(null, null, MenuType.PROFILE),
                        new UserMenuReqDto(null, null, MenuType.SHOWCASE),
                        new UserMenuReqDto(null, null, MenuType.ARTWORK)
                )
        );
        String token = jwtUtil.generateSignInToken(user.getLoginId(), user.getRole().getRoleType()).accessToken();

        // when
        MvcResult mvcResult = mvc.perform(
                        post("/api/v1/user-menus")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(dto))
                ).andExpect(status().isOk())
                .andReturn();

        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        MenuResDto response = objectMapper.readValue(responseString, MenuResDto.class);

        assertThat(response.menuList()).hasSize(3);
    }

    @Test
    void 기존_메뉴_수정이_가능하다() throws Exception {
        // given
        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");
        UserMenu userMenu = objectFactory.createUserMenu(user, null, MenuType.ARTWORK);
        Contents contents = objectFactory.createContentEntity();
        MenuReqDto dto = new MenuReqDto(
                Set.of(new UserMenuReqDto(userMenu.getId(), contents.getId(), MenuType.SHOWCASE))
        );
        String token = jwtUtil.generateSignInToken(user.getLoginId(), user.getRole().getRoleType()).accessToken();

        // when
        MvcResult mvcResult = mvc.perform(
                        post("/api/v1/user-menus")
                                .header("Authorization", "Bearer " + token)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(dto))
                ).andExpect(status().isOk())
                .andReturn();

        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        MenuResDto response = objectMapper.readValue(responseString, MenuResDto.class);

        assertThat(response.menuList()).hasSize(1);
        UserMenuResDto responseMenu = response.menuList().get(0);

        assertThat(responseMenu.id()).isEqualTo(userMenu.getId());
        assertThat(responseMenu.contents().id()).isEqualTo(contents.getId());
    }
}
