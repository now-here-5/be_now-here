package com.now_here5.now_here.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HealthController {
    @GetMapping("/health")
    ResponseEntity<String> checkHealth(){
        return ResponseEntity.ok("healthy");
    }
}
