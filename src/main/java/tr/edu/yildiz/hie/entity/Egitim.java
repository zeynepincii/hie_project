package tr.edu.yildiz.hie.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "egitimler")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Egitim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "egitim_adi", nullable = false)
    private String egitimAdi;

    @Column(name = "egitmen_adi")
    private String egitmenAdi;

    @Column(name = "baslangic_tarihi", nullable = false)
    private LocalDateTime baslangicTarihi;

    @Column(name = "bitis_tarihi", nullable = false)
    private LocalDateTime bitisTarihi;

    // Eğitim bitince arka planda çalışan Quartz Scheduler bu bayrağı true yapacak
    @Column(name = "anket_aktif_mi")
    private boolean anketAktifMi = false;
}
