package app.guad.controllers;

import app.guad.security.SecurityConfiguration;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureRestTestClient
@Import(SecurityConfiguration.class)
@Testcontainers
class AuthControllerTest {
}