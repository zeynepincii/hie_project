package tr.edu.yildiz.hie.entity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "personel")
@Data // Lombok: Getter, Setter ve toString metodlarını otomatik yazar
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Personel implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Üniversite/Kurum sistemlerinde eşleştirme yapmak (Persis vb.) için kilit alan
    @Column(name = "sicil_no", nullable = false, unique = true)
    private String sicilNo;

    @Column(nullable = false)
    private String ad;

    @Column(nullable = false)
    private String soyad;

    @Column(nullable = false, unique = true)
    private String eposta;

    // JWT ve Spring Security entegrasyonu için şifre alanı
    @Column(nullable = false)
    private String sifre;

    @Column(nullable = false)
    private String unvan; // Persis'ten güncellenecek alan

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Rol rol; // Enum tipindeki rolümüz

    // Veritabanından veriyi tamamen silmek yerine "pasife çekmek" kurumsal bir reflekstir
    @Column(name = "aktif_mi")
    private boolean aktifMi = true;






    // --- SPRING SECURITY USERDETAILS METOTLARI ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Kullanıcının rolünü (Örn: PERSONEL, BIRIM_AMIRI) Spring Security'nin anladığı formata çeviriyoruz.
        // Eğer Rol adında bir enum oluşturduysan bu şekilde kullanabilirsin.
        return List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + this.rol.name()));
    }

    @Override
    public String getPassword() {
        // Veritabanındaki şifre alanımız hangisiyse onu döndürüyoruz
        return this.sifre;
    }

    @Override
    public String getUsername() {
        // Sistemde "kullanıcı adı" olarak e-posta adresini kullanıyoruz
        return this.eposta;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Hesap süresi dolma mantığı kullanmıyoruz
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Hesap kilitleme mantığı kullanmıyoruz
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Şifre geçerlilik süresi mantığı kullanmıyoruz
    }

    @Override
    public boolean isEnabled() {
        // Kendi oluşturduğumuz "aktifMi" bayrağını doğrudan Spring Security'nin aktiflik kontrolüne bağlıyoruz!
        // Böylece işten ayrılan (aktifMi=false) biri login olmaya çalışırsa Spring otomatik olarak reddedecek.
        return this.aktifMi;
    }
}
