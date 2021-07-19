package com.github.glorindor.priconneserverbackend.player;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/player")
public class PlayerController {

    @GetMapping("/hello")
    @ResponseStatus(HttpStatus.OK)
    public String greet() {
        return "Hello";
    }
}
