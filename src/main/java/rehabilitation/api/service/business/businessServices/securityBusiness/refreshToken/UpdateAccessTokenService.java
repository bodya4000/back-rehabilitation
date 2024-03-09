package rehabilitation.api.service.business.businessServices.securityBusiness.refreshToken;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rehabilitation.api.service.business.businessServices.securityBusiness.jwtToken.JwtTokenBuilder;
import rehabilitation.api.service.business.businessServices.securityBusiness.refreshToken.RefreshTokenService;
import rehabilitation.api.service.business.businessServices.userBusiness.UserService;
import rehabilitation.api.service.dto.auth.JwtResponseDto;
import rehabilitation.api.service.entity.sql.UserModel;
import rehabilitation.api.service.entity.sql.security.RefreshToken;
import rehabilitation.api.service.exceptionHandling.exception.auth.InvalidTokenException;
import rehabilitation.api.service.exceptionHandling.exception.auth.UserModelDoesNotExistException;
import rehabilitation.api.service.exceptionHandling.exception.buisness.NotFoundLoginException;

@Service
@RequiredArgsConstructor
public class UpdateAccessTokenService {

    private final UserService userService;
    private final JwtTokenBuilder jwtTokenBuilder;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public JwtResponseDto updateAccessToken(String token) throws InvalidTokenException, NotFoundLoginException {
        RefreshToken refreshToken = findIfExist(token);
        validateRefreshToken(refreshToken);
        removeRefreshTokenFromDatabase(refreshToken);
        UserModel user = extractUserFromRefreshToken(refreshToken);
        UserDetails userDetails = getUserDetails(user);
        return generateJwtResponseDto(userDetails);
    }

    private UserModel extractUserFromRefreshToken(RefreshToken refreshToken) {
        if (refreshToken.getToken() != null) {
            return refreshToken.getUserModel();
        }
        throw new UserModelDoesNotExistException();
    }

    private JwtResponseDto generateJwtResponseDto(UserDetails userDetails) throws NotFoundLoginException {
        String newAccessToken = jwtTokenBuilder.generateToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(userDetails);
        return new JwtResponseDto(
                newAccessToken,
                newRefreshToken.getToken());
    }

    private void removeRefreshTokenFromDatabase(RefreshToken refreshToken) {
        refreshTokenService.removeRefreshToken(refreshToken);
    }

    private RefreshToken findIfExist(String token) {
        return refreshTokenService.getByToken(token);
    }


    private UserDetails getUserDetails(UserModel user) {
        return userService.loadUserByUsername(user.getLogin());
    }
    private void validateRefreshToken(RefreshToken refreshToken) throws InvalidTokenException {
        refreshTokenService.verifyExpiration(refreshToken);
    }
}
