package tr.edu.yildiz.hie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersisPersonelDTO {
    // Persis'ten gelecek olan ve bizim sistemimizde eşleşecek veriler
    private String sicilNo;
    private String ad;
    private String soyad;
    private String unvan;
    private String eposta;
}
