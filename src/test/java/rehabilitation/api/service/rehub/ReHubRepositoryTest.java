    package rehabilitation.api.service.rehub;

    import lombok.extern.slf4j.Slf4j;
    import org.assertj.core.api.Assertions;
    import org.junit.jupiter.api.AfterEach;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
    import org.springframework.context.annotation.Import;
    import rehabilitation.api.service.config.ConfigTest;
    import rehabilitation.api.service.entity.ClientModel;
    import rehabilitation.api.service.entity.ReHubModel;
    import rehabilitation.api.service.entity.SpecialistModel;
    import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
    import rehabilitation.api.service.repositories.jpa.ClientRepository;
    import rehabilitation.api.service.repositories.jpa.ReHubRepository;
    import rehabilitation.api.service.repositories.jpa.SpecialistRepository;
    import rehabilitation.api.service.utills.GeneratingUtils;

    import java.util.List;

    @DataJpaTest
    @Slf4j
    @Import(ConfigTest.class)
    class ReHubRepositoryTest {

        private static final String CLIENT = "client";
        private static final String SPECIALIST = "specialist";
        private static final String REHUB = "rehub";


        @Autowired
        private ClientRepository clientRepository;

        @Autowired
        private SpecialistRepository specialistRepository;

        @Autowired
        private ReHubRepository underTestObject;

        @Autowired
        private GeneratingUtils generatingUtils;

        @AfterEach
        void clearDb() {
            underTestObject.deleteAll();
            clientRepository.deleteAll();
            specialistRepository.deleteAll();
        }


        @Test
        @DisplayName("Test if rehub are correctly loaded with specialists and clients")
        void testIfReHubsLoadedWithSpecialistsAndClients() {
            // given
            var map = generatingUtils.createClientAndSpecialistAndSave(1);
            ClientModel clientModel = (ClientModel) map.get(CLIENT);
            SpecialistModel specialistModel = (SpecialistModel) map.get(SPECIALIST);

            // when
            List<ReHubModel> reHubModelList = underTestObject.findAllBy();

            // then
            assertReHubsLoadedWithSpecialistsAndClients(reHubModelList, specialistModel, clientModel);
        }

        @Test
        @DisplayName("Test if rehub is found with specialists and with clients by login")
        void testFindingReHubByLogin() throws NotFoundLoginException {
            // given
            var map = generatingUtils.createClientAndSpecialistAndSave(1);
            ClientModel clientModel = (ClientModel) map.get(CLIENT);
            ReHubModel reHubModel = (ReHubModel) map.get(REHUB);
            SpecialistModel specialistModel = (SpecialistModel) map.get(SPECIALIST);

            String reHubLogin = reHubModel.getLogin();

            // when
            ReHubModel loadTrueReHub = underTestObject.findByLogin(reHubLogin)
                    .orElseThrow(() -> new NotFoundLoginException(reHubLogin));

            // when/then
            assertFindByLogin(loadTrueReHub, reHubModel);

            //then
            assertReHubsLoadedWithSpecialistsAndClients(reHubModel, specialistModel, clientModel);

            // Additional test case with false login
            String falseLogin = "falseLogin";
            assertNotFoundLoginException(falseLogin);
        }

        @Test
        @DisplayName("Test finding a rehub by login and fetching its roles.")
        void testFindingByLoginAndFetchingRoles() throws NotFoundLoginException {
            // given
            var map = generatingUtils.createClientAndSpecialistAndSave(1);
            ReHubModel reHubModel = (ReHubModel) map.get(REHUB);
            String reHubLogin = reHubModel.getLogin();

            // when
            ReHubModel loadTrueReHub = underTestObject.findByLoginFetchRoles(reHubLogin)
                    .orElseThrow(() -> new NotFoundLoginException(reHubLogin));

            // then
            assertReHubFetchRoles(loadTrueReHub);

            // Additional test case with false login
            String falseLogin = "falseLogin";
            assertNotFoundLoginException(falseLogin);
        }

        /**
         * Asserts if specialist has been loaded with roles
         *
         * @param loadedReHub the specialist that has been loaded from db
         */
        private void assertReHubFetchRoles(ReHubModel loadedReHub) {
            Assertions.assertThat(loadedReHub).satisfies(
                    reHub -> {
                        Assertions.assertThat(reHub.getRoles()).isNotEmpty().isNotNull();
                    }
            );
        }

        /**
         * Asserts if specialist have been loaded with clients from db
         *
         * @param loadedFromDbReHub the specialist that has been loaded from db
         * @param actualRehub specialist that we created and saved in db
         */
        private void assertFindByLogin(
                ReHubModel loadedFromDbReHub,
                ReHubModel actualRehub
        ) {
            Assertions.assertThat(loadedFromDbReHub).satisfies(
                    reHub -> {
                        Assertions.assertThat(reHub).isEqualTo(actualRehub);
                    }
            );
        }

        /**
         * Asserts if each specialist have been loaded with his clients
         *
         * @param reHubModelList the list we have loaded from db
         * @param expectedClient the client that we want one of the specialist to have
         */
        private void assertReHubsLoadedWithSpecialistsAndClients(
                List<ReHubModel> reHubModelList,
                SpecialistModel expectedSpecialist,
                ClientModel expectedClient) {

            Assertions.assertThat(reHubModelList).isNotEmpty().allSatisfy(rehub -> {
                Assertions.assertThat(rehub.getSpecialists()).allSatisfy(specialist -> {
                    Assertions.assertThat(specialist).isEqualTo(expectedSpecialist);
                    Assertions.assertThat(specialist.getClients()).contains(expectedClient);
                });
            });
        }

        private void assertReHubsLoadedWithSpecialistsAndClients(
                ReHubModel reHubModel,
                SpecialistModel expectedSpecialist,
                ClientModel expectedClient) {

                Assertions.assertThat(reHubModel.getSpecialists()).isNotNull().isNotEmpty();
                Assertions.assertThat(reHubModel.getSpecialists()).contains(expectedSpecialist).allSatisfy(
                        specialist -> {
                            Assertions.assertThat(specialist.getClients()).isNotEmpty().isNotNull();
                            Assertions.assertThat(specialist).isEqualTo(expectedSpecialist);
                            Assertions.assertThat(specialist.getClients()).contains(expectedClient);
                        }
                );
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
