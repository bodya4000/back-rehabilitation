package rehabilitation.api.service.repositories.sql;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import rehabilitation.api.service.config.ElasticSearchConfig;
import rehabilitation.api.service.dto.entities.UserDto;
import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.UserRepository;
import rehabilitation.api.service.utills.GeneratingUtils;

@SpringBootTest(classes = ElasticSearchConfig.class)
@ExtendWith(SpringExtension.class)
public class UserRepositoryTest  {
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
    private GeneratingUtils generatingUtils;

    @Autowired
    private UserRepository underTestObject;

    @Autowired
    private EntityManager entityManager;

    @AfterEach
    void clearDb() {
        entityManager.clear();
    }




    @Test
    @DisplayName("Test finding a client by login and fetching its roles.")
    void Should_FindUserWithRoles_When_LoginCorrect() throws NotFoundLoginException {
        // given
        UserModel userModel = generatingUtils.createUserAndSave(1);
        String userLogin = userModel.getLogin();

        // when
        UserModel loadTrueUser = underTestObject.findByLoginFetchRoles(userLogin).orElseThrow(() -> new NotFoundLoginException(userLogin));

        // then
        assertUserFetchRoles(loadTrueUser, userModel);

        //additional assert on wrong login
        String falseLogin = "falseLogin";
        assertNotFoundLoginException(falseLogin);
    }

    @Test
    @DisplayName("Test finding a UserModel by id and mapping it into UserDto.")
    void Should_FindUserAndMapToDto_When_LoginCorrect() throws NotFoundLoginException {
        // given
        UserModel userModel = generatingUtils.createUserAndSave(1);
        String userLogin = userModel.getLogin();

        // when
        UserDto loadTrueUserDto = underTestObject.findDtoByLogin(userLogin).orElseThrow(() -> new NotFoundLoginException(userLogin));

        //then
        assertIfUserDtoIsCorrect(loadTrueUserDto, userModel);

        //additional assert on wrong login
        String falseLogin = "falseLogin";
        assertNotFoundLoginException(falseLogin);
    }

    private void assertUserFetchRoles(UserModel loadedUser, UserModel actualUser) {
        Assertions.assertThat(loadedUser).satisfies(user -> {
            Assertions.assertThat(user).isEqualTo(actualUser);
            Assertions.assertThat(user.getRoles()).isNotEmpty();
        });
    }

    private void assertIfUserDtoIsCorrect(
            UserDto userDto,
            UserModel userModel
    ){
        Assertions.assertThat(userDto).satisfies(
                dto -> {
                    Assertions.assertThat(dto.login()).isEqualTo(userModel.getLogin());
                    Assertions.assertThat(dto.email()).isEqualTo(userModel.getEmail());
                    Assertions.assertThat(dto.contactInformation()).isEqualTo(userModel.getContactInformation());
                    Assertions.assertThat(dto.address()).isEqualTo(userModel.getAddress());
                    Assertions.assertThat(dto.imgUrl()).isEqualTo(userModel.getImgUrl());
                }
        );
    }

    private void assertNotFoundLoginException(String falseLogin) {
        Assertions.assertThatThrownBy(() -> underTestObject.findByLogin(falseLogin).orElseThrow(() -> new NotFoundLoginException(falseLogin))).isInstanceOf(NotFoundLoginException.class).hasMessageContaining(falseLogin);
    }
}
