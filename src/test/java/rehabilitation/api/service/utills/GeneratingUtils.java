package rehabilitation.api.service.utills;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import rehabilitation.api.service.entity.sql.*;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.entity.sql.security.UserRole;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;
import rehabilitation.api.service.repositories.jpa.UserRepository;

import java.util.HashMap;
import java.util.Map;

@TestComponent
public class GeneratingUtils {

    private static final String CLIENT = "client";
    private static final String SPECIALIST = "specialist";
    private static final String REHUB = "rehub";


    private ClientRepository clientRepository;
    private SpecialistRepository specialistRepository;
    private ReHubRepository reHubRepository;
    private UserRepository userRepository;


    @Autowired
    public void setClientRepository(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Autowired
    public void setSpecialistRepository(SpecialistRepository specialistRepository) {
        this.specialistRepository = specialistRepository;
    }
    @Autowired
    public void setReHubRepository(ReHubRepository reHubRepository) {
        this.reHubRepository = reHubRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    /**
     * Generates a client
     * @param i index for client
     * @return generated client
     */
    public ClientModel createClientAndSave(int i) {
        ClientModel clientModel = new ClientModel();
        clientModel.setLogin(CLIENT + i);
        clientModel.setPassword("client");
        clientModel.setEmail(clientModel.getLogin() + "@mail.com");
        clientModel.getRoles().add(new UserRole(Role.ROLE_CLIENT, clientModel));
        clientRepository.save(clientModel);
        return clientModel;
    }

    /**
     * Generates a specialist
     * @param i index for specialist
     * @return generated specialist
     */
    public SpecialistModel createSpecialistAndSave(int i) {
        SpecialistModel specialistModel = new SpecialistModel();
        specialistModel.setLogin(SPECIALIST + i);
        specialistModel.setPassword("specialist");
        specialistModel.setEmail(specialistModel.getLogin() + "@mail.com");
        specialistModel.getRoles().add(new UserRole(Role.ROLE_SPECIALIST, specialistModel));
        specialistRepository.save(specialistModel);
        return specialistModel;
    }

    /**
     * Generates a specialist and a client with specialist.
     * @param i index for specialist
     * @return Map of UserModels containing "specialist" and "client".
     */
    public Map<String, UserModel> createClientAndSpecialistAndSave(int i) {
        Map<String, UserModel> map = new HashMap<>();

        var reHubModel = createRehubAndSave(i);
        var specialist = createSpecialistAndSave(i);
        var client = createClientAndSave(i);
        reHubModel.addSpecialist(specialist);
        client.addSpecialist(specialist);
        clientRepository.save(client);

        map.put(SPECIALIST ,specialist);
        map.put(CLIENT, client);
        map.put(REHUB, reHubModel);

        return map;
    }


    private ReHubModel createRehubAndSave(int i){
        ReHubModel reHubModel = new ReHubModel();
        reHubModel.setLogin(REHUB + i);
        reHubModel.setPassword("rehub");
        reHubModel.setEmail(reHubModel.getLogin() + "@mail.com");
        reHubModel.getRoles().add(new UserRole(Role.ROLE_REHUB, reHubModel));
        reHubRepository.save(reHubModel);
        return reHubModel;
    }


    public UserModel createUserAndSave(int i) {
        UserModel userModel = new UserModel();
        userModel.setLogin(REHUB + i);
        userModel.setPassword("rehub");
        userModel.setEmail(userModel.getLogin() + "@mail.com");
        userModel.addRole(new UserRole(Role.ROLE_ADMIN, userModel));
        userRepository.save(userModel);
        return userModel;
    }
}
