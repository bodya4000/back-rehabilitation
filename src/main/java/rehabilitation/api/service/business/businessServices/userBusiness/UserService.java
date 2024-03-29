package rehabilitation.api.service.business.businessServices.userBusiness;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;
import rehabilitation.api.service.repositories.jpa.ClientRepository;
import rehabilitation.api.service.repositories.jpa.ReHubRepository;
import rehabilitation.api.service.repositories.jpa.SpecialistRepository;
import rehabilitation.api.service.repositories.jpa.UserRepository;

import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final ClientRepository clientRepository;
    private final ReHubRepository reHubRepository;
    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        try {
            UserModel user = Stream.of(
                            clientRepository.findByLoginFetchRoles(login),
                            specialistRepository.findByLoginFetchRoles(login),
                            reHubRepository.findByLoginFetchRoles(login),
                            userRepository.findByLoginFetchRoles(login)
                    )
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst()
                    .orElseThrow(() -> new NotFoundLoginException(login));


            return new org.springframework.security.core.userdetails.User(
                    user.getLogin(),
                    user.getPassword(),
                    user.getRoles()
            );
        } catch (NotFoundLoginException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }
}

