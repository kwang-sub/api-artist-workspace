package org.example.workspace.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationConstant {

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
    }
}
