package tr.edu.yildiz.hie.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "atama_durum_katalogu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AtamaDurumKatalogu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // "ATANDI", "KATILDI", "MAZERETLI" gibi kodlar
    @Column(name = "durum_kodu", nullable = false, unique = true)
    private String durumKodu;

    // Kullanıcı arayüzünde görünecek açıklama
    @Column(name = "aciklama", nullable = false)
    private String aciklama;
}
