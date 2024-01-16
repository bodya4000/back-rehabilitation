    package rehabilitation.api.service.specialist;

    import lombok.extern.slf4j.Slf4j;
    import org.assertj.core.api.Assertions;
    import org.junit.jupiter.api.AfterEach;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
    import org.springframework.context.annotation.Import;
    import rehabilitation.api.service.config.ConfigTest;
    import rehabilitation.api.service.entity.*;
    import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
    import rehabilitation.api.service.repositories.ClientRepository;
    import rehabilitation.api.service.repositories.SpecialistRepository;
    import rehabilitation.api.service.utills.GeneratingUtils;

    import java.util.List;

    @DataJpaTest
    @Slf4j
    @Import(ConfigTest.class)
    class SpecialistRepositoryTest {

        private static final String CLIENT = "client";
        private static final String SPECIALIST = "specialist";
        private static final String REHUB = "rehub";


        @Autowired
        private ClientRepository clientRepository;

        @Autowired
        private SpecialistRepository underTestObject;

        @Autowired
        private GeneratingUtils generatingUtils;

        @AfterEach
        void clearDb() {
            underTestObject.deleteAll();
            clientRepository.deleteAll();
        }


        @Test
        @DisplayName("Test if specialists are correctly loaded with clients and with reHub")
        void testIfSpecialistsAreLoadedWithClientsAndReHubs() {
            // given
            var map = generatingUtils.createClientAndSpecialistAndSave(1);
            ClientModel clientModel = (ClientModel) map.get(CLIENT);
            ReHubModel reHubModel = (ReHubModel) map.get(REHUB);

            // when
            List<SpecialistModel> specialistModels = underTestObject.findAllBy();

            // then
            assertSpecialistsLoadedWithClients(specialistModels, clientModel);
            assertSpecialistsLoadedWithRehub(specialistModels, reHubModel);
        }

        @Test
        @DisplayName("Test if specialists are correctly loaded with clients and with reHub by login")
        void testFindingSpecialistByLoginWithClientsAndRehub() throws NotFoundLoginException {
            // given
            var map = generatingUtils.createClientAndSpecialistAndSave(1);
            ClientModel clientModel = (ClientModel) map.get(CLIENT);
            ReHubModel reHubModel = (ReHubModel) map.get(REHUB);
            SpecialistModel specialistModel = (SpecialistModel) map.get(SPECIALIST);

            String specialistLogin = specialistModel.getLogin();

            // when
            SpecialistModel loadTrueSpecialist = underTestObject.findByLogin(specialistLogin)
                    .orElseThrow(() -> new NotFoundLoginException(specialistLogin));

            // when/then
            assertFindByLogin(clientModel, specialistModel, loadTrueSpecialist);

            // then

            assertSpecialistsLoadedWithClients(specialistModel, clientModel);
            assertSpecialistsLoadedWithRehub(specialistModel, reHubModel);

            // Additional test case with false login
            String falseLogin = "falseLogin";
            assertNotFoundLoginException(falseLogin);
        }

        @Test
        @DisplayName("Test finding a specialist by login and fetching its roles.")
        void testFindingByLoginAndFetchingRoles() throws NotFoundLoginException {
            // given
            var map = generatingUtils.createClientAndSpecialistAndSave(1);
            SpecialistModel specialistModel = (SpecialistModel) map.get(SPECIALIST);
            String specialistLogin = specialistModel.getLogin();

            // when
            SpecialistModel loadTrueSpecialist = underTestObject.findByLoginFetchRoles(specialistLogin)
                    .orElseThrow(() -> new NotFoundLoginException(specialistLogin));

            // then
            assertSpecialistFetchRoles(specialistModel, loadTrueSpecialist);

            // Additional test case with false login
            String falseLogin = "falseLogin";
            assertNotFoundLoginException(falseLogin);
        }


        private void assertSpecialistFetchRoles(SpecialistModel expectedSpecialist, SpecialistModel actualSpecialist) {
            Assertions.assertThat(actualSpecialist).satisfies(
                    specialist -> {
                        Assertions.assertThat(specialist).isEqualTo(expectedSpecialist);
                        Assertions.assertThat(specialist.getRoles()).isNotEmpty();
                    }
            );
        }
        private void assertFindByLogin(ClientModel clientModel,
                                       SpecialistModel expectedSpecialist, SpecialistModel actualSpecialist) {
            Assertions.assertThat(actualSpecialist).satisfies(
                    specialist -> {
                        Assertions.assertThat(specialist).isEqualTo(expectedSpecialist);
                        Assertions.assertThat(specialist.getClients()).contains(clientModel);
                    }
            );
        }

        /**
         * Asserts list of all specialists in db if they were loaded with clients
         * @param specialistModelList the list that is being asserted
         * @param expectedClient the client that we expect one of the specialist to have
         */

        private void assertSpecialistsLoadedWithClients(List<SpecialistModel> specialistModelList, ClientModel expectedClient) {
            Assertions.assertThat(specialistModelList).isNotEmpty().allSatisfy(specialist -> {
                Assertions.assertThat(specialist.getClients()).isNotNull();
                Assertions.assertThat(specialist.getClients()).isNotEmpty();
                Assertions.assertThat(specialist.getClients()).contains(expectedClient);
            });
        }

        /**
         * Asserts a specific specialist loaded by login in db if he was loaded with clients
         * @param specialist specialist that is being asserted
         * @param expectedClient the client that we expect one of the specialist to have
         */

        private void assertSpecialistsLoadedWithClients(SpecialistModel specialist, ClientModel expectedClient) {
                Assertions.assertThat(specialist.getClients()).isNotNull();
                Assertions.assertThat(specialist.getClients()).isNotEmpty();
                Assertions.assertThat(specialist.getClients()).contains(expectedClient);
        }

        /**
         * Asserts list of all specialists in db if they were loaded with rehub
         * @param specialistModelList the list that is being asserted
         * @param reHubModel the rehub that we expect one of the specialist to have
         */
        private void assertSpecialistsLoadedWithRehub(List<SpecialistModel> specialistModelList, ReHubModel reHubModel) {
            Assertions.assertThat(specialistModelList).isNotEmpty().allSatisfy(specialist -> {
                Assertions.assertThat(specialist.getReHub()).isEqualTo(reHubModel);
            });
        }

        /**
         * Asserts a specific specialist loaded by login in db if he was loaded with rehub
         * @param specialist specialist that is being asserted
         * @param reHubModel the rehub that we expect one of the specialist to have
         */
        private void assertSpecialistsLoadedWithRehub(SpecialistModel specialist, ReHubModel reHubModel) {
                Assertions.assertThat(specialist.getReHub()).isEqualTo(reHubModel);
        }


        /**
         * Asserts if exception has been thrown
         * @param falseLogin false value on purpose
         */
        private void assertNotFoundLoginException(String falseLogin) {
            Assertions.assertThatThrownBy(() ->
                            underTestObject.findByLogin(falseLogin)
                                    .orElseThrow(() -> new NotFoundLoginException(falseLogin))
                    )
                    .isInstanceOf(NotFoundLoginException.class)
                    .hasMessageContaining(falseLogin);
        }
    }
