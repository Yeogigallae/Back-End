package com.umc.yeogi_gal_lae;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController {

    @GetMapping("/")
    public String home() {
        return "forward:/static/index.html"; // index.html 을 서빙
    }
}
