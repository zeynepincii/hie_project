package tr.edu.yildiz.hie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.yildiz.hie.entity.*;
import tr.edu.yildiz.hie.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EgitimAtamaService {
    private final PersonelEgitimAtamaRepository atamaRepository;
    private final EgitimRepository egitimRepository;
    private final PersonelRepository personelRepository;
    private final AtamaDurumKataloguRepository durumKataloguRepository;
    private final PersonelAmirTarihcesiRepository amirTarihcesiRepository;

    /**
     * SENARYO 1: Birim Amiri, kendi personellerine eğitim atar.
     */
    @Transactional
    public void personeleEgitimAta(Long amirId, Long egitimId, List<Long> personelIdList) {
        log.info("Amir ID: {} tarafından Eğitim ID: {} için atama işlemi başlatıldı.", amirId, egitimId);

        // 1. Eğitimi ve 'ATANDI' durumunu veritabanından bul
        Egitim egitim = egitimRepository.findById(egitimId)
                .orElseThrow(() -> new RuntimeException("Eğitim bulunamadı!"));

        AtamaDurumKatalogu atandiDurumu = durumKataloguRepository.findByDurumKodu("ATANDI")
                .orElseThrow(() -> new RuntimeException("Sistem hatası: ATANDI durumu katalogda yok!"));

        for (Long personelId : personelIdList) {
            // 2. GÜVENLİK KONTROLÜ (Imperative Yaklaşım): Bu personel gerçekten bu amirin altında mı çalışıyor?
            boolean yetkisiVarMi = amirTarihcesiRepository.existsByAmirIdAndPersonelIdAndAktifMiTrue(amirId, personelId);

            if (!yetkisiVarMi) {
                log.warn("GÜVENLİK İHLALİ: Amir ID: {}, kendi biriminde olmayan Personel ID: {} için işlem yapmaya çalıştı!", amirId, personelId);
                // İsteğe bağlı olarak burada işlemi tamamen kesebilir (throw exception) veya o personeli atlayabiliriz.
                continue;
            }

            // 3. Persis'ten çekilen "İzinli mi?" kontrolü buraya gelecek
            // Eğer personel o tarihte izinliyse atama yapılmayacak (İsterlerdeki kritik kural)

            Personel personel = personelRepository.findById(personelId).orElseThrow();

            // 4. Atama kaydını oluştur
            PersonelEgitimAtama yeniAtama = PersonelEgitimAtama.builder()
                    .personel(personel)
                    .egitim(egitim)
                    .durum(atandiDurumu)
                    .atamaTarihi(LocalDateTime.now())
                    .build();

            atamaRepository.save(yeniAtama);
        }

        log.info("Atama işlemi başarıyla tamamlandı.");
    }

    /**
     * SENARYO 2: Personel kendisine atanan eğitime mazeret bildirir.
     */
    @Transactional
    public void mazeretBildir(Long personelId, Long atamaId, String gerekce) {
        // 1. Atama kaydını bul
        PersonelEgitimAtama atama = atamaRepository.findById(atamaId)
                .orElseThrow(() -> new RuntimeException("Atama kaydı bulunamadı!"));

        // 2. GÜVENLİK KONTROLÜ: Bu atama gerçekten bu personele mi ait?
        if (!atama.getPersonel().getId().equals(personelId)) {
            throw new RuntimeException("Yetkisiz işlem: Sadece kendi eğitimlerinize mazeret bildirebilirsiniz.");
        }

        // 3. Durumu "MAZERET_BILDIRDI" (veya ONAY_BEKLIYOR) olarak güncelle
        AtamaDurumKatalogu mazeretDurumu = durumKataloguRepository.findByDurumKodu("MAZERET_BILDIRDI")
                .orElseThrow();

        atama.setDurum(mazeretDurumu);
        atama.setMazeretGerekcesi(gerekce);

        atamaRepository.save(atama);
        log.info("Personel ID: {} mazeret bildirdi. Gerekçe: {}", personelId, gerekce);
    }
}
