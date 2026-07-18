package com.ridesync_v2.ridesync.Controller;

import java.util.*;
import com.ridesync_v2.ridesync.Entity.User;
import com.ridesync_v2.ridesync.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> verify(@Valid @RequestBody User user){
        String response = userService.verify(user);
        if(response.equals("Verified")){
            return new ResponseEntity<>(response,HttpStatus.FOUND);
        }
        return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
    }
}
