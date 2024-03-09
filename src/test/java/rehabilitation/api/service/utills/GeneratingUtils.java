package rehabilitation.api.service.utills;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import rehabilitation.api.service.entity.sql.*;
import rehabilitation.api.service.entity.sql.security.Role;
import rehabilitation.api.service.entity.sql.security.UserRole;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;
import rehabilitation.api.service.repositories.jpa.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Component
public class GeneratingUtils {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private SpecialistRepository specialistRepository;
    @Autowired
    private ReHubRepository reHubRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Generates a client
     * @param i index for client
     * @return generated client
     */
    public ClientModel createClientAndSave(int i) {
        ClientModel clientModel = new ClientModel();
        clientModel.setLogin(EntityType.CLIENT.name() + i);
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
        specialistModel.setLogin(EntityType.SPECIALIST.name() + i);
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
    public Map<EntityType, UserModel> createClientAndSpecialistAndSave(int i) {
        Map<EntityType, UserModel> map = new HashMap<>();

        var reHubModel = createRehubAndSave(i);
        var specialist = createSpecialistAndSave(i);
        var client = createClientAndSave(i);
        reHubModel.addSpecialist(specialist);
        client.addSpecialist(specialist);
        clientRepository.save(client);

        map.put(EntityType.SPECIALIST ,specialist);
        map.put(EntityType.CLIENT, client);
        map.put(EntityType.REHUB, reHubModel);

        return map;
    }


    public ReHubModel createRehubAndSave(int i){
        ReHubModel reHubModel = new ReHubModel();
        reHubModel.setLogin(EntityType.REHUB.name() + i);
        reHubModel.setPassword("rehub");
        reHubModel.setEmail(reHubModel.getLogin() + "@mail.com");
        reHubModel.getRoles().add(new UserRole(Role.ROLE_REHUB, reHubModel));
        reHubRepository.save(reHubModel);
        return reHubModel;
    }


    public UserModel createUserAndSave(int i) {
        UserModel userModel = new UserModel();
        userModel.setLogin(EntityType.REHUB.name() + i);
        userModel.setPassword("rehub");
        userModel.setEmail(userModel.getLogin() + "@mail.com");
        userModel.addRole(new UserRole(Role.ROLE_ADMIN, userModel));
        userRepository.save(userModel);
        return userModel;
    }
}
