package rehabilitation.api.service.exceptionHandling.exception.auth;

import org.springframework.security.core.AuthenticationException;

public class WrongPasswordOrLoginException extends AuthenticationException {
    public WrongPasswordOrLoginException(String e){
        super(e);
    }
}
