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
import rehabilitation.api.service.dto.entities.ClientDto;
import rehabilitation.api.service.entity.sql.ClientModel;
import rehabilitation.api.service.entity.sql.SpecialistModel;
import rehabilitation.api.service.entity.sql.UserType;
import rehabilitation.api.service.exceptionHandling.exception.buisness.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ClientRepository;

import rehabilitation.api.service.utills.GeneratingUtils;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ElasticSearchConfig.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class ClientControllerTest {
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
    private ClientRepository clientRepository;

    @Autowired
    private EntityManager entityManager;

    private record RegistrationAndAuthenticationDto(RegistrationDto clientRegistrationDto, JwtResponseDto jwtResponseDto) {
    }

    @BeforeEach
    public void clearDb() {
        mongoTemplate.getDb().drop();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @Transactional
    public void Should_LoadWholeListOfClients() throws Exception {
        mockMvc.perform(get("/client-section/client")
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
        mockMvc.perform(get("/client-section/client/{login}", login)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser
    @Transactional
    public void Should_ReturnOK_When_CredentialsRight() throws Exception {
        // arrange
        var client = generateAndSaveClient();

        // when/then
        mockMvc.perform(get("/client-section/client/{login}", client.getLogin())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(objectMapper.writeValueAsString(
                        mapToClientDto(client)
                )));
    }



    @Test
    @WithMockUser
    @Transactional
    public void Should_ReturnForbidden_When_JwtIsNull() throws Exception {
        // arrange
        var client = generateAndSaveClient();
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "newFirstname");

        // when/then
        mockMvc.perform(patch("/client-section/client/{login}", client.getLogin())
                        .content(objectMapper.writeValueAsString(updates))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void Should_ReturnForbidden_When_TryToGetNotPrincipalData() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateClient();

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "newFirstname");

        // when/then
        var falseLogin = "falseLogin";
        mockMvc.perform(patch("/client-section/client/{login}", falseLogin)
                        .content(objectMapper.writeValueAsString(updates))
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @Test
    @Transactional
    public void Should_UpdateClient_When_LoginAndUpdatesRight() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateClient();

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "newFirstname");

        // when/then
        mockMvc.perform(patch("/client-section/client/{login}", "login")
                        .content(objectMapper.writeValueAsString(updates))
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("client updated"));
    }


    @Test
    @Transactional
    public void Should_SuccessfullyDeleteClient() throws Exception {
        // arrange
        RegistrationDto clientRegistrationDto = getClientRegistrationDto();

        // when/then
        JwtResponseDto adminJwtResponseDto = registerAndAuthenticateAdmin();
        mockMvc.perform(delete("/client-section/client/{login}", clientRegistrationDto.login())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminJwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("client deleted"));
    }


    @Test
    @Transactional
    public void Should_ReturnForbidden_When_NotAdminTriesDeleteClient() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateClient();

        // when/then
        mockMvc.perform(delete("/client-section/client/{login}",
                        registrationAndAuthenticationDto.clientRegistrationDto().login())
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto().accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    public void Should_SuccessfullyAddSpecialistToClient() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateClient();

        var client = findClientInDB(registrationAndAuthenticationDto);
        var specialist = generateSpecialistAndSave();

        // when/then
        mockMvc.perform(post("/client-section/{clientLogin}/specialist/{specialistLogin}",
                        client.getLogin(), specialist.getLogin())
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @Transactional
    public void Should_SuccessfullyRemoveSpecialistFromClient() throws Exception {
        // arrange
        RegistrationAndAuthenticationDto registrationAndAuthenticationDto = registerAndAuthenticateClient();

        var client = findClientInDB(registrationAndAuthenticationDto);
        var specialist = generateSpecialistAndSave();

        client.addSpecialist(specialist);
        entityManager.detach(specialist);
        entityManager.detach(client);

        // when/then
        mockMvc.perform(delete("/client-section/{clientLogin}/specialist/{specialistLogin}",
                        client.getLogin(), specialist.getLogin())
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + registrationAndAuthenticationDto.jwtResponseDto.accessToken())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // helpers

    @NotNull
    private ClientModel findClientInDB(RegistrationAndAuthenticationDto registrationAndAuthenticationDto) {
        return clientRepository.findByLogin(
                registrationAndAuthenticationDto.clientRegistrationDto.login()).get();
    }

    @NotNull
    private RegistrationAndAuthenticationDto registerAndAuthenticateClient() throws AlreadyExistLoginException, NotFoundLoginException {
        RegistrationDto clientRegistrationDto = getClientRegistrationDto();
        JwtResponseDto jwtResponseDto = authService.signIn(
                new AuthenticateDto(clientRegistrationDto.login(), clientRegistrationDto.password()));
        return new RegistrationAndAuthenticationDto(clientRegistrationDto, jwtResponseDto);
    }

    @NotNull
    private RegistrationDto getClientRegistrationDto() throws AlreadyExistLoginException {
        RegistrationDto clientRegistrationDto = new RegistrationDto(
                "login", "email", "password", UserType.CLIENT);
        authService.signUp(clientRegistrationDto);
        return clientRegistrationDto;
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
    private ClientDto mapToClientDto(ClientModel client) {
        return new ClientDto(client.getLogin(), client.getFirstName(), client.getLastName(),
                client.getEmail(), client.getAddress(), client.getContactInformation(),
                client.getImgUrl(), client.getListOfSpecialistsLogin());
    }

}
