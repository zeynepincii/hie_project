package tr.edu.yildiz.hie.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "personel_amir_tarihcesi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonelAmirTarihcesi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Alt personel (Kimin amiri atanıyor?)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personel_id", nullable = false)
    private Personel personel;

    // Üst amir (Atanan amir kim?)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amir_id", nullable = false)
    private Personel amir;

    // Bu amirlik ilişkisi ne zaman başladı?
    @Column(name = "baslangic_tarihi", nullable = false)
    private LocalDate baslangicTarihi;

    // Bu amirlik ilişkisi ne zaman bitti? (Eğer hala devam ediyorsa null kalır)
    @Column(name = "bitis_tarihi")
    private LocalDate bitisTarihi;

    // Şu anki aktif amiri bulmak için kolaylaştırıcı bir bayrak (flag)
    @Column(name = "aktif_mi", nullable = false)
    private boolean aktifMi = true;
}
