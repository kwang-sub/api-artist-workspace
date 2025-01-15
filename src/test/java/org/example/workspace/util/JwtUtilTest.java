package org.example.workspace.util;

import org.assertj.core.api.SoftAssertions;
import org.example.workspace.dto.response.AuthTokenResDto;
import org.example.workspace.entity.code.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;


class JwtUtilTest {

    private JwtUtil jwtUtil;
    String username;
    RoleType roleType;
    Clock clock;
    Instant now;

    @BeforeEach
    void setUp() {
        now = Instant.now();
        clock = Clock.fixed(now, ZoneId.systemDefault());
        jwtUtil = new JwtUtil(clock);
        username = "user";
        roleType = RoleType.ROLE_ARTIST;
    }

    @Test
    @DisplayName("토큰 생성 테스트")
    void 토큰_생성_테스트() {
        // given

        // when
        AuthTokenResDto tokens = jwtUtil.generateToken(username, roleType);

        // then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(tokens).isNotNull();
        softAssertions.assertThat(tokens.accessToken()).isNotEmpty();
        softAssertions.assertThat(tokens.refreshToken()).isNotEmpty();

        softAssertions.assertAll();
    }

    @Test
    @DisplayName("사용자 이름 추출 테스트")
    void 사용자_이름_추출_테스트() {
        // given
        AuthTokenResDto tokens = jwtUtil.generateToken(username, roleType);

        // when
        String extractedUsername = jwtUtil.extractUsername(tokens.accessToken());

        // then
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("사용자 역할 추출 테스트")
    void 사용자_역할_추출_테스트() {
        // given
        AuthTokenResDto tokens = jwtUtil.generateToken(username, roleType);

        // when
        RoleType extractedRole = jwtUtil.extractRole(tokens.accessToken());

        // then
        assertThat(extractedRole).isEqualTo(roleType);
    }

    @Test
    @DisplayName("액세스 토큰 만료 테스트")
    void 액세스_토큰_만료_테스트() {
        // given
        AuthTokenResDto tokens = jwtUtil.generateToken(username, roleType);
        assertThat(jwtUtil.isTokenExpired(tokens.accessToken())).isFalse();

        // when
        clock = Clock.fixed(now.plusSeconds(5 * 60), ZoneId.systemDefault());
        JwtUtil expiredJwtUtil = new JwtUtil(clock);

        clock = Clock.fixed(now.plusSeconds((5 * 60) - 1), ZoneId.systemDefault());
        JwtUtil nonExpiredJwtUtil = new JwtUtil(clock);

        // then
        assertThat(expiredJwtUtil.isTokenExpired(tokens.accessToken())).isTrue();
        assertThat(nonExpiredJwtUtil.isTokenExpired(tokens.accessToken())).isFalse();
    }

    @Test
    @DisplayName("리프레쉬 토큰 만료 테스트")
    void 리프레쉬_토큰_만료_테스트() {
        // given
        AuthTokenResDto tokens = jwtUtil.generateToken(username, roleType);
        assertThat(jwtUtil.isTokenExpired(tokens.refreshToken())).isFalse();

        // when
        clock = Clock.fixed(now.plusSeconds(24 * 60 * 60), ZoneId.systemDefault());
        JwtUtil expiredJwtUtil = new JwtUtil(clock);

        clock = Clock.fixed(now.plusSeconds((24 * 60 * 60) - 1), ZoneId.systemDefault());
        JwtUtil nonExpiredJwtUtil = new JwtUtil(clock);

        // then
        assertThat(expiredJwtUtil.isTokenExpired(tokens.refreshToken())).isTrue();
        assertThat(nonExpiredJwtUtil.isTokenExpired(tokens.refreshToken())).isFalse();
    }

    @Test
    @DisplayName("토큰 유효성 검사 테스트")
    void 토큰_유효성_검사_테스트() {
        // given
        AuthTokenResDto tokens = jwtUtil.generateToken(username, roleType);

        // when
        boolean isValid = jwtUtil.validateToken(tokens.accessToken(), username);
        boolean isInvalid = jwtUtil.validateToken(tokens.accessToken(), "another");

        // then
        assertThat(isValid).isTrue();
        assertThat(isInvalid).isFalse();
    }
}