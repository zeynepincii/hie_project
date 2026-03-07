package tr.edu.yildiz.hie.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tr.edu.yildiz.hie.entity.PersonelAmirTarihcesi;

@Repository
public interface PersonelAmirTarihcesiRepository extends JpaRepository<PersonelAmirTarihcesi, Long> {

    // Service katmanında kullandığımız o meşhur güvenlik kontrolü metodu:
    // Bu amir, bu personelin şu anki (aktif) amiri mi?
    boolean existsByAmirIdAndPersonelIdAndAktifMiTrue(Long amirId, Long personelId);
}
