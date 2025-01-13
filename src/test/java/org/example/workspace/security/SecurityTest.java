package org.example.workspace.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("cors 9000번 포트 성공 테스트")
    void cors_9000번_포트_성공_테스트() throws Exception {
        mvc.perform(
                        get("/api/v1/login")
                                .header("Origin", "http://localhost:9000")
                                .header("Access-Control-Request-Method", "GET")
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("cors 임의 포트 실패 테스트")
    void cors_임의_포트_실패_테스트() throws Exception {
        mvc.perform(
                        get("/api/v1/login")
                                .header("Origin", "http://localhost:8081")
                                .header("Access-Control-Request-Method", "GET")
                )
                .andExpect(status().isForbidden());
    }
}
