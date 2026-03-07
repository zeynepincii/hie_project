package tr.edu.yildiz.hie.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.edu.yildiz.hie.dto.EgitimAtamaRequestDTO;
import tr.edu.yildiz.hie.service.EgitimAtamaService;

import org.springframework.web.bind.annotation.*;
import tr.edu.yildiz.hie.entity.Egitim;
import tr.edu.yildiz.hie.repository.EgitimRepository;

@CrossOrigin(origins = "http://localhost:5173") // React'ten gelecek isteklere izin veriyoruz

@Slf4j
@RestController
@RequestMapping("/api/v1/egitimler") // Endüstri standardı versiyonlamalı URL
@RequiredArgsConstructor
public class EgitimController {

    private final EgitimAtamaService egitimAtamaService;

    // YENİ EKLENEN BAĞIMLILIK: Doğrudan eğitimi kaydetmek için
    private final EgitimRepository egitimRepository;

    /**
     * UÇ NOKTA 0: Admin'in yeni bir eğitim tanımlaması (POST)
     * Örnek URL: POST http://localhost:8080/api/v1/egitimler
     */
    @PostMapping
    public ResponseEntity<Egitim> egitimOlustur(@RequestBody Egitim egitim) {
        log.info("API İsteği Alındı: Yeni eğitim oluşturuluyor - Adı: {}", egitim.getEgitimAdi());
        Egitim kaydedilenEgitim = egitimRepository.save(egitim);
        return ResponseEntity.ok(kaydedilenEgitim);
    }

    /**
     * UÇ NOKTA 1: Birim Amirinin personellerine eğitim ataması yapması (POST)
     */
    @PostMapping("/atama")
    public ResponseEntity<String> personeleEgitimAta(@RequestBody EgitimAtamaRequestDTO request) {
        log.info("API İsteği Alındı: Eğitim ataması - Eğitim ID: {}", request.getEgitimId());
        egitimAtamaService.personeleEgitimAta(request.getAmirId(), request.getEgitimId(), request.getPersonelIdList());
        return ResponseEntity.ok("Eğitim atama işlemi başarıyla tamamlandı.");
    }

    /**
     * UÇ NOKTA 2: Personelin atandığı eğitime mazeret bildirmesi (POST)
     */
    @PostMapping("/atama/{atamaId}/mazeret")
    public ResponseEntity<String> mazeretBildir(
            @PathVariable Long atamaId,
            @RequestParam Long personelId,
            @RequestParam String gerekce) {
        log.info("API İsteği Alındı: Mazeret bildirimi - Atama ID: {}", atamaId);
        egitimAtamaService.mazeretBildir(personelId, atamaId, gerekce);
        return ResponseEntity.ok("Mazeret başarıyla sisteme kaydedildi ve onaya sunuldu.");
    }
}
