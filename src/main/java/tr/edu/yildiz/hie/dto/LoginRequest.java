package tr.edu.yildiz.hie.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String eposta;
    private String sifre;
}
