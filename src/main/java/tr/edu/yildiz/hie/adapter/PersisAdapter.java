package tr.edu.yildiz.hie.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tr.edu.yildiz.hie.dto.PersisPersonelDTO;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component // Spring'in bu sınıfı bir "Bean" olarak yönetmesi için
public class PersisAdapter {
    // Senin tarayıcının ağ sekmesinde yakaladığın o meşhur adres
    private final String PERSIS_URL = "https://persis.yildiz.edu.tr/persisweb/project2.dll/HandleEvent";
    private final RestTemplate restTemplate;

    public PersisAdapter() {
        // RestTemplate'i başlatıyoruz
        this.restTemplate = new RestTemplate();
    }

    public List<PersisPersonelDTO> tumPersonelleriGetir() {
        log.info("Persis sistemine HTTP POST isteği atılıyor...");
        List<PersisPersonelDTO> personelListesi = new ArrayList<>();

        try {
            // 1. İstek Başlıklarını (Headers) Ayarla
            HttpHeaders headers = new HttpHeaders();
            // Legacy sistemler genelde JSON yerine Form-Data veya XML bekleyebilir.
            // Ağ sekmesindeki "Content-Type" değerine göre burayı güncelleyebiliriz.
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 2. İstek Gövdesini (Body/Payload) Hazırla
            // DİKKAT: .dll sisteminin tam olarak hangi parametreleri beklediğini (örn: islem=listeGetir)
            // tarayıcının ağ (network) sekmesindeki 'Payload' kısmından bakarak buraya yazmalısın.
            String requestBody = "parametre1=deger1&parametre2=deger2";

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            // 3. İsteği At ve Cevabı Al
            ResponseEntity<String> response = restTemplate.exchange(
                    PERSIS_URL,
                    HttpMethod.POST, // Senin tespit ettiğin POST metodu
                    requestEntity,
                    String.class // Gelecek cevabı şimdilik ham String olarak alıyoruz
            );

            // 4. Cevabı Kontrol Et ve İşle
            if (response.getStatusCode() == HttpStatus.OK) { // 200 OK durumu
                String hamVeri = response.getBody();
                log.info("Persis'ten 200 OK yanıtı alındı. Veri uzunluğu: {}", hamVeri != null ? hamVeri.length() : 0);

                // TODO: Gelen bu hamVeri'yi (JSON veya XML formatındadır) parse edip,
                // PersisPersonelDTO nesnelerine dönüştürüp personelListesi'ne ekleyeceğiz.
                // Jackson kütüphanesinin ObjectMapper sınıfı bu iş için biçilmiş kaftandır.

            } else {
                log.error("Persis sistemi hata döndürdü. HTTP Status: {}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Persis sistemine bağlanırken ağ veya zaman aşımı hatası oluştu: ", e);
            // Burada hata fırlatmayıp boş liste dönüyoruz ki kendi sistemimiz çökmeyip çalışmaya devam etsin
        }

        return personelListesi; // Temizlenmiş ve bizim dilimize çevrilmiş liste!
    }
}
