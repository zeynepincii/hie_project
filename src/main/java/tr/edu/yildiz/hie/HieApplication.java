package tr.edu.yildiz.hie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling  // Zamanlanmış görevlerin (Scheduler) çalışması için
@EnableAsync //(Asenkron mailler için)
@SpringBootApplication
public class HieApplication {

	public static void main(String[] args) {
		SpringApplication.run(HieApplication.class, args);
	}

	@Bean
	public org.springframework.boot.CommandLineRunner testKullanicisiOlustur(
			tr.edu.yildiz.hie.repository.PersonelRepository repository,
			org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {

		return args -> {
			// 1. PERSONEL (Zeynep)
			if (repository.findByEposta("zeynep@ytu.edu.tr").isEmpty()) {
				repository.save(tr.edu.yildiz.hie.entity.Personel.builder()
						.ad("Zeynep").soyad("İnci").eposta("zeynep@ytu.edu.tr")
						.sifre(passwordEncoder.encode("123456")).sicilNo("25011606")
						.unvan("Bilgisayar Mühendisi").aktifMi(true)
						.rol(tr.edu.yildiz.hie.entity.Rol.PERSONEL).build());
			}

			// 2. BİRİM AMİRİ (Örnek Şube Müdürü)
			if (repository.findByEposta("amir@ytu.edu.tr").isEmpty()) {
				repository.save(tr.edu.yildiz.hie.entity.Personel.builder()
						.ad("Ahmet").soyad("Yılmaz").eposta("amir@ytu.edu.tr")
						.sifre(passwordEncoder.encode("123456")).sicilNo("100200")
						.unvan("Şube Müdürü").aktifMi(true)
						.rol(tr.edu.yildiz.hie.entity.Rol.BIRIM_AMIRI).build());
			}

			// 3. ADMIN (Personel Daire Başkanlığı Yetkilisi)
			if (repository.findByEposta("admin@ytu.edu.tr").isEmpty()) {
				repository.save(tr.edu.yildiz.hie.entity.Personel.builder()
						.ad("Mehmet").soyad("Başkan").eposta("admin@ytu.edu.tr")
						.sifre(passwordEncoder.encode("123456")).sicilNo("100100")
						.unvan("Daire Başkanı").aktifMi(true)
						.rol(tr.edu.yildiz.hie.entity.Rol.ADMIN).build());
			}

		};
	}
	}

