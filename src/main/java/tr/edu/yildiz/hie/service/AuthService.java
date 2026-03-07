package tr.edu.yildiz.hie.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tr.edu.yildiz.hie.dto.AuthResponse;
import tr.edu.yildiz.hie.dto.LoginRequest;
import tr.edu.yildiz.hie.repository.PersonelRepository;
import tr.edu.yildiz.hie.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {
    // Spring Security'nin o meşhur şifre kontrolcüsü
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PersonelRepository personelRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse girisYap(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEposta(), request.getSifre())
        );

        var user = personelRepository.findByEposta(request.getEposta()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwtToken)
                .rol(user.getRol().name()) // <-- Kullanıcının rolünü Enum'dan String'e çevirip ekliyoruz!
                .build();
    }

}
