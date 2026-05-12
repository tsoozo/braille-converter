package mn.braille.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mn.braille.dto.AuthRequest;
import mn.braille.dto.AuthResponse;
import mn.braille.exception.BrailleException;
import mn.braille.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            String accessToken = jwtService.generateAccessToken(auth.getName());
            String refreshToken = jwtService.generateRefreshToken(auth.getName());
            log.info("User '{}' logged in", auth.getName());
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(jwtService.getAccessTokenExpiration())
                    .build();
        } catch (BadCredentialsException e) {
            throw new BrailleException("Нэвтрэх нэр эсвэл нууц үг буруу байна", HttpStatus.UNAUTHORIZED);
        }
    }

    public AuthResponse refresh(String refreshToken) {
        String username = jwtService.validateAndExtractUsername(refreshToken);
        String newAccess = jwtService.generateAccessToken(username);
        return AuthResponse.builder()
                .accessToken(newAccess)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }
}
