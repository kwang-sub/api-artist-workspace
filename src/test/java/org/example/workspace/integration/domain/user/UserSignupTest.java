package org.example.workspace.integration.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.assertj.core.api.SoftAssertions;
import org.example.workspace.common.ApplicationConstant;
import org.example.workspace.dto.request.UserReqDto;
import org.example.workspace.dto.request.UsersSnsReqDto;
import org.example.workspace.dto.response.FieldErrorResDto;
import org.example.workspace.dto.response.UserResDto;
import org.example.workspace.entity.User;
import org.example.workspace.factory.ObjectFactory;
import org.example.workspace.factory.RequestParameterFactory;
import org.example.workspace.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
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

import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ComponentScan(basePackages = "org.example.workspace")
@AutoConfigureMockMvc
public class UserSignupTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ObjectFactory objectFactory;
    @Autowired
    private RequestParameterFactory requestParameterFactory;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JavaMailSender mockMailSender;

    private final String URL = "/api/v1/users";

    @Test
    @DisplayName("유저생성이가능하다")
    void 유저_생성이_가능하다() throws Exception {
        // given
        when(mockMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        UserReqDto userReqDto = objectFactory.createUsersReqDto();

        // when

        MvcResult sut = mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReqDto))
                )
                .andExpect(status().isCreated())
                .andReturn();

        // then
        String responseString = sut.getResponse().getContentAsString();
        UserResDto response = objectMapper.readValue(responseString, UserResDto.class);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response).isNotNull();
        softAssertions.assertThat(response.loginId()).isEqualTo(userReqDto.loginId());
        softAssertions.assertThat(response.userName()).isEqualTo(userReqDto.userName());
        softAssertions.assertThat(response.nickname()).isEqualTo(userReqDto.nickname());
        softAssertions.assertThat(response.email()).isEqualTo(userReqDto.email());
        softAssertions.assertThat(response.phoneNumber()).isEqualTo(userReqDto.phoneNumber());
        softAssertions.assertThat(response.isActivated()).isFalse();
        softAssertions.assertThat(response.userSnsList()).isNotEmpty();
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("유저생성시파라미터없으면안된다")
    void 유저_생성시_파라미터_없으면_안된다() throws Exception {
        // given
        UserReqDto userReqDto = UserReqDto.builder()
                .build();

        // when
        MvcResult sut = mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userReqDto))
                )
                .andExpect(status().isBadRequest())
                .andReturn();


        // then
        String responseString = sut.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);
        @SuppressWarnings("unchecked")
        List<FieldErrorResDto> fieldErrors = (List<FieldErrorResDto>) Objects.requireNonNull(response.getProperties())
                .get(ApplicationConstant.ExceptionHandler.FIELD_ERROR_KEY);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(fieldErrors).isNotNull();
        softAssertions.assertThat(fieldErrors)
                .extracting("field")
                .containsExactlyInAnyOrder(
                        "loginId",
                        "password",
                        "confirmPassword",
                        "userName",
                        "nickname",
                        "workspaceName",
                        "email",
                        "phoneNumber"
                );

        softAssertions.assertAll();
    }

    @DisplayName("유저생성시파라미터유효하지않으면안된다")
    @RepeatedTest(9)
    void 유저생성시_파라미터_유효하지_않으면_안된다(RepetitionInfo repetitionInfo) throws Exception {
        // given
        int totalCount = repetitionInfo.getTotalRepetitions();
        int nowCount = repetitionInfo.getCurrentRepetition() - 1;

        String invalidPassword = requestParameterFactory.createInvalidPassword(nowCount, totalCount);
        UserReqDto userReqDto = UserReqDto
                .builder()
                .loginId(requestParameterFactory.createInvalidLoginId(nowCount, totalCount))
                .password(invalidPassword)
                .confirmPassword(invalidPassword)
                .userName(requestParameterFactory.createInvalidLengthString(nowCount, totalCount, 101))
                .nickname(requestParameterFactory.createInvalidLengthString(nowCount, totalCount, 101))
                .workspaceName(requestParameterFactory.createInvalidLengthString(nowCount, totalCount, 101))
                .email(requestParameterFactory.createInvalidEmail(nowCount, totalCount))
                .phoneNumber(requestParameterFactory.createInvalidPhoneNumber(nowCount, totalCount))
                .userSnsList(List.of(UsersSnsReqDto.builder().build()))
                .build();

        // when
        MvcResult sut = mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userReqDto))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        String responseString = sut.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);
        @SuppressWarnings("unchecked")
        List<FieldErrorResDto> fieldErrors = (List<FieldErrorResDto>) Objects.requireNonNull(response.getProperties())
                .get(ApplicationConstant.ExceptionHandler.FIELD_ERROR_KEY);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(fieldErrors).isNotNull();
        softAssertions.assertThat(fieldErrors)
                .extracting("field")
                .containsExactlyInAnyOrder(
                        "loginId",
                        "password",
                        "confirmPassword",
                        "userName",
                        "nickname",
                        "workspaceName",
                        "email",
                        "phoneNumber",
                        "userSnsList[0].snsType",
                        "userSnsList[0].snsUsername"
                );

        softAssertions.assertAll();
    }

    @Test
    @DisplayName("패스워드와확인패스워드불일치하면안된다")
    void 패스워드와_확인패스워드_불일치하면_안된다() throws Exception {
        // given
        UserReqDto userReqDto = UserReqDto.builder()
                .loginId("kwang")
                .password("!work1234")
                .confirmPassword("!!work1234")
                .userName("최광섭")
                .nickname("최광섭")
                .workspaceName("최광섭")
                .email("test@gmail.com")
                .phoneNumber("01012341234")
                .build();

        // when
        MvcResult sut = mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userReqDto))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        String responseString = sut.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response).isNotNull();
        softAssertions.assertThat(response.getDetail()).isEqualTo("유효하지 않은 비밀번호입니다.");
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("유저비밀번호는암호화되어야한다")
    void 유저비밀번호는_암호화_되어야한다() throws Exception {
        // given
        when(mockMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        UserReqDto userReqDto = objectFactory.createUsersReqDto();

        // when
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userReqDto))
                )
                .andExpect(status().isCreated());

        // then
        User user = userRepository.findByLoginIdAndIsDeletedFalse(userReqDto.loginId())
                .orElse(null);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(user).isNotNull();
        softAssertions.assertThat(passwordEncoder.matches(userReqDto.password(), user.getPassword())).isTrue();
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("아이디가같은유저는생성안된다")
    void 아이디가_같은_유저는_생성_안된다() throws Exception {
        // given
        when(mockMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        UserReqDto userReqDto = objectFactory.createUsersReqDto();
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReqDto))
                )
                .andExpect(status().isCreated());

        // when
        MvcResult mvcResult = mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReqDto))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response).isNotNull();
        softAssertions.assertThat(response.getDetail()).isEqualTo("이미 등록된 사용자 아이디입니다.");
        softAssertions.assertAll();
    }

    @Test
    void 이메일이_같은_유저는_생성_안된다() throws Exception {
        // given
        when(mockMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        UserReqDto userReqDto = objectFactory.createUsersReqDto();
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReqDto))
                )
                .andExpect(status().isCreated());
        UserReqDto duplicateEmailUser = UserReqDto.builder()
                .loginId("newuser")
                .password("!work1234")
                .confirmPassword("!work1234")
                .userName("newuser")
                .nickname("newuser")
                .workspaceName("newuser")
                .email(userReqDto.email())
                .phoneNumber("01012341234")
                .build();

        // when
        MvcResult mvcResult = mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEmailUser))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response).isNotNull();
        softAssertions.assertThat(response.getDetail()).isEqualTo("이미 등록된 이메일입니다.");
        softAssertions.assertAll();
    }

    @Test
    void 회원가입시_이메일인증_메일이_발송된다() throws Exception {
        // given
        UserReqDto userReqDto = objectFactory.createUsersReqDto();
        when(mockMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));

        // when
        mvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userReqDto))
                )
                .andExpect(status().isCreated());

        // then
        verify(mockMailSender, times(1)).send((MimeMessage) any());
    }
}
