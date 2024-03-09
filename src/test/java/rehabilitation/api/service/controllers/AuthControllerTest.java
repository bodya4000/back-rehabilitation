package rehabilitation.api.service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.lifecycle.Startables;
import rehabilitation.api.service.business.businessServices.securityBusiness.AuthService;
import rehabilitation.api.service.config.ElasticSearchConfig;
import rehabilitation.api.service.dto.auth.AuthenticateDto;
import rehabilitation.api.service.dto.auth.JwtResponseDto;
import rehabilitation.api.service.dto.auth.RefreshTokenRequestDto;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.sql.UserType;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ElasticSearchConfig.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:jammy")
            .withExposedPorts(27017);

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:alpine");

    static Network network;
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:alpine")
            .withExposedPorts(6379)
            .withNetwork(network);


    @DynamicPropertySource
    public static void setUpThings(DynamicPropertyRegistry registry) {
        Startables.deepStart(postgreSQLContainer, mongoDBContainer, redisContainer).join();

        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    static {
        mongoDBContainer.start();
        var mappedPort = mongoDBContainer.getMappedPort(27017);
        System.setProperty("mongodb.container.port", String.valueOf(mappedPort));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    public void clearDb() {
        mongoTemplate.getDb().drop();
    }


    @Test
    @Transactional
    void Should_CreateUser() throws Exception {
        // arrange
        RegistrationDto registrationDto = new RegistrationDto(
                "login", "email", "password", UserType.CLIENT);

        // when/then
        mockMvc.perform(post("/registration")
                        .content(objectMapper.writeValueAsString(registrationDto))
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered"));
    }

    @Test
    @Transactional
    void Should_ThrowAlreadyExistsException_When_UserExistsUserWithSameCredentials() throws Exception {
        // arrange
        RegistrationDto registrationDtoOfFirstUser = new RegistrationDto(
                "login", "email", "password", UserType.CLIENT);
        authService.signUp(registrationDtoOfFirstUser);

        RegistrationDto registrationDtoOfSecondUser = new RegistrationDto(
                "login", "email", "password", UserType.CLIENT);

        // when/then
        mockMvc.perform(post("/registration")
                        .content(objectMapper.writeValueAsString(registrationDtoOfSecondUser))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void Should_AuthenticateAndReturnJwt_When_UserExists() throws Exception {
        // arrange
        RegistrationDto registrationDto = new RegistrationDto(
                "login", "email", "password", UserType.CLIENT);
        authService.signUp(registrationDto);

        var authenticateDto = new AuthenticateDto("login", "password");
        // when/then
        mockMvc.perform(post("/authentication")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticateDto)))
                .andExpect(MockMvcResultMatchers.status().isFound());
    }

    @Test
    @Transactional
    void Should_RefreshAccessToken() throws Exception {
        // arrange
        RegistrationDto registrationDto = new RegistrationDto(
                "login", "email", "password", UserType.CLIENT);
        authService.signUp(registrationDto);

        JwtResponseDto jwtResponseDto = authService.signIn(
                new AuthenticateDto(registrationDto.login(), registrationDto.password()
        ));
        RefreshTokenRequestDto refreshTokenRequestDto = new RefreshTokenRequestDto(jwtResponseDto.refreshToken());

        // when/then
        mockMvc.perform(MockMvcRequestBuilders.post("/refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(refreshTokenRequestDto)))
                .andExpect(status().isFound());
    }

    @Test
    @Transactional
    void Should_ReturnNotFound_When_UserNotFoundDuringAuthentication() throws Exception {
        // arrange
        var authenticateDto = new AuthenticateDto("nonExistingUsername", "password");

        // when/then
        mockMvc.perform(post("/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(authenticateDto)))
                .andExpect(status().isUnauthorized());
    }
}