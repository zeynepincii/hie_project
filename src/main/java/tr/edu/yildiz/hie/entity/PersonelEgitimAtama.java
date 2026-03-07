package tr.edu.yildiz.hie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "personel_egitim_atamalari")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PersonelEgitimAtama {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hangi Personel?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personel_id", nullable = false)
    private Personel personel;

    // Hangi Eğitim?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "egitim_id", nullable = false)
    private Egitim egitim;

    // Mevcut Durumu Ne? (Atandı, Mazeretli vs.)
    @ManyToOne(fetch = FetchType.EAGER) // Durumu anında görmek isteriz genelde
    @JoinColumn(name = "durum_id", nullable = false)
    private AtamaDurumKatalogu durum;

    // Eğer mazeret bildirdiyse açıklaması buraya gelecek
    @Column(name = "mazeret_gerekcesi", length = 500)
    private String mazeretGerekcesi;

    @Column(name = "atama_tarihi", nullable = false)
    private LocalDateTime atamaTarihi = LocalDateTime.now();
}
