package tr.edu.yildiz.hie.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    /**
     * @Async anotasyonu, bu metodun ana akışı beklemeden ayrı bir iş parçacığında (Thread)
     * çalışmasını sağlar. Böylece for döngüsü maillerin gitmesini beklemez, anında döner.
     */
    @Async
    public void hatirlatmaMailiGonder(String aliciMail, String egitimAdi, LocalDateTime tarih) {
        log.info("Mail gönderimi başlatılıyor -> Alıcı: {}", aliciMail);

        try {
            // Tarihi daha okunaklı bir formata çeviriyoruz
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            String formatliTarih = tarih.format(formatter);

            SimpleMailMessage mesaj = new SimpleMailMessage();
            mesaj.setFrom("hizmetici-sistem@kurum.edu.tr"); // application.properties'deki mail ile uyumlu olmalı
            mesaj.setTo(aliciMail);
            mesaj.setSubject("📌 HATIRLATMA: Yaklaşan Hizmet İçi Eğitim");
            mesaj.setText("Değerli Personelimiz,\n\n" +
                    "Sisteme tanımlı olan '" + egitimAdi + "' konulu eğitiminiz " +
                    formatliTarih + " tarihinde gerçekleştirilecektir.\n" +
                    "Eğitime katılımınız kurum standartları gereği zorunludur. Eğer katılamayacak bir " +
                    "durumunuz varsa lütfen sistem üzerinden amirinize mazeret bildiriniz.\n\n" +
                    "İyi çalışmalar dileriz.\n" +
                    "Personel Daire Başkanlığı");

            mailSender.send(mesaj);
            log.info("✅ Mail başarıyla iletildi: {}", aliciMail);

        } catch (Exception e) {
            log.error("❌ Mail gönderiminde hata oluştu! Alıcı: {} - Sebep: {}", aliciMail, e.getMessage());
            // Gerçek kurumsal yapıda burada hataya düşen mailleri ayrı bir veritabanı tablosuna yazarız ki sonra tekrar deneyebilelim.
        }
    }
}
