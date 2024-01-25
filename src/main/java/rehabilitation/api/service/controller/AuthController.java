package rehabilitation.api.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.business.businessServices.authBusiness.AuthService;
import rehabilitation.api.service.dto.auth.AuthenticateDto;
import rehabilitation.api.service.dto.auth.AuthenticationResponse;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.*;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /*
     * This method create a client in database and returns its json
     *
     *
     * here will be implemented registration logic
     * */
    @PostMapping("/registration")
    public ResponseEntity<?> createClient(@RequestBody RegistrationDto registrationDto) throws AlreadyExistLoginException, PasswordRegistryException, BadRequestException {

        UserModel userModel = authService.signUp(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                userModel
        );
    }

    @PostMapping("/authentication")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticateDto authenticateDto) throws WrongPasswordOrLoginException {

        AuthenticationResponse auhtResponse = authService.signIn(authenticateDto);
        return ResponseEntity.status(HttpStatus.FOUND).body(
                auhtResponse
        );
    }

}

