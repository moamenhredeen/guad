package app.guad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GuadApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuadApplication.class, args);
    }
}
