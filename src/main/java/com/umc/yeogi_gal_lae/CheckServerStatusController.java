package com.umc.yeogi_gal_lae;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class CheckServerStatusController {
    @GetMapping("/health")
    public String healthCheck() {
        return LocalDateTime.now().toString();
    }
}
