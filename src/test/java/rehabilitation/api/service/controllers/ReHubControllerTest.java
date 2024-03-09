package rehabilitation.api.service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
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
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.dto.entities.RehubDto;
import rehabilitation.api.service.entity.sql.ReHubModel;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.entity.sql.UserType;
import rehabilitation.api.service.exceptionHandling.exception.buisness.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.utills.GeneratingUtils;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ElasticSearchConfig.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ReHubControllerTest {
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
    private ObjectMapper objectMapper;

    @Autowired
    private GeneratingUtils generatingUtils;

    @Autowired
    private AuthService authService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ReHubRepository reHubRepository;

    @Autowired
    private EntityManager entityManager;

    private record RegistrationAndAuthenticationDto(RegistrationDto reHubRegistrationDto, JwtResponseDto jwtResponseDto) {
    }

    @BeforeEach
    public void clearDb() {
        mongoTemplate.getDb().drop();
    }

    @Test
    @Transactional
    @WithMockUser(roles = "ADMIN")
    public void Should_LoadWholeListOfReHubs() throws Exception {
        mockMvc.perform(get("/rehub-section/rehub")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Transactional
    public void Should_ReturnNotFound_When_CredentialsWrong() throws Exception {
        // arrange
        String login = "wrongLogin";

        // when/then
        mockMvc.perform(get("/rehub-section/rehub/{login}", login)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Transactional
    public void Should_ReturnOK_When_CredentialsRight() throws Exception {
        // arrange
        var reHub = generateAndSaveReHub();

        // when/then
        mockMvc.perform(get("/rehub-section/rehub/{login}", reHub.getLogin())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(
                        mapToReHubDto(reHub)
                )));
    }



    @Test
    @WithMockUser
    @Transactional
    public void Should_ReturnForbidden_When_JwtIsNull() throws Exception {
        // arrange
        var reHub = generateAndSaveReHub();
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "newName");

        // when/then
        mockMvc.perform(patch("/rehub-section/rehub/{login}", reHub.getLogin())
                        .content(objectMapper.writeValueAsString(updates))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void Should_ReturnForbidden_When_TryToGetNotPrincipalData() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateReHub();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "newName");

        // when/then
        var falseLogin = "falseLogin";
        mockMvc.perform(patch("/rehub-section/rehub/{login}", falseLogin)
                        .content(objectMapper.writeValueAsString(updates))
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @Test
    @Transactional
    public void Should_UpdateReHub_When_LoginAndUpdatesRight() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateReHub();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "newName");

        // when/then
        mockMvc.perform(patch("/rehub-section/rehub/{login}",
                        registrationAndAuthenticationDto.reHubRegistrationDto.login())
                        .content(objectMapper.writeValueAsString(updates))
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("rehub updated"));
    }


    @Test
    @Transactional
    public void Should_SuccessfullyDeleteReHub() throws Exception {
        // arrange
        RegistrationDto specialistRegistrationDto = getReHubRegistrationDto();

        // when/then
        JwtResponseDto adminJwtResponseDto = registerAndAuthenticateAdmin();
        mockMvc.perform(delete("/rehub-section/rehub/{login}", specialistRegistrationDto.login())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("rehub deleted"));
    }


    @Test
    @Transactional
    public void Should_ReturnForbidden_When_NotAdminTriesDeleteReHub() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateReHub();

        // when/then
        mockMvc.perform(delete("/rehub-section/rehub/{login}",
                        registrationAndAuthenticationDto.reHubRegistrationDto().login())
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto().accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void Should_SuccessfullyAddSpecialistToReHub() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateReHub();

        var reHub = findReHubInDB(registrationAndAuthenticationDto);
        var specialist = generateSpecialistAndSave();

        // when/then
        mockMvc.perform(post("/rehub-section/{rehubLogin}/specialist/{specialistLogin}",
                        reHub.getLogin(), specialist.getLogin())
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    public void Should_SuccessfullyRemoveSpecialistFromSpecialist() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateReHub();

        var reHub = findReHubInDB(registrationAndAuthenticationDto);
        var specialist = generateSpecialistAndSave();

        reHub.addSpecialist(specialist);
        entityManager.detach(specialist);
        entityManager.detach(reHub);

        // when/then
        mockMvc.perform(delete("/rehub-section/{rehubLogin}/specialist/{specialistLogin}",
                        reHub.getLogin(), specialist.getLogin())
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // helpers

    @NotNull
    private ReHubModel findReHubInDB(RegistrationAndAuthenticationDto registrationAndAuthenticationDto) {
        return reHubRepository.findByLogin(
                registrationAndAuthenticationDto.reHubRegistrationDto.login()).get();
    }

    @NotNull
    private RegistrationAndAuthenticationDto registerAndAuthenticateReHub() throws AlreadyExistLoginException, NotFoundLoginException {
        RegistrationDto specialistRegistrationDto = getReHubRegistrationDto();
        JwtResponseDto jwtResponseDto = authService.signIn(
                new AuthenticateDto(specialistRegistrationDto.login(), specialistRegistrationDto.password()));
        return new RegistrationAndAuthenticationDto(specialistRegistrationDto, jwtResponseDto);
    }

    @NotNull
    private RegistrationDto getReHubRegistrationDto() throws AlreadyExistLoginException {
        RegistrationDto specialistRegistrationDto = new RegistrationDto(
                "login", "email", "password", UserType.REHUB);
        authService.signUp(specialistRegistrationDto);
        return specialistRegistrationDto;
    }

    private JwtResponseDto registerAndAuthenticateAdmin() throws AlreadyExistLoginException, NotFoundLoginException {
        RegistrationDto adminRegistrationDto = new RegistrationDto(
                "admin", "adminEmail", "password", UserType.ADMIN);
        authService.signUp(adminRegistrationDto);
        return authService.signIn(new AuthenticateDto("admin", "password"));
    }
    private SpecialistModel generateSpecialistAndSave() {
        return generatingUtils.createSpecialistAndSave(1);
    }

    private ReHubModel generateAndSaveReHub() {
        return generatingUtils.createRehubAndSave(1);
    }

    @NotNull
    private RehubDto mapToReHubDto(ReHubModel reHub) {
        return new RehubDto(reHub.getLogin(), reHub.getName(), reHub.getEmail(),
                reHub.getAddress(), reHub.getContactInformation(), reHub.getImgUrl(),
                reHub.getListOfSpecialistsLogin());
    }
}
