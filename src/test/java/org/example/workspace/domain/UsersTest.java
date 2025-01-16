package org.example.workspace.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.example.workspace.common.ApplicationConstant;
import org.example.workspace.dto.request.UsersReqDto;
import org.example.workspace.dto.response.FieldErrorResDto;
import org.example.workspace.dto.response.UsersResDto;
import org.example.workspace.entity.Users;
import org.example.workspace.factory.ObjectFactory;
import org.example.workspace.factory.RequestParameterFactory;
import org.example.workspace.repository.UsersRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(value = {ObjectFactory.class, RequestParameterFactory.class})
@AutoConfigureMockMvc
public class UsersTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ObjectFactory objectFactory;
    @Autowired
    private RequestParameterFactory requestParameterFactory;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("유저 생성 테스트")
    public void 유저_생성_테스트() throws Exception {
        // given
        UsersReqDto usersReqDto = objectFactory.createUsersReqDto();

        // when
        MvcResult sut = mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersReqDto))
                )
                .andExpect(status().isCreated())
                .andReturn();

        String responseString = sut.getResponse().getContentAsString();
        UsersResDto response = objectMapper.readValue(responseString, UsersResDto.class);

        // then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response).isNotNull();
        softAssertions.assertThat(response.loginId()).isEqualTo(usersReqDto.getLoginId());
        softAssertions.assertThat(response.userName()).isEqualTo(usersReqDto.getUserName());
        softAssertions.assertThat(response.nickname()).isEqualTo(usersReqDto.getNickname());
        softAssertions.assertThat(response.email()).isEqualTo(usersReqDto.getEmail());
        softAssertions.assertThat(response.phoneNumber()).isEqualTo(usersReqDto.getPhoneNumber());
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("유저 생성시 빈 값 유효성 테스트")
    public void 유저_생성시_빈값_유효성_테스트() throws Exception {
        // given
        UsersReqDto usersReqDto = UsersReqDto.builder()
                .build();

        // when
        MvcResult sut = mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(usersReqDto))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = sut.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);
        @SuppressWarnings("unchecked")
        List<FieldErrorResDto> fieldErrors = (List<FieldErrorResDto>) Objects.requireNonNull(response.getProperties())
                .get(ApplicationConstant.ExceptionHandler.FIELD_ERROR_KEY);

        // then
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
                        "email",
                        "phoneNumber"
                );

        softAssertions.assertAll();
    }

    @DisplayName("유저 생성시 올바르지 않은 요청 값 유효성 테스트")
    @RepeatedTest(9)
    public void 유저_생성시_올바르지_않은_요청_값_유효성_테스트(RepetitionInfo repetitionInfo) throws Exception {
        // given
        int totalCount = repetitionInfo.getTotalRepetitions();
        int nowCount = repetitionInfo.getCurrentRepetition() - 1;

        String invalidPassword = requestParameterFactory.createInvalidPassword(nowCount, totalCount);
        UsersReqDto usersReqDto = new UsersReqDto(
                requestParameterFactory.createInvalidLoginId(nowCount, totalCount),
                invalidPassword,
                invalidPassword,
                requestParameterFactory.createInvalidLengthString(nowCount, totalCount, 101),
                requestParameterFactory.createInvalidLengthString(nowCount, totalCount, 101),
                requestParameterFactory.createInvalidEmail(nowCount, totalCount),
                requestParameterFactory.createInvalidPhoneNumber(nowCount, totalCount)
        );

        // when
        MvcResult sut = mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(usersReqDto))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = sut.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);
        @SuppressWarnings("unchecked")
        List<FieldErrorResDto> fieldErrors = (List<FieldErrorResDto>) Objects.requireNonNull(response.getProperties())
                .get(ApplicationConstant.ExceptionHandler.FIELD_ERROR_KEY);

        // then
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
                        "email",
                        "phoneNumber"
                );

        softAssertions.assertAll();
    }

    @Test
    @DisplayName("유저 생성시 패스워드 불일치 예외 테스트")
    void 유저_생성시_패스워드_불일치_예외_테스트() throws Exception {
        // given
        UsersReqDto usersReqDto = new UsersReqDto(
                "kwang",
                "!work1234",
                "!!work1234",
                "최광섭",
                "최광섭",
                "choikwangsub@gmail.com",
                "01012341234"
        );

        // when
        MvcResult sut = mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(usersReqDto))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String responseString = sut.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);

        // then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response).isNotNull();
        softAssertions.assertThat(response.getDetail()).isEqualTo("유효하지 않은 비밀번호입니다.");
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("사용자 생성시 비밀번호 암호화 테스트")
    void 사용자_생성시_비밀번호_암호화_테스트() throws Exception {
        // given
        UsersReqDto usersReqDto = objectFactory.createUsersReqDto();

        // when
         mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(usersReqDto))
                )
                .andExpect(status().isCreated());
        Users user = usersRepository.findByLoginIdAndIsDeletedFalse(usersReqDto.getLoginId())
                .orElse(null);

        // then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(user).isNotNull();
        softAssertions.assertThat(passwordEncoder.matches(usersReqDto.getPassword(), user.getPassword())).isTrue();
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("사용자 생성시 id 중복 테스트")
    void 사용자_생성시_id_중복_테스트() throws Exception {
        // given
        UsersReqDto usersReqDto = objectFactory.createUsersReqDto();
        mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersReqDto))
                )
                .andExpect(status().isCreated());

        // when
        MvcResult mvcResult = mvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usersReqDto))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
        String responseString = mvcResult.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);

        // then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response).isNotNull();
        softAssertions.assertThat(response.getDetail()).isEqualTo("이미 등록된 사용자 아이디입니다.");
        softAssertions.assertAll();
    }
}
