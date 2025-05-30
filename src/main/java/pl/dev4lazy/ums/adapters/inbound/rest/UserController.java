package pl.dev4lazy.ums.adapters.inbound.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {
    @GetMapping("/")
    public Map<String,String> home() {
        return Map.of("status", "OK");
    }
}