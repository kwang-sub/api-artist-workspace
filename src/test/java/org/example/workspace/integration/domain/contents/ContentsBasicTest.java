package org.example.workspace.integration.domain.contents;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@Transactional
@ComponentScan(basePackages = "org.example.workspace")
@AutoConfigureMockMvc
public class ContentsBasicTest {

    @Test
    void 파일저장_할수있다() {
        fail();
        // given

        // when

        // then
    }

    @Test
    void 파일저장_내역은_데이터베이스로_관리된다() {
        fail();
        // given

        // when

        // then
    }

    @Test
    void 저장된_파일정보를_제공한다() {
        fail();
        // given

        // when

        // then
    }
}
