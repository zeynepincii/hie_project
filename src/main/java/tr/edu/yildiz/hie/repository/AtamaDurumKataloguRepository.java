package tr.edu.yildiz.hie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.yildiz.hie.entity.AtamaDurumKatalogu;

import java.util.Optional;

@Repository
public interface AtamaDurumKataloguRepository extends JpaRepository<AtamaDurumKatalogu, Long> {
        // Enum mantığını veritabanında esnekçe kullanmak için durum koduna göre arama
        Optional<AtamaDurumKatalogu> findByDurumKodu(String durumKodu);
}
