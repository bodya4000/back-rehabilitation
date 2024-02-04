package rehabilitation.api.service.business.businessServices.authBusiness.jwtBusiness;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rehabilitation.api.service.exceptionHandling.exception.auth.InvalidTokenException;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer";


    private final JwtTokenParser jwtTokenParser;

    private record JwtAndUsername(String jwt, String username) {
    }
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request, 
            @NonNull HttpServletResponse response, 
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        JwtAndUsername jwtAndUsername = processAuthorizationHeader(authHeader);

        if (jwtAndUsername.username() != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            addUsersAuthTokenToContext(jwtAndUsername);
        }
        filterChain.doFilter(request, response);
    }

    private void addUsersAuthTokenToContext(JwtAndUsername jwtAndUsername) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                jwtAndUsername.username(),
                null,
                jwtTokenParser.extractRoles(jwtAndUsername.jwt()).stream().map(SimpleGrantedAuthority::new).toList());
        SecurityContextHolder.getContext().setAuthentication(token);
        log.debug("User '{}' authenticated successfully.", jwtAndUsername.username());
    }

    private JwtAndUsername processAuthorizationHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith(BEARER)) {
            String jwt = getJwtToken(authHeader);
            String username = parseUsernameFromJwt(jwt);
            return new JwtAndUsername(jwt, username);
        } else {
            throw new InvalidTokenException();
        }
    }


    private String getJwtToken(String authHeader) throws InvalidTokenException {
        try {
            return authHeader.substring(7);
        } catch (StringIndexOutOfBoundsException e) {
            throw new InvalidTokenException();
        }
    }

    private String parseUsernameFromJwt(String jwt) {
        return jwtTokenParser.extractUsername(jwt);
    }
}