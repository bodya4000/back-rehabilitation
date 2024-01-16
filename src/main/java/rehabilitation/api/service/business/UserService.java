package rehabilitation.api.service.business;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rehabilitation.api.service.entity.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.NotFoundLoginException;
import rehabilitation.api.service.repositories.ClientRepository;
import rehabilitation.api.service.repositories.ReHubRepository;
import rehabilitation.api.service.repositories.SpecialistRepository;
import rehabilitation.api.service.repositories.UserRepository;

import java.util.ArrayList;
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
                            clientRepository.findByLogin(login),
                            specialistRepository.findByLogin(login),
                            reHubRepository.findByLogin(login),
                            userRepository.findByLogin(login)
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

