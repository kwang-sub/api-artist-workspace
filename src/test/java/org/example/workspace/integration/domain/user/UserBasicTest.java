package org.example.workspace.integration.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.example.workspace.dto.request.UserUpdateReqDto;
import org.example.workspace.dto.response.UserResDto;
import org.example.workspace.dto.response.UserSnsResDto;
import org.example.workspace.entity.Contents;
import org.example.workspace.entity.User;
import org.example.workspace.entity.UserSns;
import org.example.workspace.entity.code.SnsType;
import org.example.workspace.factory.ObjectFactory;
import org.example.workspace.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@ComponentScan(basePackages = "org.example.workspace")
@AutoConfigureMockMvc
public class UserBasicTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ObjectFactory objectFactory;
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void 사용자는_본인정보를_조회할수있다() throws Exception {
        // given
        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");
        UserSns userSns = objectFactory.createUsersSnsEntity(user, SnsType.INSTAGRAM);
        String token = jwtUtil.generateSignInToken(user.getLoginId(), user.getRole().getRoleType())
                .accessToken();
        // when
        MvcResult mvcResult = mvc.perform(
                        get("/api/v1/users/my")
                                .header("Authorization", "Bearer " + token)
                ).andExpect(status().isOk())
                .andReturn();
        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        UserResDto response = objectMapper.readValue(responseString, UserResDto.class);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response).isNotNull();
        softAssertions.assertThat(response.id()).isEqualTo(user.getId());
        softAssertions.assertThat(response.loginId()).isEqualTo(user.getLoginId());
        softAssertions.assertThat(response.userName()).isEqualTo(user.getUserName());
        softAssertions.assertThat(response.nickname()).isEqualTo(user.getNickname());
        softAssertions.assertThat(response.email()).isEqualTo(user.getEmail());
        softAssertions.assertThat(response.phoneNumber()).isEqualTo(user.getPhoneNumber());
        softAssertions.assertThat(response.isActivated()).isEqualTo(user.getIsActivated());
        softAssertions.assertThat(response.userSnsList()).hasSize(1);
        softAssertions.assertThat(response.getClass().getRecordComponents().length).isEqualTo(11);
        softAssertions.assertAll();
    }


    @Test
    void 사용자는_본인_프로필을_수정할_수있다() throws Exception {
        // given
        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");
        UserSns userSnsTwitter = objectFactory.createUsersSnsEntity(user, SnsType.TWITTER);
        UserSns userSnsTwoFacebook = objectFactory.createUsersSnsEntity(user, SnsType.FACEBOOK);
        String token = jwtUtil.generateSignInToken(user.getLoginId(), user.getRole().getRoleType()).accessToken();
        Contents contents = objectFactory.createContentEntity();
        UserUpdateReqDto dto = objectFactory.createUserUpdateReqDto(userSnsTwitter, contents.getId());

        // when
        MvcResult mvcResult = mvc.perform(patch("/api/v1/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                ).andExpect(status().isOk())
                .andReturn();

        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        UserResDto response = objectMapper.readValue(responseString, UserResDto.class);
        List<Long> responseUserSnsIdList = response.userSnsList().stream().map(UserSnsResDto::id).toList();

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(response).isNotNull();
        softAssertions.assertThat(response.id()).isEqualTo(user.getId());
        softAssertions.assertThat(response.userName()).isEqualTo(dto.userName());
        softAssertions.assertThat(response.nickname()).isEqualTo(dto.nickname());
        softAssertions.assertThat(response.workspaceName()).isEqualTo(dto.workspaceName());
        softAssertions.assertThat(response.phoneNumber()).isEqualTo(dto.phoneNumber());
        softAssertions.assertThat(response.bio()).isEqualTo(dto.bio());
        softAssertions.assertThat(response.userSnsList()).hasSize(2);
        softAssertions.assertThat(responseUserSnsIdList.contains(userSnsTwitter.getId())).isTrue();
        softAssertions.assertThat(responseUserSnsIdList.contains(userSnsTwoFacebook.getId())).isFalse();

        softAssertions.assertThat(response.logo().id()).isEqualTo(dto.logoId());

        softAssertions.assertAll();
    }

    @Test
    void 프로필_수정시_워크스페이스명이_중복될수_없다() throws Exception {
        // given
        User exitsUser = objectFactory.createUsersEntity("exitsuser123", "exitsuser", "exitsuser@example.com");
        User user = objectFactory.createUsersEntity("user123", "user", "user@example.com");
        UserUpdateReqDto dto = UserUpdateReqDto.builder()
                .userName("update")
                .nickname("update")
                .workspaceName(exitsUser.getWorkspaceName())
                .phoneNumber("01112341234")
                .build();
        String token = jwtUtil.generateSignInToken(user.getLoginId(), user.getRole().getRoleType()).accessToken();
        // when
        MvcResult mvcResult = mvc.perform(patch("/api/v1/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                ).andExpect(status().isBadRequest())
                .andReturn();

        // then
        String responseString = mvcResult.getResponse().getContentAsString();
        ProblemDetail response = objectMapper.readValue(responseString, ProblemDetail.class);

        assertThat(response.getDetail()).isEqualTo("이미 등록된 워크스페이스명입니다.");
    }
}
