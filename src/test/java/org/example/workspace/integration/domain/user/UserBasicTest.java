package org.example.workspace.integration.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.SoftAssertions;
import org.example.workspace.dto.response.UserResDto;
import org.example.workspace.entity.User;
import org.example.workspace.entity.UserSns;
import org.example.workspace.factory.ObjectFactory;
import org.example.workspace.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
        User user = objectFactory.createUsersEntity();
        UserSns userSns = objectFactory.createUsersSnsEntity(user);
        String token = jwtUtil.generateSignInToken(user.getLoginId(), user.getRole().getRoleType())
                .accessToken();
        // when
        MvcResult mvcResult = mvc.perform(
                        get("/api/v1/users/my")
                                .header("Authorization", "Bearer " + token)
                ).andExpect(MockMvcResultMatchers.status().isOk())
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
        softAssertions.assertThat(response.getClass().getRecordComponents().length).isEqualTo(8);
        softAssertions.assertAll();
    }


    @Test
    void 사용자는_본인_프로필을_수정할_수있다() {
        fail();
        // given

        // when

        // then
    }
}
