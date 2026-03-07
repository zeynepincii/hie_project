package tr.edu.yildiz.hie.dto;

import lombok.Data;
import java.util.List;

@Data
public class EgitimAtamaRequestDTO {
    private Long amirId; // İleride bunu JWT token içinden alacağız, şimdilik React'ten gelsin
    private Long egitimId;
    private List<Long> personelIdList;
}
