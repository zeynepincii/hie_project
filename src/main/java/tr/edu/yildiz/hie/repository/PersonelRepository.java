package tr.edu.yildiz.hie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.yildiz.hie.entity.Personel;

import java.util.Optional;
import java.util.List;

@Repository
public interface PersonelRepository extends JpaRepository<Personel, Long> {

    // 1. PERSİS Entegrasyonu İçin: Sicil numarasına göre personeli bulur.
    // Dış sistemden gelen veriyi kendi sistemimizdeki kişiyle eşleştirirken hayat kurtaracak.
    Optional<Personel> findBySicilNo(String sicilNo);

    // 2. Giriş (Login) İşlemleri İçin: E-posta adresiyle kullanıcıyı bulur.
    Optional<Personel> findByEposta(String eposta);

    // 3. İş Kuralı (Business Rule): Sadece hala kurumda çalışan (aktif) personelleri getirir.
    // İşten ayrılanlara (aktifMi = false) yanlışlıkla eğitim atanmasını engelleriz.
    List<Personel> findByAktifMiTrue();

}