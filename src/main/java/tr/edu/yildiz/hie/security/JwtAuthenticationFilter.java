package tr.edu.yildiz.hie.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. İsteğin başlığındaki (Header) "Authorization" kısmını al
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 2. Eğer başlık yoksa veya "Bearer " ile başlamıyorsa, bu isteği atla
        // (Çünkü kullanıcı belki de sisteme ilk defa giriş (Login) yapmaya geliyordur, bilet sormamalıyız)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " kelimesinden sonrasını (asıl token şifresini) kesip al (7 karakter sonrasını al)
        jwt = authHeader.substring(7);

        // 4. Token'ın içinden e-posta adresini çıkar (Bir önceki adımda yazdığımız JwtService'i kullanarak)
        userEmail = jwtService.extractUsername(jwt);

        // 5. Eğer e-posta varsa ve o anki oturumda kimse giriş yapmış görünmüyorsa doğrulamaya başla
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Veritabanından bu e-postaya sahip kullanıcıyı bul
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Token gerçekten bu kullanıcıya mı ait ve süresi geçerli mi diye sor
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Geçerliyse Spring Security'ye "Bu kişi güvenilir, kapıyı aç" diyoruz
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Sistemin o anki yetkili kullanıcısı olarak bu kişiyi atıyoruz
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 6. Filtreyi devam ettir ve isteği asıl hedefine (Örn: EgitimController'a) ulaştır
        filterChain.doFilter(request, response);
    }
}
