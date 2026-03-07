package tr.edu.yildiz.hie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.yildiz.hie.entity.Egitim;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EgitimRepository extends JpaRepository<Egitim, Long>{
    // Anketleri aktif edilmiş eğitimleri bul (Mail atılacakları tespit etmek için)
    List<Egitim> findByAnketAktifMiTrue();

    // Belli bir tarih aralığındaki eğitimleri getir
    List<Egitim> findByBaslangicTarihiBetween(LocalDateTime baslangic, LocalDateTime bitis);
}
