package tr.edu.yildiz.hie.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import tr.edu.yildiz.hie.dto.AuthResponse;
import tr.edu.yildiz.hie.dto.LoginRequest;
import tr.edu.yildiz.hie.service.AuthService;
import org.springframework.web.bind.annotation.CrossOrigin;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("Sisteme giriş isteği alındı -> E-posta: {}", request.getEposta());

        try {
            // İşler yolunda giderse AuthService bileti (Token) dönecek
            AuthResponse token = authService.girisYap(request);
            log.info("Giriş BAŞARILI, bilet üretildi!");
            return ResponseEntity.ok(token);

        } catch (BadCredentialsException e) {
            // Eğer şifre veya e-posta yanlışsa Spring bu hatayı fırlatır
            log.error("HATA: Şifre uyuşmazlığı veya yanlış e-posta!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Hata: Girdiğiniz e-posta veya şifre yanlış!");

        } catch (Exception e) {
            // Eğer veritabanında rol/enum uyuşmazlığı gibi sistemsel bir hata varsa buraya düşer
            log.error("SİSTEMSEL HATA DETAYI: ", e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sistemsel Hata: " + e.getMessage());
        }
    }
}