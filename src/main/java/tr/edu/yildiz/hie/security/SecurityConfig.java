package tr.edu.yildiz.hie.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring'e "Web güvenlik kurallarını buradan yöneteceğim" diyoruz
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF Korumasını Kapatıyoruz: JWT kullandığımız ve tarayıcı çerezi (cookie)
                // tutmadığımız için CSRF ataklarına karşı zaten güvendeyiz.
                .csrf(AbstractHttpConfigurer::disable)

                // 2. URL İzinleri (Rota Yönetimi)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**", "/error").permitAll()// Auth altındaki her şeye (Login) şifresiz izin ver
                        .anyRequest().authenticated() // Geriye kalan TÜM isteklere JWT (Kimlik) zorunluluğu getir
                )

                // 3. Oturum Yönetimi (Session Management)
                .sessionManagement(sess -> sess
                        // REST API yazdığımız için STATELESS (Durumsuz) mimari seçiyoruz.
                        // Yani sunucu kimseyi hafızasında tutmayacak, her istekte bileti (JWT) baştan kontrol edecek.
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. Kimlik Doğrulayıcıyı (Provider) Sisteme Tanıtıyoruz
                .authenticationProvider(authenticationProvider)

                // 5. Kendi yazdığımız Polis'i (JwtAuthenticationFilter), Spring'in standart şifre sorma
                // filtresinden HEMEN ÖNCEYE yerleştiriyoruz. Böylece bilet kontrolü ilk başta yapılıyor.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
