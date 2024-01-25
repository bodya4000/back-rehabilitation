package rehabilitation.api.service.user;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import rehabilitation.api.service.config.ConfigTest;
import rehabilitation.api.service.dto.entities.UserDto;
import rehabilitation.api.service.entity.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.UserRepository;
import rehabilitation.api.service.utills.GeneratingUtils;

@DataJpaTest
@Import(ConfigTest.class)
@Slf4j
public class UserRepositoryTest {

    private static final String CLIENT = "client";
    private static final String SPECIALIST = "specialist";

    @Autowired
    private GeneratingUtils generatingUtils;

    @Autowired
    private UserRepository underTestObject;

    @AfterEach
    void clearDb() {
        underTestObject.deleteAll();
    }




    @Test
    @DisplayName("Test finding a client by login and fetching its roles.")
    void findByLoginFetchRoles() throws NotFoundLoginException {
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
    void testMappingUserDto() throws NotFoundLoginException {
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

    /**
     * Asserts if exception has been thrown
     *
     * @param falseLogin false value on purpose
     */
    private void assertNotFoundLoginException(String falseLogin) {
        Assertions.assertThatThrownBy(() -> underTestObject.findByLogin(falseLogin).orElseThrow(() -> new NotFoundLoginException(falseLogin))).isInstanceOf(NotFoundLoginException.class).hasMessageContaining(falseLogin);
    }
}
