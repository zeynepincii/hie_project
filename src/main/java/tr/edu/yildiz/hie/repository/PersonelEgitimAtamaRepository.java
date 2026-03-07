package tr.edu.yildiz.hie.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.yildiz.hie.entity.PersonelEgitimAtama;

import java.util.List;

@Repository
public interface PersonelEgitimAtamaRepository extends JpaRepository<PersonelEgitimAtama, Long> {
    // 1. Personelin Kendi Ekranı: Bana atanmış tüm eğitimleri getir
    List<PersonelEgitimAtama> findByPersonelId(Long personelId);

    // 2. Daire Başkanı Ekranı: Bu eğitime kimler atanmış?
    List<PersonelEgitimAtama> findByEgitimId(Long egitimId);

    // 3. İş Kuralı: Belirli bir personelin, "ONAY_BEKLIYOR" veya "MAZERETLI" olan atamalarını getir
    List<PersonelEgitimAtama> findByPersonelIdAndDurum_DurumKodu(Long personelId, String durumKodu);

    // Sadece belirli bir duruma (örneğin "ATANDI") sahip olan tüm atamaları getir
    List<PersonelEgitimAtama> findByDurum_DurumKodu(String durumKodu);
}
