package rehabilitation.api.service.exceptionHandling.exception.auth;

public class TokenExpiredException extends RuntimeException{
    public TokenExpiredException(){
        super("Refresh token is expired. Please make a new login..!");
    }
}
