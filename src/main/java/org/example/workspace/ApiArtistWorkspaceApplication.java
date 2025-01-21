package org.example.workspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ApiArtistWorkspaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiArtistWorkspaceApplication.class, args);
    }

}
