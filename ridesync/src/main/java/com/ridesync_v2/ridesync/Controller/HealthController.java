package com.ridesync_v2.ridesync.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping
    public ResponseEntity<String> healthCheck(){
        return new ResponseEntity<>("System up & running ... "+ new Date().toString(), HttpStatus.OK);
    }
}
