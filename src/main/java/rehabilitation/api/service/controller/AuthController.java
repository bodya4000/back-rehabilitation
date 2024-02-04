package rehabilitation.api.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rehabilitation.api.service.business.businessServices.authBusiness.AuthService;
import rehabilitation.api.service.business.businessServices.authBusiness.jwtBusiness.RefreshTokenService;
import rehabilitation.api.service.dto.auth.AuthenticateDto;
import rehabilitation.api.service.dto.auth.JwtResponseDto;
import rehabilitation.api.service.dto.auth.RefreshTokenRequestDto;
import rehabilitation.api.service.dto.auth.RegistrationDto;
import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.exceptionHandling.exception.auth.BadRequestException;
import rehabilitation.api.service.exceptionHandling.exception.auth.InvalidTokenException;
import rehabilitation.api.service.exceptionHandling.exception.auth.WrongPasswordOrLoginException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.AlreadyExistLoginException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/registration")
    public ResponseEntity<String> createClient(@RequestBody RegistrationDto registrationDto)
            throws AlreadyExistLoginException, BadRequestException {
        authService.signUp(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                "User registered"
        );
    }

    @PostMapping("/authentication")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticateDto authenticateDto)
            throws WrongPasswordOrLoginException, NotFoundLoginException {

        JwtResponseDto auhtResponse = authService.signIn(authenticateDto);
        return ResponseEntity.status(HttpStatus.FOUND).body(
                auhtResponse
        );
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<JwtResponseDto> updateAccessToken(
            @RequestBody RefreshTokenRequestDto refreshTokenRequestDto)
            throws InvalidTokenException, NotFoundLoginException {
        System.out.println(refreshTokenRequestDto);
        return ResponseEntity.ok(authService.updateAccessToken(refreshTokenRequestDto.token()));
    }

}

