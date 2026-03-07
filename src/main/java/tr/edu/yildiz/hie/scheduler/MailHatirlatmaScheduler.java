package tr.edu.yildiz.hie.scheduler;


// import com.proje.hizmetici.service.EmailService; -> Bunu bir sonraki adımda yazacağız
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tr.edu.yildiz.hie.entity.PersonelEgitimAtama;
import tr.edu.yildiz.hie.repository.PersonelEgitimAtamaRepository;
import tr.edu.yildiz.hie.service.EmailService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailHatirlatmaScheduler {
    private final PersonelEgitimAtamaRepository atamaRepository;
    private final EmailService emailService;

    /**
     * KURAL: Her gün sabah saat 08:00'de çalışır.
     * Amacı: Durumu "ATANDI" olan (henüz katılmamış veya mazeret girmemiş) personelleri bulup
     * onlara yaklaşan eğitimleri için otomatik hatırlatma maili atmaktır.
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void egitimeKatilmayanlaraHatirlatmaMailiAt() {
        log.info("Zamanlanmış Görev Başladı: Günlük eğitim hatırlatma mailleri taranıyor...");

        try {
            // 1. Durumu sadece "ATANDI" olan atamaları bul.
            // (Eğer "KATILDI" veya "MAZERETLI" ise onlara mail atmamalıyız)
            List<PersonelEgitimAtama> bekleyenAtamalar =
                    atamaRepository.findByDurum_DurumKodu("ATANDI");

            int gonderilenMailSayisi = 0;

            // 2. Bulunan kayıtları tek tek gez
            for (PersonelEgitimAtama atama : bekleyenAtamalar) {

                // Sadece gelecekteki eğitimler için hatırlatma atıyoruz
                if (atama.getEgitim().getBaslangicTarihi().isAfter(LocalDateTime.now())) {

                    String aliciMail = atama.getPersonel().getEposta();
                    String egitimAdi = atama.getEgitim().getEgitimAdi();

                    // 3. Mail gönderme servisini çağır
                    emailService.hatirlatmaMailiGonder(aliciMail, egitimAdi, atama.getEgitim().getBaslangicTarihi());

                    log.info("Hatırlatma Maili Gönderildi -> Kime: {}, Eğitim: {}", aliciMail, egitimAdi);
                    gonderilenMailSayisi++;
                }
            }

            log.info("Zamanlanmış Görev Tamamlandı: Toplam {} adet hatırlatma maili başarıyla kuyruğa iletildi.", gonderilenMailSayisi);

        } catch (Exception e) {
            log.error("Hatırlatma mailleri gönderilirken kritik bir hata oluştu!", e);
        }
    }
}
