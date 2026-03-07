package tr.edu.yildiz.hie.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    @GetMapping("/merhaba")
    public ResponseEntity<String> merhabaDe() {
        return ResponseEntity.ok("Harika! Geçerli bir biletin var ve kilitli kapıdan içeri girdin.");
    }
}
