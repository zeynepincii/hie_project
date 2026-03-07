package tr.edu.yildiz.hie.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.edu.yildiz.hie.dto.PersisPersonelDTO;
import tr.edu.yildiz.hie.entity.Personel;
import tr.edu.yildiz.hie.entity.Rol;
import tr.edu.yildiz.hie.repository.PersonelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j // Loglama yapmak için
@Service
@RequiredArgsConstructor
public class PersonelSenkronizasyonService {
    private final PersonelRepository personelRepository;
    // private final PersisAdapter persisAdapter; --> Bunu bir sonraki adımda yazacağız

    /**
     * Her gece saat 03:00'te otomatik çalışacak zamanlanmış görev (Cron Job).
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void persisVerileriniSenkronizeEt() {
        log.info("Persis senkronizasyonu başlatılıyor...");

        try {
            // 1. ADIM: PersisAdapter üzerinden kurumdaki tüm güncel personelleri çek
            // List<PersisPersonelDTO> guncelListe = persisAdapter.tumPersonelleriGetir();

            // 2. ADIM: Kendi veritabanımızdaki mevcut aktif personelleri çek
            // List<Personel> mevcutPersoneller = personelRepository.findByAktifMiTrue();

            // 3. ADIM: İki listeyi sicilNo üzerinden karşılaştır (Ekle, Güncelle, Pasife Çek)
            // karsilastirVeGuncelle(guncelListe, mevcutPersoneller);

            log.info("Persis senkronizasyonu başarıyla tamamlandı.");
        } catch (Exception e) {
            log.error("Senkronizasyon sırasında kritik bir hata oluştu: ", e);
            // Burada gerekirse sistem yöneticisine acil bir mail atacak kodu tetikleyebiliriz
        }
    }

    // İş mantığını böleceğimiz private metodlar buraya gelecek...
    // PersonelSenkronizasyonService.java içine eklenecek private metod

    private void karsilastirVeGuncelle(List<PersisPersonelDTO> persistenGelenler, List<Personel> mevcutPersoneller) {

        // 1. Mevcut personelleri hızlı arama için HashMap'e çeviriyoruz (Key: Sicil No, Value: Personel nesnesi)
        Map<String, Personel> mevcutPersonelMap = mevcutPersoneller.stream()
                .collect(Collectors.toMap(Personel::getSicilNo, p -> p));

        List<Personel> kaydedilecekler = new ArrayList<>();

        // 2. Persis'ten gelen güncel listeyi dönüyoruz
        for (PersisPersonelDTO gelen : persistenGelenler) {
            Personel mevcut = mevcutPersonelMap.get(gelen.getSicilNo());

            if (mevcut != null) {
                // DURUM 1: Personel bizde zaten var. Güncelleme var mı kontrol et.
                if (!mevcut.getUnvan().equals(gelen.getUnvan())) {
                    mevcut.setUnvan(gelen.getUnvan());
                    kaydedilecekler.add(mevcut);
                }
                // İşlem bittiği için map'ten çıkarıyoruz.
                mevcutPersonelMap.remove(gelen.getSicilNo());
            } else {
                // DURUM 2: Personel bizde yok. Kuruma yeni girmiş, yeni kayıt oluştur.
                Personel yeniPersonel = Personel.builder()
                        .sicilNo(gelen.getSicilNo())
                        .ad(gelen.getAd())
                        .soyad(gelen.getSoyad())
                        .unvan(gelen.getUnvan())
                        .eposta(gelen.getEposta()) // DTO'dan geldiğini varsayıyoruz
                        .rol(Rol.PERSONEL) // Varsayılan olarak personel atanır
                        .aktifMi(true)
                        .build();
                kaydedilecekler.add(yeniPersonel);
            }
        }

        // DURUM 3: Döngü bittikten sonra Map'te kalanlar Persis'te artık olmayanlardır (Ayrılanlar).
        for (Personel ayrilanPersonel : mevcutPersonelMap.values()) {
            ayrilanPersonel.setAktifMi(false);
            kaydedilecekler.add(ayrilanPersonel);
        }

        // 3. Tüm değişiklikleri tek seferde (Batch Update) veritabanına kaydet.
        personelRepository.saveAll(kaydedilecekler);
        log.info("Senkronizasyon: {} adet personel kaydı eklendi/güncellendi.", kaydedilecekler.size());
    }
}
