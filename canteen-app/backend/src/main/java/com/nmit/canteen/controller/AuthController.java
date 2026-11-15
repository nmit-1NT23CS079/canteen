package com.nmit.canteen.controller;

import com.nmit.canteen.model.User;
import com.nmit.canteen.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    public record RegisterRequest(@NotBlank String name, @Email @NotBlank String email, @Size(min = 6) String password) {}
    public record LoginRequest(@NotBlank String email, @NotBlank String password) {}

    private final AuthService auth;
    public AuthController(AuthService auth) { this.auth = auth; }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@Valid @RequestBody RegisterRequest r) {
        return auth.register(r.name(), r.email(), r.password());
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest r) {
        String token = auth.login(r.email(), r.password());
        User u = auth.requireUser("Bearer " + token);
        return Map.of("token", token, "user", u);
    }
}
