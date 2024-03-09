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
import rehabilitation.api.service.dto.entities.SpecialistDto;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.entity.sql.UserType;
import rehabilitation.api.service.exceptionHandling.exception.buisness.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;
import rehabilitation.api.service.utills.GeneratingUtils;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ElasticSearchConfig.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class SpecialistControllerTest  {
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
    private SpecialistRepository specialistRepository;

    @Autowired
    private EntityManager entityManager;

    private record RegistrationAndAuthenticationDto(RegistrationDto specialistRegistrationDto, JwtResponseDto jwtResponseDto) {
    }

    @BeforeEach
    public void clearDb() {
        mongoTemplate.getDb().drop();
    }

    @Test
    @Transactional
    public void Should_LoadWholeListOfSpecialists() throws Exception {
        mockMvc.perform(get("/specialist-section/specialist")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    @Transactional
    public void Should_ReturnNotFound_When_CredentialsWrong() throws Exception {
        // arrange
        String login = "wrongLogin";

        // when/then
        mockMvc.perform(get("/specialist-section/specialist/{login}", login)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    @Transactional
    public void Should_ReturnOK_When_CredentialsRight() throws Exception {
        // arrange
        var specialist = generateSpecialistAndSave();

        // when/then
        mockMvc.perform(get("/specialist-section/specialist/{login}", specialist.getLogin())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(
                        mapToSpecialistDto(specialist)
                )));
    }



    @Test
    @WithMockUser
    @Transactional
    public void Should_ReturnForbidden_When_JwtIsNull() throws Exception {
        // arrange
        var specialist = generateSpecialistAndSave();
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "newFirstname");

        // when/then
        mockMvc.perform(patch("/specialist-section/specialist/{login}", specialist.getLogin())
                        .content(objectMapper.writeValueAsString(updates))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void Should_ReturnForbidden_When_TryToGetNotPrincipalData() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateSpecialist();

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "newFirstname");

        // when/then
        var falseLogin = "falseLogin";
        mockMvc.perform(patch("/specialist-section/specialist/{login}", falseLogin)
                        .content(objectMapper.writeValueAsString(updates))
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @Test
    @Transactional
    public void Should_UpdateSpecialist_When_LoginAndUpdatesRight() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateSpecialist();

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "newFirstname");

        // when/then
        mockMvc.perform(patch("/specialist-section/specialist/{login}",
                        registrationAndAuthenticationDto.specialistRegistrationDto.login())
                        .content(objectMapper.writeValueAsString(updates))
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("specialist successfully updated"));
    }


    @Test
    @Transactional
    public void Should_SuccessfullyDeleteSpecialist() throws Exception {
        // arrange
        RegistrationDto specialistRegistrationDto = getSpecialistRegistrationDto();

        // when/then
        JwtResponseDto adminJwtResponseDto = registerAndAuthenticateAdmin();
        mockMvc.perform(delete("/specialist-section/specialist/{login}", specialistRegistrationDto.login())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("specialist deleted"));
    }


    @Test
    @Transactional
    public void Should_ReturnForbidden_When_NotAdminTriesDeleteSpecialist() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateSpecialist();

        // when/then
        mockMvc.perform(delete("/specialist-section/specialist/{login}",
                        registrationAndAuthenticationDto.specialistRegistrationDto().login())
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto().accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void Should_SuccessfullyAddClientToSpecialist() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateSpecialist();

        var specialist = findSpecialistInDB(registrationAndAuthenticationDto);
        var client = generateAndSaveClient();

        // when/then
        mockMvc.perform(post("/specialist-section/{specialistLogin}/client/{clientLogin}",
                        specialist.getLogin(), client.getLogin())
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    public void Should_SuccessfullyRemoveClientFromSpecialist() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateSpecialist();

        var specialist = findSpecialistInDB(registrationAndAuthenticationDto);
        var client = generateAndSaveClient();

        specialist.addClient(client);
        entityManager.detach(specialist);
        entityManager.detach(client);

        // when/then
        mockMvc.perform(delete("/specialist-section/{specialistLogin}/client/{clientLogin}",
                        specialist.getLogin(), client.getLogin())
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // helpers

    @NotNull
    private SpecialistModel findSpecialistInDB(RegistrationAndAuthenticationDto registrationAndAuthenticationDto) {
        return specialistRepository.findByLogin(
                registrationAndAuthenticationDto.specialistRegistrationDto.login()).get();
    }

    @NotNull
    private RegistrationAndAuthenticationDto registerAndAuthenticateSpecialist() throws AlreadyExistLoginException, NotFoundLoginException {
        RegistrationDto specialistRegistrationDto = getSpecialistRegistrationDto();
        JwtResponseDto jwtResponseDto = authService.signIn(
                new AuthenticateDto(specialistRegistrationDto.login(), specialistRegistrationDto.password()));
        return new RegistrationAndAuthenticationDto(specialistRegistrationDto, jwtResponseDto);
    }

    @NotNull
    private RegistrationDto getSpecialistRegistrationDto() throws AlreadyExistLoginException {
        RegistrationDto specialistRegistrationDto = new RegistrationDto(
                "login", "email", "password", UserType.SPECIALIST);
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

    private ClientModel generateAndSaveClient() {
        return generatingUtils.createClientAndSave(1);
    }

    @NotNull
    private SpecialistDto mapToSpecialistDto(SpecialistModel specialist) {
        String reHubLogin = "";
        if (specialist.getReHub() != null){
            reHubLogin = specialist.getLogin();
        }
        return new SpecialistDto(specialist.getLogin(), specialist.getFirstName(), specialist.getLastName(),
                specialist.getCity(), specialist.getAge(), specialist.getExperience(),
                specialist.getRate(), specialist.getSpeciality(), specialist.getImgUrl(),
                specialist.getDescription(), reHubLogin, specialist.getListOfClientsLogin());
    }
}
