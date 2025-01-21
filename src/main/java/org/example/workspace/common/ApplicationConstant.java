package org.example.workspace.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConstant {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Email{
        public static final String SIGNUP_CONFIRM_TITLE = "[Artist Workspace] 신규 가입을 환영합니다! 이메일 인증을 완료해주세요.";
        public static final String RECOVERY_TITLE = "[Artist Workspace] 계정정보 찾기";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Entity {
        public static final int MAX_LENGTH_TEXT_SMALL = 100;
        public static final int MAX_LENGTH_TEXT_NORMAL = 255;
        public static final int MAX_LENGTH_TEXT_LARGE = 500;
        public static final int MAX_LENGTH_TEXT_DESC = 5000;

        public static final int MAX_LENGTH_CODE = 2;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Jwt {
        public static final String CLAIMS_KEY_ROLE = "role";
        public static final String CLAIMS_KEY_ID = "id";
        public static final String CLAIMS_KEY_TYPE = "type";
        public static final String CLAIMS_KEY_CODE = "code";

        public static final long ACCESS_TOKEN_EXPIRATION_MS = 5 * 60 * 1000;
        public static final long REFRESH_TOKEN_EXPIRATION_MS = 24 * 60 * 60 * 1000;
        public static final long EMAIL_VERIFY_TOKEN_EXPIRATION_MS = 60 * 60 * 1000;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ExceptionHandler {
        public static final String FIELD_ERROR_KEY = "fieldErrors";
    }

}
